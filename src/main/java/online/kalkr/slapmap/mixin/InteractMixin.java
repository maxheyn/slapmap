package online.kalkr.slapmap.mixin;

import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import online.kalkr.slapmap.Slapmap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(ItemFrameEntity.class)
public class InteractMixin {

    @Inject(method = "interact", at = @At(value = "HEAD"))
    private void mixTop(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        ItemFrameEntity dis = (ItemFrameEntity)(Object) this;
        ((FixedAccessor) dis).setFixed(false);
    }

    @Inject(method = "interact", at = @At(value = "TAIL"))
    private void mixBot(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        ItemFrameEntity dis = (ItemFrameEntity)(Object) this;

        boolean isManaged = Slapmap.loadedMaps.isEntityManaged(dis);
        if (isManaged && dis.isInvisible()) {
            ((FixedAccessor) dis).setFixed(true);
        }
    }
}