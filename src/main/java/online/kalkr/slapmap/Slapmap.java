package online.kalkr.slapmap;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.Box;
import online.kalkr.slapmap.action.Register;
import online.kalkr.slapmap.func.MapManager;

public class Slapmap implements ModInitializer {

    public static MapManager loadedMaps;

    @Override
    public void onInitialize() {
        Register.slapCommand();
        Register.stickEvent();
        Register.serverEvent();

        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {

            if (world.isClient) {
                return ActionResult.PASS;
            }

            if (entity.getType() != EntityType.ITEM_FRAME) return ActionResult.PASS;
            ItemStack frameItem = ((ItemFrameEntity) entity).getHeldItemStack();
            if (!frameItem.isItemEqual(Items.FILLED_MAP.getDefaultStack())) return ActionResult.PASS;
            int mapId = frameItem.getTag().getInt("map");

            for (ItemFrameEntity zoing:world.getEntitiesByType(EntityType.ITEM_FRAME, new Box(entity.getPos().x, entity.getPos().y, entity.getPos().z, entity.getPos().x, entity.getPos().y, entity.getPos().z), itemFrameEntity -> true)) {
                ItemEntity george = new ItemEntity(world, zoing.getPos().x, zoing.getPos().y, zoing.getPos().z, zoing.getHeldItemStack());
                world.spawnEntity(george);
                zoing.remove();
            }
            System.out.println(mapId);

            return ActionResult.FAIL;
        });

    }

}

