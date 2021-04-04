package online.kalkr.slapmap.action.world;

import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;
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

import java.util.Arrays;
import java.util.function.Function;

public class Use {
    public static TypedActionResult<ItemStack> onUse(PlayerEntity player, World world, Hand hand) {
        HitResult hit = player.raycast(player.isCreative() ? 5 : 4.5, 1, false);
        ItemStack handItem = player.getStackInHand(hand);
        TypedActionResult<ItemStack> pass = TypedActionResult.pass(ItemStack.EMPTY);

        if (hit.getType() != HitResult.Type.BLOCK || world.isClient) return pass;

        if (handItem.getTag() != null && !handItem.getTag().getString("image").isEmpty()) stickevent(player, world, hit, handItem);
        if (handItem.getItem() == Items.FILLED_MAP) mapEvent(player, world, hit, handItem);

        return pass;
    }

    private static void mapEvent (PlayerEntity player, World world, HitResult hit, ItemStack handItem) {
        Direction playerDirection = Direction.fromRotation(player.getRotationClient().y);
        Direction blockFace = ((BlockHitResult) hit).getSide();
        BlockPos originBlockPosition = ((BlockHitResult) hit).getBlockPos();

        OrientationManager orientation = new OrientationManager(originBlockPosition, 1, 1, blockFace, playerDirection);

        placeMaps(world, orientation, blockFace, (pos) -> handItem);

        if (!player.isCreative()) handItem.decrement(1);
    }

    private static void stickevent (PlayerEntity player, World world, HitResult hit, ItemStack handItem) {
        assert handItem.getTag() != null;
        String image = handItem.getTag().getString("image");

        if (!Arrays.asList(Slapmap.mapManager.list()).contains(image)) {
            player.sendMessage(new LiteralText("There isn't an image named "+ image +"!").formatted(Formatting.RED), false);
            player.sendMessage(new LiteralText("Try importing one using: /slap "+ image +" url=...").formatted(Formatting.GRAY, Formatting.ITALIC), false);
            return;
        };

        Direction playerDirection = Direction.fromRotation(player.getRotationClient().y);
        Direction blockFace = ((BlockHitResult) hit).getSide();
        BlockPos originBlockPosition = ((BlockHitResult) hit).getBlockPos();

        OrientationManager orientation = new OrientationManager(
                originBlockPosition,
                Slapmap.mapManager.getWidth(image),
                Slapmap.mapManager.getHeight(image),
                blockFace,
                playerDirection
        );

        placeMaps(world, orientation, blockFace, (pos) -> getMapFromId(Slapmap.mapManager.getMaps(image)[pos]));
    }

    private static void placeMaps(World world, OrientationManager orientation, Direction blockFace, Function<Integer, ItemStack> getItem) {
        BlockBox box = orientation.box;
        for (BlockPos framePos : BlockPos.iterate(box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ)) {
            ItemFrameEntity itemFrame = new ItemFrameEntity(world, new BlockPos(0, 0, 0), blockFace);

            itemFrame.setHeldItemStack(getItem.apply(orientation.relPos(framePos)));
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
