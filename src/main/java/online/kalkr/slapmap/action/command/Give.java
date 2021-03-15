package online.kalkr.slapmap.action.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import online.kalkr.slapmap.Slapmap;

import java.util.concurrent.CompletableFuture;

public class Give {
    public static CompletableFuture<Suggestions> nameSuggester(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) {
        for (String image : Slapmap.mapManager.list()) {
            String[] words = context.getInput().split(" ");
            if (words.length != 1 && !image.contains(words[1])) continue;
            builder.suggest(image);
        }
        return builder.buildFuture();
    }

    public static void giveStick(PlayerEntity player, String name) {
        CompoundTag imageTag = new CompoundTag();
        imageTag.putString("image", name);

        ItemStack slapstick = new ItemStack(Items.STICK);
        slapstick.setTag(imageTag);
        slapstick.setCustomName(new TranslatableText("slapmap.stickname", name));

        ItemEntity itemEntity = new ItemEntity(player.world, player.getPos().x, player.getPos().y, player.getPos().z, slapstick);
        player.world.spawnEntity(itemEntity);
    }

    public static int give(CommandContext<ServerCommandSource> c) throws CommandSyntaxException {
        ServerCommandSource src = c.getSource();
        PlayerEntity player = src.getPlayer();

        String name = StringArgumentType.getString(c, "name");
        if (!Slapmap.mapManager.has(name)) {
            src.sendFeedback(new TranslatableText("slapmap.err.none", new LiteralText(name).formatted(Formatting.ITALIC)).formatted(Formatting.RED), false);
            src.sendFeedback(new TranslatableText("slapmap.err.none.tip", name).formatted(Formatting.GRAY, Formatting.ITALIC), false);
            return -1;
        }

        giveStick(player, name);
        return 0;
    }
}
