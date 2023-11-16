package mc.recraftor.enchant_decay.enchantment_decay.mixin;

import mc.recraftor.enchant_decay.enchantment_decay.DecaySource;
import mc.recraftor.enchant_decay.enchantment_decay.EnchantmentDecay;
import net.minecraft.enchantment.ThornsEnchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ThornsEnchantment.class)
public abstract class ThornsEnchantmentMixin {
    @Inject(
            method = "onUserDamaged",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/enchantment/ThornsEnchantment;getDamageAmount(ILnet/minecraft/util/math/random/Random;)I",
                    shift = At.Shift.AFTER
            )
    )
    private void onUserDamagedInjector(LivingEntity user, Entity attacker, int level, CallbackInfo ci) {
        user.getArmorItems().forEach(stack -> EnchantmentDecay.decay(stack, user.getRandom(), DecaySource.THORNS));
    }
}
