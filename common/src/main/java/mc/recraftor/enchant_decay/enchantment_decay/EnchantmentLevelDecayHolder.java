package mc.recraftor.enchant_decay.enchantment_decay;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.Registries;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

public class EnchantmentLevelDecayHolder extends LinkedHashMap<Enchantment, Integer> {
    private final Map<Enchantment, Integer> decayMap = new LinkedHashMap<>();

    public @Nullable Integer getDecay(Enchantment e) {
        return decayMap.get(e);
    }

    public void putDecay(Enchantment e, int value) {
        decayMap.put(e, value);
    }

    public void combineDecay(Enchantment e, Integer i) {
        if (i == null) return;
        if (i == 0) {
            decayMap.put(e, i);
            return;
        }
        if (!decayMap.containsKey(e) || decayMap.get(e) == null) decayMap.put(e, i);
        else decayMap.put(e, (decayMap.get(e) + i)/2);
    }

    public void apply(ItemStack stack) {
        if (this.isEmpty()) return;
        NbtList list = new NbtList();
        for (Enchantment e : this.keySet()) {
            if (e == null) continue;
            int level = get(e);
            Integer decay = getDecay(e);
            NbtCompound compound = EnchantmentHelper.createNbt(EnchantmentHelper.getEnchantmentId(e), level);
            if (decay != null) EnchantmentDecay.setDecay(compound, decay);
            list.add(compound);
            if (stack.isOf(Items.ENCHANTED_BOOK)) {
                EnchantedBookItem.addEnchantment(stack, new EnchantmentLevelEntry(e, level));
            }
        }
        if (list.isEmpty()) {
            stack.removeSubNbt("Enchantments");
            return;
        }
        if (!stack.isOf(Items.ENCHANTED_BOOK)) {
            stack.setSubNbt("Enchantments", list);
        }
    }

    public EnchantmentLevelDecayHolder(ItemStack stack) {
        super();
        NbtList list = stack.isOf(Items.ENCHANTED_BOOK) ? EnchantedBookItem.getEnchantmentNbt(stack) : stack.getEnchantments();
        for (int i = 0; i < list.size(); i++) {
            NbtCompound compound = list.getCompound(i);
            Registries.ENCHANTMENT.getOrEmpty(EnchantmentHelper.getIdFromNbt(compound)).ifPresent(e -> {
                put(e, EnchantmentHelper.getLevelFromNbt(compound));
                putDecay(e, EnchantmentDecay.getDecayFromNbt(compound));
            });
        }
    }
}
