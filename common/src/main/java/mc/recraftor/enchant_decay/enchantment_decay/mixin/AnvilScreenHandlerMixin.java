package mc.recraftor.enchant_decay.enchantment_decay.mixin;

import mc.recraftor.enchant_decay.enchantment_decay.EnchantmentLevelDecayHolder;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.AnvilScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Iterator;
import java.util.Map;

@Mixin(AnvilScreenHandler.class)
public abstract class AnvilScreenHandlerMixin {
    @Redirect(
            method = "updateResult",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/item/ItemStack;getRepairCost()I",
                    ordinal = 0
            )
    )
    private int updateResultGetInputRepairCostRedirect(ItemStack instance) {
        return 0;
    }

    @Redirect(
            method = "updateResult",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/item/ItemStack;getRepairCost()I",
                    ordinal = 1
            )
    )
    private int updateResultGetAdditionRepairCostItemRedirect(ItemStack instance) {
        return 0;
    }

    @Redirect(
            method = "updateResult",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/enchantment/EnchantmentHelper;get(Lnet/minecraft/item/ItemStack;)Ljava/util/Map;"
            )
    )
    private Map<Enchantment, Integer> updateResultGetInputMapRedirect(ItemStack stack) {
        return new EnchantmentLevelDecayHolder(stack);
    }

    @Inject(
            method = "updateResult",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/Map;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;",
                    shift = At.Shift.AFTER
            ),
            locals = LocalCapture.CAPTURE_FAILSOFT
    )
    private void updateResultPostPutCombinedInjector(CallbackInfo ci, ItemStack itemStack, int i, int j, int k,
                                                     ItemStack itemStack2, ItemStack itemStack3, Map map, boolean bl,
                                                     Map map2, boolean bl2, boolean bl3, Iterator var12,
                                                     Enchantment enchantment, int q, int r) {
        ((EnchantmentLevelDecayHolder) map).combineDecay(enchantment, ((EnchantmentLevelDecayHolder) map2).getDecay(enchantment));
    }

    @Redirect(
            method = "updateResult",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/enchantment/EnchantmentHelper;set(Ljava/util/Map;Lnet/minecraft/item/ItemStack;)V"
            )
    )
    private void updateResultApplyResultEnchantRedirect(Map<Enchantment, Integer> enchantments, ItemStack stack) {
        ((EnchantmentLevelDecayHolder)enchantments).apply(stack);
    }
}
