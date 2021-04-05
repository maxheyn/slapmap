package online.kalkr.slapmap.func;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.server.command.ServerCommandSource;
import online.kalkr.slapmap.Slapmap;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.concurrent.CompletableFuture;

public class CommandSuggester {

    public static Hashtable<String, String[]> argHolder = new Hashtable<>();

    public static void initArgs() {
        argHolder.put("url", new String[]{"https://", "http://"});
        argHolder.put("width", new String[]{"scale", "native", "1", "2", "3", "49"});
        argHolder.put("height", new String[]{"scale", "native", "1", "2", "3", "49"});
        argHolder.put("align", new String[]{"topleft", "top", "topright", "left", "center", "right", "bottomleft", "bottom", "bottomright"});
    }

    public static CompletableFuture<Suggestions> imageNames(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) {
        for (String image : Slapmap.mapManager.list()) {
            String[] words = context.getInput().split(" ");
            if (context.getInput().charAt(context.getInput().length()-1) != ' ' && !image.contains(words[words.length-1])) continue;
            builder.suggest(image);
        }
        return builder.buildFuture();
    }

    // looks very pretty but might be too heavy to run on laggy servers...
    public static CompletableFuture<Suggestions> arguments(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) {
        String[] words = (context.getInput()+"\n").split(" ");
        words = Arrays.copyOfRange(words, 2, words.length);

        String lastWord = CommandSuggester.splitAndGet("\n", words[words.length - 1], 0);

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
                    if (!subarg.contains(CommandSuggester.splitAndGet("=", lastWord, 1))) continue;
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
