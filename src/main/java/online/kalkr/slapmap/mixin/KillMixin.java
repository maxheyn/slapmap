package online.kalkr.slapmap.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.item.Items;
import net.minecraft.server.command.KillCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import online.kalkr.slapmap.Slapmap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;

@Mixin(KillCommand.class)
public class KillMixin {
    @Inject(at = @At("HEAD"), method = "execute", cancellable = true)
    private static void execute(ServerCommandSource source, Collection<? extends Entity> targets, CallbackInfoReturnable<Integer> ci) {
        Text name = null;
        int count = 0;

        for (Entity entity : targets) {
            if(!(entity.getType() == EntityType.ITEM_FRAME && ((ItemFrameEntity) entity).getHeldItemStack().getItem() == Items.FILLED_MAP && Slapmap.loadedMaps.isEntityManaged(entity))) {
                entity.kill();
                name = entity.getDisplayName();
                count++;
            }
        }

        if (count == 1) {
            source.sendFeedback(new TranslatableText("commands.kill.success.single", name), true);
        } else {
            source.sendFeedback(new TranslatableText("commands.kill.success.multiple", count), true);
        }

        ci.setReturnValue(targets.size());
    }
}
