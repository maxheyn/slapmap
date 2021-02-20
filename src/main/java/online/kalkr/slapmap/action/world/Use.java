package online.kalkr.slapmap.action.world;

import com.google.common.collect.Iterables;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import online.kalkr.slapmap.Slapmap;
import online.kalkr.slapmap.func.OrientationManager;

public class Use {

    public static TypedActionResult<ItemStack> builder(PlayerEntity player, World world) {
        HitResult hit = player.raycast(player.isCreative() ? 5 : 4.5, 1, false);
        ItemStack handItem = Iterables.get(player.getItemsHand(), 0);

        if (world.isClient || hit.getType() != HitResult.Type.BLOCK || !handItem.getName().getString().contains("Slapstick")) {
            return TypedActionResult.pass(ItemStack.EMPTY);
        }

        assert handItem.getTag() != null;
        String image = handItem.getTag().getString("image");

        Direction playerDirection = Direction.fromRotation(player.getRotationClient().y);
        Direction blockFace = ((BlockHitResult) hit).getSide();
        BlockPos originBlockPosition = ((BlockHitResult) hit).getBlockPos();

        OrientationManager orientation = new OrientationManager(
            originBlockPosition,
            Slapmap.loadedMaps.getWidth(image),
            Slapmap.loadedMaps.getHeight(image),
            blockFace,
            playerDirection
        );
        BlockBox box = orientation.box;

        for (BlockPos framePos : BlockPos.iterate(box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ)) {
            ItemFrameEntity itemFrame = new ItemFrameEntity(world, new BlockPos(0, 0, 0), blockFace);

            itemFrame.setHeldItemStack(getMapFromId(Slapmap.loadedMaps.getMaps(image)[orientation.relPos(framePos)]));
            itemFrame.updatePosition(framePos.getX(), framePos.getY(), framePos.getZ());
            itemFrame.setRotation(orientation.rotation);
            itemFrame.setInvisible(true);

            world.spawnEntity(itemFrame);
        }

        return TypedActionResult.pass(ItemStack.EMPTY);
    }


    private static ItemStack getMapFromId(int id) {
        ItemStack item = new ItemStack(Items.FILLED_MAP);
        CompoundTag tag = new CompoundTag();
        tag.putInt("map", id);
        item.setTag(tag);
        return item;
    }
}
