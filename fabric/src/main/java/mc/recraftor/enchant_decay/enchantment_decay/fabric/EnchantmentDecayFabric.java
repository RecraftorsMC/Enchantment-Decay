package mc.recraftor.enchant_decay.enchantment_decay.fabric;

import mc.recraftor.enchant_decay.enchantment_decay.EnchantmentDecay;
import net.fabricmc.api.ModInitializer;

public class EnchantmentDecayFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        EnchantmentDecay.init();
    }
}