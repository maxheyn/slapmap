package online.kalkr.slapmap.action.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;
import online.kalkr.slapmap.Slapmap;

public class Give {
    public static void giveStick(PlayerEntity player, String name) {
        NbtCompound imageTag = new NbtCompound();
        imageTag.putString("image", name);

        ItemStack slapstick = new ItemStack(Items.STICK);
        slapstick.setNbt(imageTag);
        slapstick.setCustomName(new LiteralText("Slapstick - "+ name));

        ItemEntity itemEntity = new ItemEntity(player.world, player.getPos().x, player.getPos().y, player.getPos().z, slapstick);
        player.world.spawnEntity(itemEntity);
    }

    public static int give(CommandContext<ServerCommandSource> c) throws CommandSyntaxException {
        ServerCommandSource src = c.getSource();
        PlayerEntity player = src.getPlayer();

        String name = StringArgumentType.getString(c, "name");
        if (!Slapmap.mapManager.has(name)) {
            src.sendFeedback(new LiteralText("There isn't an image named "+ name +"!").formatted(Formatting.RED), false);
            src.sendFeedback(new LiteralText("Try importing one using: /slap "+ name +" url=...").formatted(Formatting.GRAY, Formatting.ITALIC), false);
            return -1;
        }

        giveStick(player, name);
        return 0;
    }
}
