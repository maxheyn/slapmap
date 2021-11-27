package online.kalkr.slapmap.action;

import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.server.command.CommandManager;
import online.kalkr.slapmap.SlapmapConfig;
import online.kalkr.slapmap.action.command.Delete;
import online.kalkr.slapmap.action.command.Give;
import online.kalkr.slapmap.action.command.Help;
import online.kalkr.slapmap.action.command.Import;
import online.kalkr.slapmap.action.server.Start;
import online.kalkr.slapmap.action.world.Punch;
import online.kalkr.slapmap.action.world.Use;
import online.kalkr.slapmap.func.CommandSuggester;

import java.io.FileNotFoundException;

public class Register {

    private static int getPermLevelInConfig() {
        int permLevel = 4;
        try { permLevel = SlapmapConfig.getIntValueFromEntry("PermLevel"); } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return permLevel;
    }


    public static void slapCommand () {
        CommandSuggester.initArgs();
        int permLevel = Register.getPermLevelInConfig();
        CommandRegistrationCallback.EVENT.register((dispatcher, tank) ->
                dispatcher.register(CommandManager.literal("slap").requires(src -> src.hasPermissionLevel(permLevel)) //set to 0 to allow anyone to use the command (volatile!)
                        .executes(Help::help)
                        .then(CommandManager.literal("help")
                                .executes(Help::help)
                                .then(CommandManager.literal("adjusting")
                                        .executes(Help::adjusting)
                                )
                                .then(CommandManager.literal("importing")
                                        .executes(Help::importing)
                                )
                                .then(CommandManager.literal("placing")
                                        .executes(Help::placing)
                                )
                                .then(CommandManager.literal("removing")
                                        .executes(Help::removing)
                                )
                                .then(CommandManager.literal("deleting")
                                        .executes(Help::deleting)
                                )
                        )
                        .then(CommandManager.literal("delete")
                                .then(CommandManager.argument("name", StringArgumentType.string())
                                        .executes(Delete::delete)
                                        .suggests(CommandSuggester::imageNames)
                                )
                        )
                        .then(CommandManager.argument("name", StringArgumentType.string())
                                .executes(Give::give)
                                .suggests(CommandSuggester::imageNames)
                                .then(CommandManager.argument("args", StringArgumentType.greedyString())
                                        .executes(Import::slap)
                                        .suggests(CommandSuggester::arguments)
                                ))

                ));
    }

    public static void useEvent () {
        UseItemCallback.EVENT.register(Use::onUse);
    }

    public static void serverEvent () {
        Start.build();
    }

    public static void punchEvent () {
        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> Punch.onPunch(world, player, entity));
    }
}
