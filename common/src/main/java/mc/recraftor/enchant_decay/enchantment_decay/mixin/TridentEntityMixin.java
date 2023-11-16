package mc.recraftor.enchant_decay.enchantment_decay.mixin;

import mc.recraftor.enchant_decay.enchantment_decay.DecaySource;
import mc.recraftor.enchant_decay.enchantment_decay.EnchantmentDecay;
import mc.recraftor.enchant_decay.enchantment_decay.accessor.RandomAccessor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TridentEntity.class)
public abstract class TridentEntityMixin extends Entity {

    @Shadow private ItemStack tridentStack;

    TridentEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "<init>(Lnet/minecraft/world/World;Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;)V", at = @At("TAIL"))
    private void onInitWithStack(World world, LivingEntity owner, ItemStack stack, CallbackInfo ci) {
        EnchantmentDecay.decay(stack, ((RandomAccessor) this).decay$getRandom(), DecaySource.SHOOT);
    }

    @Inject(
            method = "onEntityHit",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/EntityType;create(Lnet/minecraft/world/World;)Lnet/minecraft/entity/Entity;",
                    shift = At.Shift.BEFORE
            )
    )
    private void onEntityHitChannelingInjector(EntityHitResult entityHitResult, CallbackInfo ci) {
        EnchantmentDecay.decay(tridentStack, ((RandomAccessor)this).decay$getRandom(), DecaySource.MAGIC);
    }
}
