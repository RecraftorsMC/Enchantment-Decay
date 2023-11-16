package mc.recraftor.enchant_decay.enchantment_decay.mixin.accessors;

import mc.recraftor.enchant_decay.enchantment_decay.accessor.RandomAccessor;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Entity.class)
public abstract class EntityRandomAccessor implements RandomAccessor {
    @Shadow @Final protected Random random;

    @Override
    public Random decay$getRandom() {
        return this.random;
    }
}
