package mc.recraftor.enchant_decay.enchantment_decay.mixin;

import mc.recraftor.enchant_decay.enchantment_decay.EnchantmentDecay;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.function.EnchantRandomlyLootFunction;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EnchantRandomlyLootFunction.class)
public abstract class EnchantRandomlyLootFunctionMixin {
    @Redirect(
            method = "addEnchantmentToStack",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/item/ItemStack;addEnchantment(Lnet/minecraft/enchantment/Enchantment;I)V"
            )
    )
    private static void addEnchantToStackAddEnchantRedirect(ItemStack instance, Enchantment enchantment, int level) {
        NbtCompound nbt = instance.getOrCreateNbt();
        NbtList list;
        if (!nbt.contains("Enchantments") || nbt.getType("Enchantments") != NbtElement.LIST_TYPE) {
            list = new NbtList();
            nbt.put("Enchantments", list);
        } else {
            list = nbt.getList("Enchantments", NbtElement.COMPOUND_TYPE);
        }
        NbtCompound compound = EnchantmentHelper.createNbt(EnchantmentHelper.getEnchantmentId(enchantment), level);
        EnchantmentDecay.setRandomLootDecay(compound, enchantment);
        list.add(compound);
    }
}
