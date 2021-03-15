package online.kalkr.slapmap.action.command;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

public class Help {

    public static int help(CommandContext<ServerCommandSource> context) {
        ServerCommandSource src = context.getSource();
        src.sendFeedback(new TranslatableText("slapmap.help.head"), false);
        src.sendFeedback(new TranslatableText("slapmap.help.line1").formatted(Formatting.GRAY, Formatting.ITALIC), false);
        src.sendFeedback(new TranslatableText("slapmap.help.line2").formatted(Formatting.GRAY, Formatting.ITALIC), false);
        return 0;
    }

    public static int importing(CommandContext<ServerCommandSource> context) {
        ServerCommandSource src = context.getSource();
        src.sendFeedback(new TranslatableText("slapmap.help.importing.head"), false);
        src.sendFeedback(new TranslatableText("slapmap.help.importing.line1").formatted(Formatting.GRAY, Formatting.ITALIC), false);
        src.sendFeedback(new TranslatableText("slapmap.help.importing.line2").formatted(Formatting.GRAY, Formatting.ITALIC), false);
        src.sendFeedback(new TranslatableText("slapmap.help.importing.line3").formatted(Formatting.GRAY, Formatting.ITALIC), false);
        return 0;
    }

    public static int adjusting(CommandContext<ServerCommandSource> context) {
        ServerCommandSource src = context.getSource();
        src.sendFeedback(new TranslatableText("slapmap.help.adjusting.head"), false);
        src.sendFeedback(new TranslatableText("slapmap.help.adjusting.line1").formatted(Formatting.GRAY, Formatting.ITALIC), false);
        src.sendFeedback(new TranslatableText("slapmap.help.adjusting.line2").formatted(Formatting.GRAY, Formatting.ITALIC), false);
        src.sendFeedback(new TranslatableText("slapmap.help.adjusting.line3").formatted(Formatting.GRAY, Formatting.ITALIC), false);
        src.sendFeedback(new TranslatableText("slapmap.help.adjusting.line4").formatted(Formatting.GRAY, Formatting.ITALIC), false);
        return 0;
    }

    public static int deleting(CommandContext<ServerCommandSource> context) {
        ServerCommandSource src = context.getSource();
        src.sendFeedback(new TranslatableText("slapmap.help.deleting.head"), false);
        src.sendFeedback(new TranslatableText("slapmap.help.deleting.line1").formatted(Formatting.GRAY, Formatting.ITALIC), false);
        src.sendFeedback(new TranslatableText("slapmap.help.deleting.line2").formatted(Formatting.GRAY, Formatting.ITALIC), false);
        return 0;
    }

    public static int placing(CommandContext<ServerCommandSource> context) {
        ServerCommandSource src = context.getSource();
        src.sendFeedback(new TranslatableText("slapmap.help.placing.head"), false);
        src.sendFeedback(new TranslatableText("slapmap.help.placing.line1").formatted(Formatting.GRAY, Formatting.ITALIC), false);
        src.sendFeedback(new TranslatableText("slapmap.help.placing.line2").formatted(Formatting.GRAY, Formatting.ITALIC), false);
        src.sendFeedback(new TranslatableText("slapmap.help.placing.line3").formatted(Formatting.GRAY, Formatting.ITALIC), false);
        return 0;
    }

    public static int removing(CommandContext<ServerCommandSource> context) {
        ServerCommandSource src = context.getSource();
        src.sendFeedback(new TranslatableText("slapmap.help.removing.head"), false);
        src.sendFeedback(new TranslatableText("slapmap.help.removing.line1").formatted(Formatting.GRAY, Formatting.ITALIC), false);
        src.sendFeedback(new TranslatableText("slapmap.help.removing.line2").formatted(Formatting.GRAY, Formatting.ITALIC), false);
        return 0;
    }
}
