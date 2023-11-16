package mc.recraftor.enchant_decay.enchantment_decay.mixin;

import mc.recraftor.enchant_decay.enchantment_decay.DecaySource;
import mc.recraftor.enchant_decay.enchantment_decay.EnchantmentDecay;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TextContent;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
    @Shadow public abstract int getDamage();

    @Inject(
            method = "damage(ILnet/minecraft/util/math/random/Random;Lnet/minecraft/server/network/ServerPlayerEntity;)Z",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getDamage()I"),
            locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    private void damageTailGetDamageDecayInjector(int amount, Random random, ServerPlayerEntity player, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValueZ()) {
            EnchantmentDecay.decay((ItemStack) ((Object)this), random, DecaySource.DAMAGE);
        }
    }

    @Inject(
            method = "damage(ILnet/minecraft/util/math/random/Random;Lnet/minecraft/server/network/ServerPlayerEntity;)Z",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/enchantment/UnbreakingEnchantment;shouldPreventDamage(Lnet/minecraft/item/ItemStack;ILnet/minecraft/util/math/random/Random;)Z",
                    shift = At.Shift.AFTER
            )
    )
    private void damagePreventedInjector(int amount, Random random, ServerPlayerEntity player,
                                         CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValueZ()) {
            EnchantmentDecay.decay((ItemStack) ((Object)this), random, DecaySource.RESIST_DAMAGE);
        }
    }

    @Inject(method = "setDamage", at = @At("HEAD"))
    private void setDamageHeadInjector(int damage, CallbackInfo ci) {
        if (damage < getDamage()) {
            EnchantmentDecay.decay((ItemStack) ((Object) this), Random.create(), DecaySource.REPAIR);
        }
    }

    @Inject(
            method = "method_17869",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/List;add(Ljava/lang/Object;)Z",
                    shift = At.Shift.BEFORE
            ),
            cancellable = true,
            locals = LocalCapture.CAPTURE_FAILSOFT
    )
    private static void appendEnchantmentsGetNameInjector(List<Text> list, NbtCompound nbtCompound,
                                                          Enchantment e, CallbackInfo ci) {
        if (EnchantmentDecay.isDecayImmune(nbtCompound) && !e.isCursed()) {
            MutableText vanilla = Text.translatable(e.getTranslationKey()).formatted(e.isCursed() ? Formatting.RED : Formatting.GRAY);
            list.add(MutableText.of(TextContent.EMPTY).append(Text.literal("* ").formatted(Formatting.GREEN)).append(vanilla));
            ci.cancel();
            return;
        }
        if (!EnchantmentDecay.hasPositiveDecay(nbtCompound)) return;
        MutableText vanilla = Text.translatable(e.getTranslationKey()).formatted(e.isCursed() ? Formatting.RED : Formatting.GRAY);
        int level = EnchantmentHelper.getLevelFromNbt(nbtCompound);
        if (level > 1 || e.getMaxLevel() != 1) {
            vanilla.append(" ").append(Text.translatable("enchantment.level." + level));
        }
        MutableText custom = Text.translatable(EnchantmentDecay.DECAY_TOOLTIP_KEY, EnchantmentDecay.getDecayFromNbt(nbtCompound));
        list.add(vanilla.append(Text.literal("  ")).append(custom.formatted(Formatting.DARK_GRAY)));
        ci.cancel();
    }
}
