package online.kalkr.slapmap.action;

import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.server.command.CommandManager;
import online.kalkr.slapmap.action.command.Give;
import online.kalkr.slapmap.action.command.Help;
import online.kalkr.slapmap.action.command.Import;
import online.kalkr.slapmap.action.server.Start;
import online.kalkr.slapmap.action.world.Punch;
import online.kalkr.slapmap.action.world.Use;

public class Register {
    public static void slapCommand () {
        Import.initArgs();
        CommandRegistrationCallback.EVENT.register((dispatcher, tank) ->
                dispatcher.register(CommandManager.literal("slap").requires(src -> src.hasPermissionLevel(2))
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
                        .then(CommandManager.argument("name", StringArgumentType.string())
                                .executes(Give::give)
                                .suggests(Give::nameSuggester)
                                .then(CommandManager.argument("args", StringArgumentType.greedyString())
                                        .executes(Import::slap)
                                        .suggests(Import::argSuggester)
                                ))

                ));
    }

    public static void useEvent () { UseItemCallback.EVENT.register(Use::onUse); }

    public static void serverEvent () { Start.build(); }

    public static void punchEvent () {
        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> Punch.onPunch(world, player, entity));
    }
}
