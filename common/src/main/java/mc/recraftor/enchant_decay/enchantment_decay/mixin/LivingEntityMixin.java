package mc.recraftor.enchant_decay.enchantment_decay.mixin;

import mc.recraftor.enchant_decay.enchantment_decay.DecaySource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import static mc.recraftor.enchant_decay.enchantment_decay.EnchantmentDecay.decay;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Shadow public abstract Random getRandom();

    @Shadow public abstract Iterable<ItemStack> getArmorItems();

    @Inject(method = "onKilledBy", at = @At("TAIL"))
    private void onKilledByTailInjector(LivingEntity adversary, CallbackInfo ci) {
        decay(adversary.getMainHandStack(), this.getRandom(), DecaySource.KILL);
    }

    @Inject(method = "modifyAppliedDamage", at = @At("TAIL"))
    private void modifyAppliedDamageTailInjector(DamageSource source, float amount, CallbackInfoReturnable<Float> cir) {
        DecaySource decay;
        //TODO: damage source tags
        if (source.isOf(DamageTypes.IN_FIRE) || source.isOf(DamageTypes.ON_FIRE) || source.isOf(DamageTypes.FIREBALL)) decay = DecaySource.FIRE;
        else if (source.isOf(DamageTypes.FALL)) decay = DecaySource.FALL;
        else if (source.isOf(DamageTypes.EXPLOSION) || source.isOf(DamageTypes.PLAYER_EXPLOSION)) decay = DecaySource.BLAST;
        else if (source.isOf(DamageTypes.MOB_PROJECTILE) || source.isOf(DamageTypes.ARROW)) decay = DecaySource.GET_SHOT;
        else decay = null;

        getArmorItems().forEach(stack -> {
            decay(stack, getRandom(), DecaySource.HURT);
            if (decay != null) {
                decay(stack, getRandom(), decay);
            }
        });
    }

    @Inject(
            method = "getNextAirUnderwater",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/math/random/Random;nextInt(I)I",
                    shift = At.Shift.AFTER),
            locals = LocalCapture.CAPTURE_FAILEXCEPTION
    )
    private void getNextAirUnderwaterInjector(int air, CallbackInfoReturnable<Integer> cir, int i) {
        getArmorItems().forEach(stack -> decay(stack, getRandom(), DecaySource.BREATHING, cir.getReturnValue()));
    }

    @Inject(method = "travel",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/LivingEntity;getMovementSpeed()F",
                    shift = At.Shift.AFTER),
            locals = LocalCapture.CAPTURE_FAILEXCEPTION
    )
    private void travelGetMovementSpeedInjector(Vec3d movementInput, CallbackInfo ci, double d, boolean bl,
                                                FluidState fluidState, double e, float f, float g, float h) {
        getArmorItems().forEach(stack -> decay(stack, getRandom(), DecaySource.WATER_MOVEMENT, (int) h));
    }

    @Inject(
            method = "travel",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/LivingEntity;applyMovementInput(Lnet/minecraft/util/math/Vec3d;F)Lnet/minecraft/util/math/Vec3d;"
            )
    )
    private void travelApplyMovementInputInjector(Vec3d movementInput, CallbackInfo ci) {
        if (isSneaking()) getArmorItems().forEach(stack -> decay(stack, getRandom(), DecaySource.SNEAK));
    }

    @Inject(
            method = "applyMovementEffects",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/enchantment/FrostWalkerEnchantment;freezeWater(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;I)V",
                    shift = At.Shift.AFTER
            ),
            locals = LocalCapture.CAPTURE_FAILEXCEPTION
    )
    private void applyMovementEffectsFreezeWaterInjector(BlockPos pos, CallbackInfo ci, int i) {
        getArmorItems().forEach(stack -> decay(stack, getRandom(), DecaySource.FROST_WALK, i));
    }

    @Inject(
            method = "getVelocityMultiplier",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/enchantment/EnchantmentHelper;getEquipmentLevel(Lnet/minecraft/enchantment/Enchantment;Lnet/minecraft/entity/LivingEntity;)I",
                    shift = At.Shift.AFTER
            )
    )
    private void getVelocityMultiplierGetSoulSpeedLevelInjector(CallbackInfoReturnable<Float> cir) {
        if (cir.getReturnValueZ()) getArmorItems().forEach(stack -> decay(stack, getRandom(), DecaySource.SOUL_WALK));
    }

    @Inject(
            method = "damage",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/LivingEntity;applyDamage(Lnet/minecraft/entity/damage/DamageSource;F)V"
            )
    )
    private void damageApplyDamageInjector(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (!(source.getAttacker() instanceof LivingEntity entity)) return;
        decay(entity.getMainHandStack(), entity.getRandom(), DecaySource.ATTACK);
    }
}
