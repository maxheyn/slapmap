package online.kalkr.slapmap.action.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;
import online.kalkr.slapmap.Slapmap;
import online.kalkr.slapmap.func.ImageProcessor;

import java.util.Arrays;

public class Import {

    public static int slap(CommandContext<ServerCommandSource> c) throws CommandSyntaxException {
        ServerCommandSource src = c.getSource();

        String[] kvs = StringArgumentType.getString(c, "args").split(" ");

        String url = "";
        String alignx = "center";
        String aligny = "center";
        int width = -1;
        int height = -1;

        for (String kv : kvs) {
            String k = kv.split("=")[0];
            String v = String.join("=", Arrays.copyOfRange(kv.split("="), 1, kv.split("=").length));
            if (k.equals("url")) {
                url = v;
            }
            if (k.equals("align")) {
                if (v.contains("left")) {
                    alignx = "left";
                }
                if (v.contains("right")) {
                    alignx = "right";
                }
                if (v.contains("top")) {
                    aligny = "top";
                }
                if (v.contains("bottom")) {
                    aligny = "bottom";
                }
            }
            if (k.equals("width")) {
                try {
                    width = Integer.parseInt(v);
                } catch (Exception ignored) {}
                if (v.contains("native")) {
                    width = 0;
                }
                if (v.contains("scale")) {
                    width = -1;
                }
            }
            if (k.equals("height")) {
                try {
                    height = Integer.parseInt(v);
                } catch (Exception ignored) {}
                if (v.contains("native")) {
                    height = 0;
                }
                if (v.contains("scale")) {
                    height = -1;
                }
            }
        }

        String name = StringArgumentType.getString(c, "name");

        if (Arrays.asList(Slapmap.mapManager.list()).contains(name)) {
            src.sendFeedback(new LiteralText("There is already an image named "+ name +"!").formatted(Formatting.RED), false);
            src.sendFeedback(new LiteralText("Try choosing a different name or deleting the other image by using: /slap delete "+ name +".").formatted(Formatting.GRAY, Formatting.ITALIC), false);
            return -1;
        }

        ImageProcessor imgproc = new ImageProcessor();

        if (!imgproc.fromUrl(url, width, height) || !imgproc.dither() || !imgproc.toMaps(name, alignx, aligny, src.getWorld())) {
            src.sendFeedback(new LiteralText("\"An error occurred while fetching or processing the image!").formatted(Formatting.RED), false);
            src.sendFeedback(new LiteralText("Are you sure that the URL is correct, or that your image is in a .png or .jpg format?").formatted(Formatting.GRAY, Formatting.ITALIC), false);
            return -1;
        }

        Give.giveStick(src.getPlayer(), name);

        src.getMinecraftServer().save(true, true, true);

        return 0;
    }
}
