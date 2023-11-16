package mc.recraftor.enchant_decay.enchantment_decay.mixin;

import mc.recraftor.enchant_decay.enchantment_decay.DecaySource;
import mc.recraftor.enchant_decay.enchantment_decay.EnchantmentDecay;
import net.minecraft.enchantment.ProtectionEnchantment;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ProtectionEnchantment.class)
public abstract class ProtectionEnchantmentMixin {
    @Inject(method = "transformFireDuration", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;floor(F)I"))
    private static void transformFireDurationInjector(LivingEntity entity, int duration, CallbackInfoReturnable<Integer> cir) {
        entity.getArmorItems().forEach(stack -> EnchantmentDecay.decay(stack, entity.getRandom(), DecaySource.FIRE));
    }

    @Inject(method = "transformExplosionKnockback", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;clamp(DDD)D"))
    private static void transformExplosionKnockbackInjector(LivingEntity entity, double velocity, CallbackInfoReturnable<Double> cir) {
        entity.getArmorItems().forEach(stack -> EnchantmentDecay.decay(stack, entity.getRandom(), DecaySource.BLAST));
    }
}
