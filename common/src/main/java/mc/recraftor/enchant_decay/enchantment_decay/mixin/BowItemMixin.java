package mc.recraftor.enchant_decay.enchantment_decay.mixin;

import mc.recraftor.enchant_decay.enchantment_decay.DecaySource;
import mc.recraftor.enchant_decay.enchantment_decay.EnchantmentDecay;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(BowItem.class)
public abstract class BowItemMixin {
    @Inject(
            method = "onStoppedUsing",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/projectile/PersistentProjectileEntity;setDamage(D)V",
                    shift = At.Shift.AFTER
            ),
            locals = LocalCapture.CAPTURE_FAILEXCEPTION
    )
    private void onStoppedUsingSetDamageInjector(ItemStack stack, World world, LivingEntity user, int remainingUseTicks,
                                                 CallbackInfo ci, PlayerEntity playerEntity, boolean bl,
                                                 ItemStack itemStack, int i, float f, boolean bl2, ArrowItem arrowItem,
                                                 PersistentProjectileEntity persistentProjectileEntity, int j) {
        EnchantmentDecay.decay(stack, user.getRandom(), DecaySource.SHOOT, user.getRandom().nextBetween(1, j));
    }

    @Inject(
            method = "onStoppedUsing",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/projectile/PersistentProjectileEntity;setPunch(I)V",
                    shift = At.Shift.AFTER
            ),
            locals = LocalCapture.CAPTURE_FAILEXCEPTION
    )
    private void onStoppedUsingSetPunchInjector(ItemStack stack, World world, LivingEntity user, int remainingUseTicks,
                                                 CallbackInfo ci, PlayerEntity playerEntity, boolean bl,
                                                 ItemStack itemStack, int i, float f, boolean bl2, ArrowItem arrowItem,
                                                 PersistentProjectileEntity persistentProjectileEntity, int j) {
        EnchantmentDecay.decay(stack, user.getRandom(), DecaySource.SHOOT, user.getRandom().nextBetween(1, j));
    }

    @Inject(
            method = "onStoppedUsing",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/projectile/PersistentProjectileEntity;setOnFireFor(I)V",
                    shift = At.Shift.AFTER
            ),
            locals = LocalCapture.CAPTURE_FAILEXCEPTION
    )
    private void onStoppedUsingSetOnFireInjector(ItemStack stack, World world, LivingEntity user, int remainingUseTicks,
                                                 CallbackInfo ci, PlayerEntity playerEntity, boolean bl,
                                                 ItemStack itemStack, int i, float f, boolean bl2, ArrowItem arrowItem,
                                                 PersistentProjectileEntity persistentProjectileEntity, int j, int k) {
        EnchantmentDecay.decay(stack, user.getRandom(), DecaySource.SHOOT);
    }

    @Inject(
            method = "onStoppedUsing",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/player/PlayerEntity;getArrowType(Lnet/minecraft/item/ItemStack;)Lnet/minecraft/item/ItemStack;",
                    shift = At.Shift.BEFORE
            ),
            locals = LocalCapture.CAPTURE_FAILEXCEPTION
    )
    private void onStoppedUsingInfinityInjector(ItemStack stack, World world, LivingEntity user, int remainingUseTicks,
                                                CallbackInfo ci, PlayerEntity playerEntity, boolean bl) {
        if (bl && !playerEntity.getAbilities().creativeMode) EnchantmentDecay.decay(stack, user.getRandom(), DecaySource.SHOOT);
    }
}
