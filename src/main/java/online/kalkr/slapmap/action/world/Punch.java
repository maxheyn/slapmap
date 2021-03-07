package online.kalkr.slapmap.action.world;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import online.kalkr.slapmap.Slapmap;

public class Punch {

    public static ActionResult onPunch (World world, PlayerEntity player, Entity entity) {
        if (world.isClient) return ActionResult.PASS;

        if (entity.getType() != EntityType.ITEM_FRAME) return ActionResult.PASS;
        if (!((ItemFrameEntity) entity).getHeldItemStack().isItemEqual(Items.FILLED_MAP.getDefaultStack())) return ActionResult.PASS;

        Box pos = new Box(entity.getPos().x, entity.getPos().y, entity.getPos().z, entity.getPos().x, entity.getPos().y, entity.getPos().z);
        if (player.isSneaking() && player.isCreative()) {
            //TODO: get better box, instead of big dumb one that targets more than it should
            pos = new Box(entity.getPos().x+99, entity.getPos().y+99, entity.getPos().z+99, entity.getPos().x-99, entity.getPos().y-99, entity.getPos().z-99);
        }

        for (ItemFrameEntity itemFrame : world.getEntitiesByType(EntityType.ITEM_FRAME, pos, fromBox -> isSameImage(fromBox, (ItemFrameEntity) entity))) {
            if (!(player.isSneaking() && player.isCreative())) {
                ItemEntity item = new ItemEntity(world, itemFrame.getPos().x, itemFrame.getPos().y, itemFrame.getPos().z, itemFrame.getHeldItemStack());
                world.spawnEntity(item);
            }
            itemFrame.remove();
        }

        return ActionResult.FAIL;
    }

    public static boolean isSameImage(ItemFrameEntity entityFromBox, ItemFrameEntity punchedEntity) {
        int boxedId = entityFromBox.getHeldItemStack().getTag().getInt("map");
        int punchedId = punchedEntity.getHeldItemStack().getTag().getInt("map");
        return Slapmap.loadedMaps.getNameFromId(boxedId).equals(Slapmap.loadedMaps.getNameFromId(punchedId));
    }
}
