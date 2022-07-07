package online.kalkr.slapmap.action.command;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class Help {
    public static int help(CommandContext<ServerCommandSource> context) {
        ServerCommandSource src = context.getSource();
        src.sendFeedback(Text.literal("Slapmap - Import images using a simple set of commands."), false);
        src.sendFeedback(Text.literal("Use /slap help ... for more help.").formatted(Formatting.GRAY, Formatting.ITALIC), false);
        return 0;
    }

    public static int importing(CommandContext<ServerCommandSource> context) {
        ServerCommandSource src = context.getSource();
        src.sendFeedback(Text.literal("Importing Images Using Slapmap"), false);
        src.sendFeedback(Text.literal("/slap image_name url=...").formatted(Formatting.GRAY, Formatting.ITALIC), false);
        src.sendFeedback(Text.literal("Load images from the internet (as long as it is .png or .jpg) onto an array of maps using the above syntax.").formatted(Formatting.GRAY, Formatting.ITALIC), false);
        src.sendFeedback(Text.literal("If you would like to adjust the image, such as changing the width, height, or alignment, check out the adjusting page of the help command.").formatted(Formatting.GRAY, Formatting.ITALIC), false);
        return 0;
    }

    public static int adjusting(CommandContext<ServerCommandSource> context) {
        ServerCommandSource src = context.getSource();
        src.sendFeedback(Text.literal("Changing Image Properties"), false);
        src.sendFeedback(Text.literal("/slap image_name url=... width=... height=... align=...").formatted(Formatting.GRAY, Formatting.ITALIC), false);
        src.sendFeedback(Text.literal("Adjust properties using the (optional) arguments above. Width and height take a number in blocks, and alignment is a position on the item frame.").formatted(Formatting.GRAY, Formatting.ITALIC), false);
        src.sendFeedback(Text.literal("Specifying both width and height will stretch the image, so if you want it to look nice, just choose one.").formatted(Formatting.GRAY, Formatting.ITALIC), false);
        return 0;
    }

    public static int placing(CommandContext<ServerCommandSource> context) {
        ServerCommandSource src = context.getSource();
        src.sendFeedback(Text.literal("Placing Images"), false);
        src.sendFeedback(Text.literal("/slap image_name").formatted(Formatting.GRAY, Formatting.ITALIC), false);
        src.sendFeedback(Text.literal("After importing an image, you are given a Slapstick. You can also give yourself one with the above syntax.").formatted(Formatting.GRAY, Formatting.ITALIC), false);
        src.sendFeedback(Text.literal("Right click this stick wherever you want the array of maps to go. Maps can also be placed down directly without the need to worry about item frames.").formatted(Formatting.GRAY, Formatting.ITALIC), false);
        return 0;
    }

    public static int deleting(CommandContext<ServerCommandSource> context) {
        ServerCommandSource src = context.getSource();
        src.sendFeedback(Text.literal("Deleting Images"), false);
        src.sendFeedback(Text.literal("/slap delete image_name").formatted(Formatting.GRAY, Formatting.ITALIC), false);
        src.sendFeedback(Text.literal("In order to remove imported images, use the above syntax.").formatted(Formatting.GRAY, Formatting.ITALIC), false);
        src.sendFeedback(Text.literal("This will not remove Slapsticks, maps left in the inventory, and images in unloaded areas. For best results, reload the world after you issue this command.").formatted(Formatting.GRAY, Formatting.ITALIC), false);
        return 0;
    }

    public static int removing(CommandContext<ServerCommandSource> context) {
        ServerCommandSource src = context.getSource();
        src.sendFeedback(Text.literal("Removing Images"), false);
        src.sendFeedback(Text.literal("Parts of images can be easily removed by left-clicking them. If you want to remove the whole image, hold shift, then left-click.").formatted(Formatting.GRAY, Formatting.ITALIC), false);
        src.sendFeedback(Text.literal("Removing entire images only works if you are not in survival mode.").formatted(Formatting.GRAY, Formatting.ITALIC), false);
        return 0;
    }
}
