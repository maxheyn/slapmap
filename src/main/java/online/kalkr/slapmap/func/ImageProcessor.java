package online.kalkr.slapmap.func;

import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.map.MapState;
import net.minecraft.server.world.ServerWorld;
import online.kalkr.slapmap.Slapmap;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.net.URL;
import java.net.URLConnection;

public class ImageProcessor {

    private int width;
    private int height;
    private float[][] pixels;
    private int[] colorIds;

    private static final int[][] mcColorArray = initColorArray();

    private static int[][] initColorArray() {
        final int[][] mcColors = {
                {127,178,56},{247,233,163},{199,199,199},{255,0,0},{160,160,255},{167,167,167},{0,124,0},
                {255,255,255},{164,168,184},{151,109,77},{112,112,112},{64,64,255},{143,119,72},{255,252,245},
                {216,127,51},{178,76,216},{102,153,216},{229,229,51},{127,204,25},{242,127,165},{76,76,76},
                {153,153,153},{76,127,153},{127,63,178},{51,76,178},{102,76,51},{102,127,51},{153,51,51},
                {25,25,25},{250,238,77},{92,219,213},{74,128,255},{0,217,58},{129,86,49},{112,2,0},
                {209,177,161},{159,82,36},{149,87,108},{112,108,138},{186,133,36},{103,117,53},{160,77,78},
                {57,41,35},{135,107,98},{87,92,92},{122,73,88},{76,62,92},{76,50,35},{76,82,42},{142,60,46},
                {37,22,16},{189,48,49},{148,63,97},{92,25,29},{22,126,134},{58,142,140},{86,44,62},{20,180,133}
        };
        final double[] minecraftShades = { 180.0/255, 220.0/255, 1.0, 135.0/255 };

        int[][] mcColorsFinal = new int[mcColors.length*4][3];

        int i = 0;
        for (int[] color:mcColors) {
            for (double shade:minecraftShades) {
                mcColorsFinal[i][0] = (int) Math.floor(color[0]*shade);
                mcColorsFinal[i][1] = (int) Math.floor(color[1]*shade);
                mcColorsFinal[i++][2] = (int) Math.floor(color[2]*shade);
            }
        }

        return mcColorsFinal;
    }

    public boolean fromUrl(String urlString, int reqwidth, int reqheight) {
        BufferedImage urlimage;

        try {
            URL url = new URL(urlString);
            url.toURI();

            URLConnection connection = url.openConnection();
            connection.connect();
            urlimage = ImageIO.read(connection.getInputStream());

        } catch (Exception e) { return false; }
        if (urlimage == null) return false;

        width = urlimage.getWidth();
        height = urlimage.getHeight();

        // not simplifying this until i know people are satisfied with the scaling system
        // -1 scale
        //  0 native
        // >0 value

        if (reqwidth == -1) {
            if (reqheight == -1) {
                int scww = ((int) Math.ceil(((double) width) / 128)) * 128;
                int scwh = (int) ((((double) scww) / ((double) width)) * height);
                int scwsq = ((int) Math.ceil(((double) scww)/128)) * ((int) Math.ceil(((double) scwh)/128));

                int schh = ((int) Math.ceil(((double) height) / 128)) * 128;
                int schw = (int) ((((double) schh) / ((double) height)) * width);
                int schsq = ((int) Math.ceil(((double) schh)/128)) * ((int) Math.ceil(((double) schw)/128));

                width = schw;
                height = schh;
                if (scwsq < schsq) {
                    width = scww;
                    height = scwh;
                }
            }
            if (reqheight > 0) {
                int schh = reqheight * 128;
                width = (int) ((((double) schh) / ((double) height)) * width);
                height = schh;
            }
        }
        if (reqwidth == 0) {
            if (reqheight > 0) {
                height = reqheight * 128;
            }
        }
        if (reqwidth > 0) {
            if (reqheight == -1) {
                int scww = reqwidth * 128;
                height = (int) ((((double) scww) / ((double) width)) * height);
                width = scww;
            }
            if (reqheight > 0) {
                width = reqwidth * 128;
                height = reqheight * 128;
            }
        }

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D graphics2D = image.createGraphics();
        graphics2D.drawImage(urlimage, 0, 0, width, height, null);
        graphics2D.dispose();



        byte[] colorData = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();

        int channels = image.getColorModel().hasAlpha() ? 4 : 3;
        pixels = new float[colorData.length/channels][3];

        for (int i = channels-3; i < colorData.length; i+=channels) {
            pixels[i/channels] = new float[]{
                    colorData[i+2] & 0xff,
                    colorData[i+1] & 0xff,
                    colorData[i] & 0xff,
            };
        }

        return true;
    }

