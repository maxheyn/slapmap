package online.kalkr.slapmap.action.subcommand;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import online.kalkr.slapmap.Slapmap;
import online.kalkr.slapmap.func.ImageProcessor;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.concurrent.CompletableFuture;

public class Slap {

    public static Hashtable<String, String[]> argHolder = new Hashtable<>();

    public static void initArgs() {
        argHolder.put("url", new String[]{"https://", "http://"});
        //argHolder.put("width", new String[]{"nearest", "1", "2", "3"});
        //argHolder.put("height", new String[]{"nearest", "1", "2", "3"});
        //argHolder.put("align", new String[]{"top-left", "top", "top-right", "left", "center", "right", "bottom-left", "bottom", "bottom-right"});
    }

    public static final ArgumentBuilder<ServerCommandSource, RequiredArgumentBuilder<ServerCommandSource, String>> builder =
            CommandManager.argument("name", StringArgumentType.string())
                    .executes(Slap::giveName)

                    .suggests((context, builder) -> {
                        for (String image : Slapmap.mapManager.list()) {
                            String[] words = context.getInput().split(" ");
                            if (words.length != 1 && !image.contains(words[1])) continue;
                            builder.suggest(image);
                        }
                        return builder.buildFuture();
                    })

                    .then(CommandManager.argument("args", StringArgumentType.greedyString())
                            .executes(Slap::slap)

                            .suggests(Slap::argSuggester)
                    );


    private static int slap (CommandContext<ServerCommandSource> c) throws CommandSyntaxException {
        ServerCommandSource src = c.getSource();

        String[] kvs = StringArgumentType.getString(c, "args").split(" ");

        String url = "";
        //String align = "center";
        //int width = -1;
        //int height = -1;

        for ( String kv : kvs ) {
            String k = kv.split("=")[0];
            String v = String.join("=", Arrays.copyOfRange(kv.split("="), 1, kv.split("=").length));
            if (k.equals("url")) {
                url = v;
            }
        }

        String name = StringArgumentType.getString(c, "name");

        if (Arrays.asList(Slapmap.mapManager.list()).contains(name)) {
            src.sendFeedback(new LiteralText("§cThere is already an image named " + name + "!"), true);
            src.sendFeedback(new LiteralText("§7§oTry choosing a different name or deleting the other image by using: /slap delete " + name), true);
            return -1;
        }

        ImageProcessor imgproc = new ImageProcessor();

        if(!imgproc.fromUrl(url) || !imgproc.dither() || !imgproc.toMaps(name, src.getWorld())) {
            src.sendFeedback(new LiteralText("§cAn error occurred while fetching or processing the image!"), true);
            src.sendFeedback(new LiteralText("§7§oAre you sure that the URL is correct?"), true);
            return -1;
        }

        Slap.giveStick(src.getPlayer(), name);
        return 0;
    }

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
        if (!Slapmap.mapManager.has(name)) {
            src.sendFeedback(new LiteralText("§cThere isn't an image named §o" + name + "!"), true);
            src.sendFeedback(new LiteralText("§7§oTry importing one using: /slap " + name + " https://..."), true);
            return -1;
        }

        giveStick(player, name);
        return 0;
    }

    // looks very pretty but might be too heavy to run on laggy servers...
    private static CompletableFuture<Suggestions> argSuggester(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) {
        String[] words = (context.getInput()+"\n").split(" ");
        words = Arrays.copyOfRange(words, 2, words.length);

        String lastWord = Slap.splitAndGet("\n", words[words.length-1], 0);

        Enumeration<String> args = argHolder.keys();
        while ( args.hasMoreElements() ) {
            String arg = args.nextElement();

            boolean argUsed = false;
            for ( int i = 0; i < words.length - 1; i++ ) {
                if (words[i].contains(arg)) {
                    argUsed = true;
                    break;
                }
            }
            if (argUsed) continue;

            if (arg.equals(lastWord)) {
                words[words.length - 1] = arg + "=";
                builder.suggest(String.join(" ", words));
                continue;
            }

            if (lastWord.contains(arg+"=")) {
                for (String subarg : argHolder.get(arg)) {
                    if (!subarg.contains(Slap.splitAndGet("=", lastWord, 1))) continue;
                    words[words.length - 1] = arg + "=" + subarg;
                    builder.suggest(String.join(" ", words));
                }
                continue;
            }

            if (!arg.contains(lastWord)) continue;

            words[words.length - 1] = arg;
            builder.suggest(String.join(" ", words));
        }

        return builder.buildFuture();
    }

    private static String splitAndGet (String delimiter, String string, int index) {
        String[] chopped = string.split(delimiter);
        if (index >= chopped.length) return "";
        return chopped[index];
    }
}
