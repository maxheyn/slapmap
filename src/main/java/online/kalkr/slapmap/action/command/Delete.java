package online.kalkr.slapmap.action.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import online.kalkr.slapmap.Slapmap;

import java.util.Arrays;

public class Delete {
    public static int delete(CommandContext<ServerCommandSource> c) {
        c.getSource().getServer().save(true, true, true);

        String name = StringArgumentType.getString(c, "name");

        if (!Arrays.asList(Slapmap.mapManager.list()).contains(name)) {
            c.getSource().sendFeedback(new LiteralText("There isn't an image named "+ name +"!").formatted(Formatting.RED), false);
            c.getSource().sendFeedback(new LiteralText("Try importing one using: /slap "+ name +" url=...").formatted(Formatting.GRAY, Formatting.ITALIC), false);
            return -1;
        };

        World world = c.getSource().getWorld();
        PlayerEntity player = null;
        try {
            player = c.getSource().getPlayer();
        } catch (CommandSyntaxException e) { return -1; }

        Box pos = new Box(player.getPos().x+999, player.getPos().y+256, player.getPos().z+999, player.getPos().x-999, player.getPos().y-256, player.getPos().z-999);
        for (ItemFrameEntity itemFrame : world.getEntitiesByType(EntityType.ITEM_FRAME, pos, fromBox -> isIdToBeDeleted(fromBox, name))) {
            itemFrame.kill();
        }

        Slapmap.mapManager.del(name);

        c.getSource().sendFeedback(new LiteralText("Image "+ name +" successfully deleted!"), false);
        return 0;
    }

    public static boolean isIdToBeDeleted(ItemFrameEntity entityFromBox, String key) {
        return Slapmap.mapManager.getNameFromId(Slapmap.mapManager.getIdFromEntity(entityFromBox)).equals(key);
    }
}
