package mc.recraftor.enchant_decay.enchantment_decay.mixin;

import mc.recraftor.enchant_decay.enchantment_decay.DecaySource;
import mc.recraftor.enchant_decay.enchantment_decay.EnchantmentDecay;
import mc.recraftor.enchant_decay.enchantment_decay.accessor.RandomAccessor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FishingBobberEntity.class)
public abstract class FishingBobberEntityMixin extends ProjectileEntity {
    FishingBobberEntityMixin(EntityType<? extends ProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Shadow @Nullable public abstract PlayerEntity getPlayerOwner();

    @Shadow @Final private int luckOfTheSeaLevel;

    @Shadow @Final private int lureLevel;

    @Inject(
            method = "use",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/advancement/criterion/FishingRodHookedCriterion;trigger(Lnet/minecraft/server/network/ServerPlayerEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/projectile/FishingBobberEntity;Ljava/util/Collection;)V",
                    shift = At.Shift.BEFORE
            )
    )
    private void onCompleteUseInjector(ItemStack usedItem, CallbackInfoReturnable<Integer> cir) {
        int i = (luckOfTheSeaLevel > 0 ? 1 : 0) + (lureLevel > 0 ? 1 : 0);
        Entity owner = getOwner();
        if (i > 0 && owner != null) EnchantmentDecay.decay(usedItem, ((RandomAccessor)owner).decay$getRandom(), DecaySource.FISHING, i);
    }
}
