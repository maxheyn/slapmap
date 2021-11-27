package online.kalkr.slapmap;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class SlapmapConfig {
    public String __comment;
    public int maxWidth;
    public int maxHeight;
    public int permLevel;
    public String multiplayerWorldName;
    public boolean isDefaultSettings;
    private static String configPath = "config/slapmap.json";


    public SlapmapConfig(String __comment, int maxWidth, int maxHeight, int permLevel, String multiplayerWorldName) {
        this.__comment = __comment;
        this.maxWidth = maxWidth;
        this.maxHeight = maxHeight;
        this.permLevel = permLevel;
        this.multiplayerWorldName = multiplayerWorldName;
    }



    public SlapmapConfig(String __comment, int maxWidth, int maxHeight, int permLevel, String multiplayerWorldName, boolean isDefaultSettings) {
        this.__comment = __comment;
        this.maxWidth = maxWidth;
        this.maxHeight = maxHeight;
        this.permLevel = permLevel;
        this.multiplayerWorldName = multiplayerWorldName;
        this.isDefaultSettings = isDefaultSettings;
    }

    public static SlapmapConfig getDefaultConfig() {
        String defaultComment = "Width/Height are the maximum sizes these images can scale to in blocks.\nPermLevel is the default Minecraft permission system to use slapmap commands. It defaults to 4 which is only operators with a level of 4 can use it. Set it to 0 to let anyone use it.\nMultiplayerWorldName is the name of the world in which your data folder where all the maps are stored is. It defaults to world but you might need to change this if you changed your world's name.";
        int defaultWidth = 4;
        int defaultHeight = 4;
        int defaultPerms = 4;
        String defaultPath = "world";

        return new SlapmapConfig(defaultComment, defaultWidth, defaultHeight, defaultPerms, defaultPath, false);
    }

    public static boolean getBoolValueFromEntry(String entry) throws FileNotFoundException {
        Object obj = new JsonParser().parse(new FileReader(configPath));
        JsonObject jo = (JsonObject) obj;

        return jo.get(entry).getAsBoolean();
    }

    public static String getStringValueFromEntry(String entry) throws FileNotFoundException {
        Object obj = new JsonParser().parse(new FileReader(configPath));
        JsonObject jo = (JsonObject) obj;

        return jo.get(entry).getAsString();
    }

    public static int getIntValueFromEntry(String entry) throws FileNotFoundException {
        Object obj = new JsonParser().parse(new FileReader(configPath));
        JsonObject jo = (JsonObject) obj;

        return jo.get(entry).getAsInt();
    }
}