    public boolean dither() {
        colorIds = new int[pixels.length];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {

                float[] oldpixel = pixels[x+y*width];
                int colorId = reduceColor(oldpixel);

                pixels[x+y*width] = new float[]{
                        (float) mcColorArray[colorId][0],
                        (float) mcColorArray[colorId][1],
                        (float) mcColorArray[colorId][2],
                };
                colorIds[x+y*width] = colorId + 4;

                float[] difference = find_difference(oldpixel, pixels[x+y*width]);
                if(x+1<width) add_difference(x+1+y*width, difference, 7);
                if(y+1>=height) continue;
                if(x>0) add_difference(x-1+(y+1)*width, difference, 3);
                add_difference(x+(y+1)*width, difference, 5);
                if(x+1<width) add_difference(x+1+(y+1)*width, difference, 1);
            }
        }

        return true;
    }

    private static int reduceColor(float[] pixel) {
        float lowest = Float.MAX_VALUE;
        int colorId = 0;
        for (int i = 0; i < mcColorArray.length; i++) {
            float dist = Math.abs(pixel[0] - mcColorArray[i][0]) +
                    Math.abs(pixel[1] - mcColorArray[i][1]) +
                    Math.abs(pixel[2] - mcColorArray[i][2]) ;
            if (dist<lowest) {
                colorId = i;
                lowest = dist;
            }
        }
        return colorId;
    };

    private float[] find_difference (float[] original, float[] updated) {
        return new float[]{
                (float) ((original[0]-updated[0]) * 0.6),
                (float) ((original[1]-updated[1]) * 0.6),
                (float) ((original[2]-updated[2]) * 0.6),
        };
    }

    private void add_difference (int index, float[] diff, float factor) {
        pixels[index][0] += diff[0] * factor / 16;
        pixels[index][1] += diff[1] * factor / 16;
        pixels[index][2] += diff[2] * factor / 16;
    }

    public boolean toMaps(String imageName, String alignx, String aligny, ServerWorld world) {
        int mapsWidth = (int) Math.ceil(((double) width)/128);
        int mapsHeight = (int) Math.ceil(((double) height)/128);

        int shiftRight = (mapsWidth * 128 - width);
        if (alignx.equals("center")) {
            shiftRight /= 2;
        }
        if (alignx.equals("left")) {
            shiftRight = 0;
        }

        int shiftDown = (mapsHeight * 128 - height);
        if (aligny.equals("center")) {
            shiftDown /= 2;
        }
        if (aligny.equals("top")) {
            shiftDown = 0;
        }

        Integer[] stacks = new Integer[mapsWidth*mapsHeight];

        for (int mapy = 0; mapy < mapsHeight; mapy++) {
            for (int mapx = 0; mapx < mapsWidth; mapx++) {

                ItemStack stack = FilledMapItem.createMap(world, 0, 0, (byte) 3, false, false);
                MapState state = FilledMapItem.getMapState(stack, world);
                state.locked = true;

                for (int y = 0; y < 128; y++) {
                    for (int x = 0; x < 128; x++) {
                        int colorIndex = (x + (y - shiftDown) * width - shiftRight) + (mapx * 128) + (mapy * width * 128);
                        if (colorIndex >= (mapy * 128 + (y - shiftDown)) * width && (mapx * 128) + x < width + shiftRight && colorIndex >= 0 && colorIndex < colorIds.length) {
                            state.colors[x + y * 128] = (byte) colorIds[colorIndex];
                        } else {
                            state.colors[x + y * 128] = (byte) 0;
                        }
                    }
                }
                stacks[mapx + (mapsHeight-mapy-1) * mapsWidth] = FilledMapItem.getMapId(stack);;
            }
        }

        Slapmap.mapManager.add(imageName, mapsWidth, mapsHeight, stacks, true);
        return true;
    }
}
