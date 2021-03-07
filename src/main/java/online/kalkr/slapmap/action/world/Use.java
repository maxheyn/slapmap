package online.kalkr.slapmap.action.world;

import com.google.common.collect.Iterables;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Hand;
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

    private static PlayerEntity player;
    private static World world;

    public static TypedActionResult<ItemStack> onUse(PlayerEntity p, World w, Hand hand) {
        player = p;
        world = w;
        HitResult hit = player.raycast(player.isCreative() ? 5 : 4.5, 1, false);
        ItemStack handItem = Iterables.get(player.getItemsHand(), 0);
        TypedActionResult<ItemStack> pass = TypedActionResult.pass(ItemStack.EMPTY);

        if (hit.getType() != HitResult.Type.BLOCK || world.isClient) return pass;

        if (handItem.getName().getString().contains("Slapstick")) stickevent(hit, handItem);
        if (handItem.getItem() == Items.FILLED_MAP) mapEvent(hit, handItem);

        return pass;
    }


    private static void mapEvent (HitResult hit, ItemStack handItem) {
        Direction playerDirection = Direction.fromRotation(player.getRotationClient().y);
        Direction blockFace = ((BlockHitResult) hit).getSide();
        BlockPos originBlockPosition = ((BlockHitResult) hit).getBlockPos();

        OrientationManager orientation =
                new OrientationManager(originBlockPosition, 1, 1, blockFace, playerDirection);
        BlockBox box = orientation.box;

        for (BlockPos framePos : BlockPos.iterate(box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ)) {
            ItemFrameEntity itemFrame = new ItemFrameEntity(world, new BlockPos(0, 0, 0), blockFace);

            itemFrame.setHeldItemStack(handItem);
            itemFrame.updatePosition(framePos.getX(), framePos.getY(), framePos.getZ());
            itemFrame.setRotation(orientation.rotation);
            itemFrame.setInvisible(true);

            CompoundTag tag = new CompoundTag();
            itemFrame.writeCustomDataToTag(tag);
            tag.putBoolean("Fixed", true);
            itemFrame.readCustomDataFromTag(tag);

            world.spawnEntity(itemFrame);
        }
    }


    private static void stickevent (HitResult hit, ItemStack handItem) {
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

            CompoundTag tag = new CompoundTag();
            itemFrame.writeCustomDataToTag(tag);
            tag.putBoolean("Fixed", true);
            itemFrame.readCustomDataFromTag(tag);

            world.spawnEntity(itemFrame);
        }
    }


    private static ItemStack getMapFromId(int id) {
        ItemStack item = new ItemStack(Items.FILLED_MAP);
        CompoundTag tag = new CompoundTag();
        tag.putInt("map", id);
        item.setTag(tag);
        return item;
    }
}
