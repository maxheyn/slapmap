package online.kalkr.slapmap.action.subcommand;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import online.kalkr.slapmap.Slapmap;

public class Stick {

    public static final ArgumentBuilder<ServerCommandSource, LiteralArgumentBuilder<ServerCommandSource>> builder =
            CommandManager.literal("give")
                    .then(CommandManager.argument("name", StringArgumentType.greedyString())
                            .executes(Stick::giveName)

                            .suggests ((context, builder) -> {
                                for (String image : Slapmap.loadedMaps.list()) {
                                    builder.suggest(image);
                                }
                                return builder.buildFuture();
                            })
                    );


    public static void giveStick(PlayerEntity player, String name) {
        CompoundTag imageTag = new CompoundTag();
        imageTag.putString("image", name);

        ItemStack slapstick = new ItemStack(Items.STICK);
        slapstick.setTag(imageTag);
        slapstick.setCustomName(new LiteralText("Slapstick - " + name));

        ItemEntity itemEntity = new ItemEntity(player.world, player.getPos().x, player.getPos().y, player.getPos().z, slapstick);
        player.world.spawnEntity(itemEntity);
    }


    private static int giveName(CommandContext<ServerCommandSource> c) throws CommandSyntaxException {
        ServerCommandSource src = c.getSource();
        PlayerEntity player = src.getPlayer();

        String name = StringArgumentType.getString(c, "name");
        if (!Slapmap.loadedMaps.has(name)) {
            src.sendFeedback(new LiteralText("§cThere isn't an image named §o" + name + "!"), true);
            src.sendFeedback(new LiteralText("§7§oTry importing one using: /slap " + name + " https://..."), true);
            return -1;
        }

        giveStick(player, name);
        return 0;
    }
}
