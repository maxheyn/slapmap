package online.kalkr.slapmap.action.subcommand;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import online.kalkr.slapmap.func.ImageProcessor;

public class Slap {

    public static final ArgumentBuilder<ServerCommandSource, RequiredArgumentBuilder<ServerCommandSource, String>> builder =
        CommandManager.argument("name", StringArgumentType.string())
        .then(CommandManager.argument("url", StringArgumentType.greedyString())
            .executes(Slap::slap)

            .suggests ((context, builder) -> {
                if (!builder.getRemaining().contains("http")) {
                    builder.suggest("http://");
                    builder.suggest("https://");
                }
                return builder.buildFuture();
            })
        )
        .suggests ((context, builder) -> {
            if (builder.getRemaining().equals("")) {
                builder.suggest("...");
            }
            return builder.buildFuture();
        });


    private static int slap (CommandContext<ServerCommandSource> c) throws CommandSyntaxException {
        ServerCommandSource src = c.getSource();
        String url = StringArgumentType.getString(c, "url");
        String name = StringArgumentType.getString(c, "name");

        ImageProcessor imgproc = new ImageProcessor();

        // download the image, then turn the image into map format, then into maps
        if(!imgproc.fromUrl(url) || !imgproc.dither() || !imgproc.toMaps(name, src.getWorld())) {
            src.sendFeedback(new LiteralText("§cAn error occurred while fetching or processing the image!"), true);
            src.sendFeedback(new LiteralText("§7§oAre you sure that the URL is correct?"), true);
            return -1;
        }

        Stick.giveStick(src.getPlayer(), name);
        return 0;
    }
}
