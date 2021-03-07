package online.kalkr.slapmap.action.server;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import online.kalkr.slapmap.Slapmap;
import online.kalkr.slapmap.func.MapManager;

public class Start {
    public static void build() {
        ServerLifecycleEvents.SERVER_STARTED.register((server) -> {
            Slapmap.mapManager = new MapManager(server);
        });
    }
}
