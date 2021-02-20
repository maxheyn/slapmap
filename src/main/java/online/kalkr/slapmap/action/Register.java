package online.kalkr.slapmap.action;

import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.server.command.CommandManager;
import online.kalkr.slapmap.action.server.Start;
import online.kalkr.slapmap.action.subcommand.Slap;
import online.kalkr.slapmap.action.subcommand.Stick;
import online.kalkr.slapmap.action.world.Use;

public class Register {
    public static void slapCommand () {
        CommandRegistrationCallback.EVENT.register( (dispatcher, tank) ->
            dispatcher.register(CommandManager.literal("slap").requires(src -> src.hasPermissionLevel(2))
                /*

                Commands should look like this in the future
                This is planning.

                /slap                               -- simple help command

                /slap fennec                        -- give `fennec` image stick
                /slap fennec url                    -- assign web image to slap image `fennec`

                /slap help [subcommand]             -- complex help command
                /slap list                          -- list all images

                /slap delete fennec                 -- delete slap entry but not any map files.
                /slap purge fennec                  -- delete slap entry and associated map files

                */
                .then(Stick.builder)
                .then(Slap.builder)
            ));
    }

    public static void stickEvent () {
        UseItemCallback.EVENT.register((player, world, hand) -> Use.builder(player, world));
    }

    public static void serverEvent () {
        Start.build();
    }
}
