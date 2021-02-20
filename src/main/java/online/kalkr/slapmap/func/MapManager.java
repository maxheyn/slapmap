package online.kalkr.slapmap.func;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.WorldSavePath;

import java.io.*;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;

public class MapManager {

    private final Hashtable<String, ImageData> store = new Hashtable<String, ImageData>();
    private final String slapDataPath;


    private static class ImageData {
        public int width;
        public int height;
        public Integer[] mapIds;

        public ImageData(int width, int height, Integer[] mapIds) {
            this.width = width;
            this.height = height;
            this.mapIds = mapIds;
        }
    }


    public MapManager(MinecraftServer server) {
        slapDataPath = (server.isDedicated() ? "world/." : server.getSavePath(WorldSavePath.ROOT).toString().split("\\./")[1]) + "/data/_slap.dat";

        StringBuilder fileString = new StringBuilder();
        try {
            Reader reader = new FileReader(slapDataPath);
            int data = reader.read();
            while (data != -1) {
                fileString.append((char) data);
                data = reader.read();
            }
            reader.close();
        } catch (IOException e) { return; }
        String[] lineArray = fileString.toString().split("\n");

        for (String line:lineArray) {
            String[] items = line.split(" ");

            String key = items[0];
            int width = Integer.parseInt(items[1]);
            int height = Integer.parseInt(items[2]);

            Integer[] mapIds = new Integer[items.length - 3];
            for (int h = 3; h < items.length; h++) {
                mapIds[h - 3] = Integer.parseInt(items[h]);
            }

            this.add(key, width, height, mapIds, false);
        }
    }


    public void add(String key, int width, int height, Integer[] mapIds, boolean writeToFile) {
        store.put(key, new ImageData(width, height, mapIds));

        if (!writeToFile) { return; }

        StringBuilder mapIdStr = new StringBuilder();
        for (int mapId:mapIds) {
            mapIdStr.append(mapId).append(" ");
        }

        try {
            PrintWriter out = new PrintWriter(new FileWriter(slapDataPath, true));
            out.append(key + " " + width + " " + height + " " + mapIdStr + "\n");
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public String[] list() {
        String[] keyArray = new String[store.size()];

        Enumeration<String> keys = store.keys();

        int i = 0;
        while (keys.hasMoreElements()) {
            keyArray[i++] = keys.nextElement();
        }
        Arrays.sort(keyArray);

        return keyArray;
    }


    public boolean has(String key) {
        return store.containsKey(key);
    }


    public int getWidth(String key) {
        return store.get(key).width;
    }


    public int getHeight(String key) {
        return store.get(key).height;
    }


    public Integer[] getMaps(String key) {
        return store.get(key).mapIds;
    }
}
