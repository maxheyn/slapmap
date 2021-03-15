package online.kalkr.slapmap.action.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import online.kalkr.slapmap.Slapmap;
import online.kalkr.slapmap.func.ImageProcessor;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.concurrent.CompletableFuture;

public class Import {

    public static Hashtable<String, String[]> argHolder = new Hashtable<>();

    public static void initArgs() {
        argHolder.put("url", new String[]{"https://", "http://"});
        //argHolder.put("width", new String[]{"nearest", "1", "2", "3"});
        //argHolder.put("height", new String[]{"nearest", "1", "2", "3"});
        //argHolder.put("align", new String[]{"top-left", "top", "top-right", "left", "center", "right", "bottom-left", "bottom", "bottom-right"});
    }

    public static int slap(CommandContext<ServerCommandSource> c) throws CommandSyntaxException {
        ServerCommandSource src = c.getSource();

        String[] kvs = StringArgumentType.getString(c, "args").split(" ");

        String url = "";
        //String align = "center";
        //int width = -1;
        //int height = -1;

        for (String kv : kvs) {
            String k = kv.split("=")[0];
            String v = String.join("=", Arrays.copyOfRange(kv.split("="), 1, kv.split("=").length));
            if (k.equals("url")) {
                url = v;
            }
        }

        String name = StringArgumentType.getString(c, "name");

        if (Arrays.asList(Slapmap.mapManager.list()).contains(name)) {
            src.sendFeedback(new TranslatableText("slapmap.err.exisits", new LiteralText(name).formatted(Formatting.ITALIC)).formatted(Formatting.RED), false);
            src.sendFeedback(new TranslatableText("slapmap.err.exisits.tip", name).formatted(Formatting.GRAY, Formatting.ITALIC), false);
            return -1;
        }

        ImageProcessor imgproc = new ImageProcessor();

        if (!imgproc.fromUrl(url) || !imgproc.dither() || !imgproc.toMaps(name, src.getWorld())) {
            src.sendFeedback(new TranslatableText("slapmap.err.importerr").formatted(Formatting.RED), false);
            src.sendFeedback(new TranslatableText("slapmap.err.importerr.tip").formatted(Formatting.GRAY, Formatting.ITALIC), false);
            return -1;
        }

        Give.giveStick(src.getPlayer(), name);
        return 0;
    }

    // looks very pretty but might be too heavy to run on laggy servers...
    public static CompletableFuture<Suggestions> argSuggester(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) {
        String[] words = (context.getInput()+"\n").split(" ");
        words = Arrays.copyOfRange(words, 2, words.length);

        String lastWord = Import.splitAndGet("\n", words[words.length-1], 0);

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
                    if (!subarg.contains(Import.splitAndGet("=", lastWord, 1))) continue;
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
