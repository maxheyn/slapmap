package online.kalkr.slapmap;

import net.fabricmc.api.ModInitializer;
import online.kalkr.slapmap.action.Register;
import online.kalkr.slapmap.func.MapManager;

public class Slapmap implements ModInitializer {

    public static MapManager loadedMaps;

    @Override
    public void onInitialize() {
        Register.slapCommand();
        Register.useEvent();
        Register.punchEvent();
        Register.serverEvent();
    }

}

