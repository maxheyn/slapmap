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
    private void interactTop(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> returnable) {
        ItemFrameEntity self = (ItemFrameEntity) (Object) this;
        ((FixedAccessor) self).setFixed(false);
    }

    @Inject(method = "interact", at = @At(value = "TAIL"))
    private void interactBottom(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> returnable) {
        ItemFrameEntity self = (ItemFrameEntity) (Object) this;

        boolean isManaged = Slapmap.mapManager.isIdManaged(Slapmap.mapManager.getIdFromEntity(self));
        if (isManaged && self.isInvisible()) {
            ((FixedAccessor) self).setFixed(true);
        }
    }
}