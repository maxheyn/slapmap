package online.kalkr.slapmap;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.api.ModInitializer;
import online.kalkr.slapmap.action.Register;
import online.kalkr.slapmap.SlapmapConfig;
import online.kalkr.slapmap.func.MapManager;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;

public class Slapmap implements ModInitializer {

    public static MapManager mapManager;
    public static SlapmapConfig config;
    public static SlapmapConfig defaultConfig = SlapmapConfig.getDefaultConfig();
    public static Path pathForTheConfig = Paths.get("config/slapmap.json");
    public static Gson configDataStuff = new GsonBuilder().setPrettyPrinting()
            .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE).create();

    @Override
    public void onInitialize() {
        initConfig();
        Register.slapCommand();
        Register.useEvent();
        Register.punchEvent();
        Register.serverEvent();
    }

    public static void initConfig() {
        try {
            if (pathForTheConfig.toFile().exists()) {
                config = configDataStuff.fromJson(new String(Files.readAllBytes(pathForTheConfig)),
                        SlapmapConfig.class);
                if (config.isDefaultSettings) {
                    config = defaultConfig;
                    Files.write(pathForTheConfig, Collections.singleton(configDataStuff.toJson(defaultConfig)));
                }
            } else {
                Files.write(pathForTheConfig, Collections.singleton(configDataStuff.toJson(defaultConfig)));
                config = defaultConfig;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
