package mc.recraftor.enchant_decay.enchantment_decay.mixin;

import mc.recraftor.enchant_decay.enchantment_decay.DecaySource;
import mc.recraftor.enchant_decay.enchantment_decay.EnchantmentDecay;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(
            method = "getBlockBreakingSpeed",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/enchantment/EnchantmentHelper;hasAquaAffinity(Lnet/minecraft/entity/LivingEntity;)Z",
                    shift = At.Shift.AFTER
            )
    )
    private void getBlockBreakingSpeedInjector(BlockState block, CallbackInfoReturnable<Float> cir) {
        Enchantments.AQUA_AFFINITY.getEquipment(this).values().forEach(stack -> EnchantmentDecay.decay(stack, getRandom(), DecaySource.AQUA_AFFINITY));
    }
}
