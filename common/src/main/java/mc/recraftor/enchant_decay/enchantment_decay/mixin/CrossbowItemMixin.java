package mc.recraftor.enchant_decay.enchantment_decay.mixin;

import mc.recraftor.enchant_decay.enchantment_decay.DecaySource;
import mc.recraftor.enchant_decay.enchantment_decay.EnchantmentDecay;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(CrossbowItem.class)
public abstract class CrossbowItemMixin {

    @Inject(
            method = "loadProjectiles",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/item/ItemStack;copy()Lnet/minecraft/item/ItemStack;",
                    ordinal = 1,
                    shift = At.Shift.BEFORE
            ),
            locals = LocalCapture.CAPTURE_FAILSOFT
    )
    private static void loadProjectilesLoadMultishot(LivingEntity shooter, ItemStack projectile,
                                                     CallbackInfoReturnable<Boolean> cir, int i, int j, boolean bl,
                                                     ItemStack itemStack, ItemStack itemStack2, int k) {
        Random random = shooter != null ? shooter.getRandom() : Random.create();
        EnchantmentDecay.decay(projectile, random, DecaySource.SHOOT, k);
    }

    @Inject(
            method = "usageTick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/item/CrossbowItem;getPullTime(Lnet/minecraft/item/ItemStack;)I",
                    shift = At.Shift.AFTER
            ),
            locals = LocalCapture.CAPTURE_FAILSOFT
    )
    private void usageTickGetPullTimeInjector(World world, LivingEntity user, ItemStack stack, int remainingUseTicks,
                                              CallbackInfo ci, int i, SoundEvent soundEvent, SoundEvent soundEvent2) {
        if (i > 0 && CrossbowItem.getPullTime(stack) != CrossbowItem.getPullTime(new ItemStack(stack.getItem()))) {
            EnchantmentDecay.decay(stack, user.getRandom(), DecaySource.LOAD);
        }
    }

    @Inject(
            method = "createArrow",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/projectile/PersistentProjectileEntity;setPierceLevel(B)V"
            )
    )
    private static void createArrowPiercingInjector(World world, LivingEntity entity, ItemStack crossbow,
                                                    ItemStack arrow, CallbackInfoReturnable<PersistentProjectileEntity> cir) {
        EnchantmentDecay.decay(crossbow, entity.getRandom(), DecaySource.SHOOT);
    }
}
