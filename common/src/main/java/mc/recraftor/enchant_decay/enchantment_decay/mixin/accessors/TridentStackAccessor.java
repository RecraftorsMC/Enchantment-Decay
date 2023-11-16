package mc.recraftor.enchant_decay.enchantment_decay.mixin.accessors;

import mc.recraftor.enchant_decay.enchantment_decay.accessor.StackAccessor;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(TridentEntity.class)
public abstract class TridentStackAccessor implements StackAccessor {
    @Shadow private ItemStack tridentStack;

    @Override
    public ItemStack decay$getStack() {
        return tridentStack;
    }
}
