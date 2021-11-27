package online.kalkr.slapmap.func;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.WorldSavePath;
import online.kalkr.slapmap.SlapmapConfig;

import java.io.*;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.concurrent.atomic.AtomicReference;

public class MapManager {

    private final Hashtable<String, ImageData> store = new Hashtable<String, ImageData>();
    private final String mapsPath;

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
        String serverWorldName = "world";
        try { serverWorldName = SlapmapConfig.getStringValueFromEntry("MultiplayerWorldName"); } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        String fs = System.getProperty("file.separator");
        if (server.isDedicated()) {
            mapsPath = serverWorldName+fs+"data"+fs;
        } else {
            String atFile = server.getSavePath(WorldSavePath.ROOT)
                    .toString()
                    .split("saves")[1]
                    .split(fs.equals("\\") ? "\\\\" : fs)[1];
            mapsPath = "saves"+fs+atFile+fs+"data"+fs;
        }

        StringBuilder fileString = new StringBuilder();
        try {
            Reader reader = new FileReader(mapsPath.concat("_slap.dat"));
            int data = reader.read();
            while (data != -1) {
                fileString.append((char) data);
                data = reader.read();
            }
            reader.close();
        } catch (IOException e) { return; }

        String[] lineArray = fileString.toString().split("\n");

        if (fileString.toString().equals("")) {
            return;
        }

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
            PrintWriter out = new PrintWriter(new FileWriter(mapsPath.concat("_slap.dat"), true));
            out.append(key + " " + width + " " + height + " " + mapIdStr + "\n");
            out.close();
        } catch (IOException e) { return; }
    }

    public void del(String key) {
        Integer[] maps = store.get(key).mapIds;
        for (int map: maps) {
            File mapFile = new File(mapsPath+"map_"+String.valueOf(map)+".dat");
            boolean didDelete = mapFile.delete();
        }

        try {
            Reader reader = new FileReader(mapsPath.concat("_slap.dat"));
            StringBuilder fileString = new StringBuilder();
            int data = reader.read();
            while (data != -1) {
                fileString.append((char) data);
                data = reader.read();
            }
            reader.close();

            PrintWriter temp = new PrintWriter(new FileWriter(mapsPath.concat("_slap.dat.tmp"), true));
            String[] textEntries = fileString.toString().split("\n");
            for (String entry: textEntries) {
                if(!entry.split(" ")[0].equals(key)) {
                    temp.append(entry+'\n');
                }
            }
            temp.close();

            File file = new File(mapsPath.concat("_slap.dat.tmp"));
            File newFile = new File(mapsPath.concat("_slap.dat"));
            boolean didRename = file.renameTo(newFile);
        } catch (IOException e) { return; }

        store.remove(key);
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

    public int getIdFromEntity(Entity entity) {
        if (entity.getType() != EntityType.ITEM_FRAME) return -1;
        ItemStack heldItem = ((ItemFrameEntity) entity).getHeldItemStack();
        if (!heldItem.isItemEqual(Items.FILLED_MAP.getDefaultStack())) return -1;
        return heldItem.getNbt().getInt("map");
    }

    public boolean isIdManaged(int id) {
        return !String.valueOf(id).equals(getNameFromId(id));
    }

    public String getNameFromId(int id) {
        AtomicReference<String> name = new AtomicReference<String>();
        name.getAndSet(String.valueOf(id));
        store.forEach((key, value) -> {
            if (Arrays.asList(value.mapIds).contains(id)) name.getAndSet(key);
        });
        return name.get();
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
