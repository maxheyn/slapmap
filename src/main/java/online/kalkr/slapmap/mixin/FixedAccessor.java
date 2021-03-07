package online.kalkr.slapmap.mixin;

import net.minecraft.entity.decoration.ItemFrameEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ItemFrameEntity.class)
public interface FixedAccessor {
    @Accessor("fixed")
    boolean getFixed();

    @Accessor("fixed")
    void setFixed(boolean value);
}
