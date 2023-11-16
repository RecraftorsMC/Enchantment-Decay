package mc.recraftor.enchant_decay.enchantment_decay.mixin;

import mc.recraftor.enchant_decay.enchantment_decay.DecaySource;
import mc.recraftor.enchant_decay.enchantment_decay.EnchantmentDecay;
import mc.recraftor.enchant_decay.enchantment_decay.accessor.RandomAccessor;
import mc.recraftor.enchant_decay.enchantment_decay.accessor.StackAccessor;
import net.minecraft.block.BlockState;
import net.minecraft.block.LightningRodBlock;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LightningRodBlock.class)
public abstract class LightingRodBlockMixin {
    @Inject(
            method = "onProjectileHit",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/EntityType;create(Lnet/minecraft/world/World;)Lnet/minecraft/entity/Entity;",
                    shift = At.Shift.BEFORE
            )
    )
    private void onProjectileHitTridentChanneling(World world, BlockState state, BlockHitResult hit,
                                                  ProjectileEntity projectile, CallbackInfo ci) {
        if (projectile instanceof TridentEntity trident) {
            EnchantmentDecay.decay(((StackAccessor)trident).decay$getStack(), ((RandomAccessor)projectile).decay$getRandom(), DecaySource.MAGIC);
        }
    }
}
