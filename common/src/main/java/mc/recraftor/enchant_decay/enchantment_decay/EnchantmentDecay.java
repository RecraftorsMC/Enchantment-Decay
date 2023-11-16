package mc.recraftor.enchant_decay.enchantment_decay;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtInt;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryEntryList;
import net.minecraft.util.registry.RegistryKey;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

import static net.minecraft.util.registry.Registry.ENCHANTMENT;

public class EnchantmentDecay {
	public static final String MOD_ID = "enchantment_decay";
	public static final String DECAY_KEY = "decay";
	public static final String DECAY_IMMUNE_KEY = "decayImmune";
	public static final String DECAY_TOOLTIP_KEY = MOD_ID+".tooltip";
	public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

	public static void init() {
		EnchantmentDecayConfig.init();
		LOGGER.debug("Loaded mod {}", MOD_ID);
	}

	public static void decay(@NotNull ItemStack stack, Random random, DecaySource source) {
		decay(stack, random, source, 1);
	}

	public static void decay(@NotNull ItemStack stack, Random random, DecaySource source, int amount) {
		if (source == DecaySource.BLACKLIST) return;
		if (stack == null) {
			LOGGER.warn("[{}] decay called with a null stack instance", MOD_ID);
			return;
		}
		if (!stack.hasNbt()) return;
		int i = 0;
		NbtList list = stack.getEnchantments();
		while (i < list.size()) {
			NbtElement element = list.get(i);
			if (!(element instanceof NbtCompound compound)) continue;
			addDecayIfMissing(compound);
			Enchantment e = ENCHANTMENT.get(EnchantmentHelper.getIdFromNbt(compound));
			boolean b = isOfSource(e, source);
			if (b && random.nextBetween(1, 100) <= EnchantmentDecayConfig.getEnchantmentDecayProbability(e)) {
				int l = decay(compound, e, amount);
				if (l <= 0) {
					list.remove(i);
					continue;
				}
			}
			i++;
		}
		if (list.isEmpty()) {
			stack.removeSubNbt("Enchantments");
		}
	}

	private static boolean isOfSource(Enchantment enchantment, @NotNull DecaySource source) {
		Optional<RegistryEntryList.Named<Enchantment>> entryList = ENCHANTMENT.getEntryList(source.getTag());
		if (entryList.isEmpty()) return false;
		for (RegistryEntry<Enchantment> entry : entryList.get()) {
			if (entry.getKey().isPresent()) {
				return ENCHANTMENT.getId(enchantment).equals(entry.getKey().get().getValue());
			}
		}
		return false;
	}

	private static int decay(NbtCompound compound, Enchantment enchantment, int amount) {
		if (isDecayImmune(compound) || isOfSource(enchantment, DecaySource.BLACKLIST)) return EnchantmentHelper.getLevelFromNbt(compound);
		addDecayIfMissing(compound);
		int decay = compound.getInt(DECAY_KEY);
		decay += amount;
		if (decay >= EnchantmentDecayConfig.getEnchantmentMaxDecay(enchantment)) {
			compound.putInt(DECAY_KEY, 0);
			int level = EnchantmentHelper.getLevelFromNbt(compound)-1;
			EnchantmentHelper.writeLevelToNbt(compound, level);
			return level;
		}
		compound.putInt(DECAY_KEY, decay);
		return EnchantmentHelper.getLevelFromNbt(compound);
	}

	public static void addDecayIfMissing(@NotNull NbtCompound compound) {
		if (!compound.contains(DECAY_KEY, NbtElement.INT_TYPE)) {
			compound.put(DECAY_KEY, NbtInt.of(0));
		}
	}

	public static void setDecay(@NotNull NbtCompound compound, int value) {
		compound.put(DECAY_KEY, NbtInt.of(value));
	}

	public static void setRandomLootDecay(NbtCompound nbt, Enchantment e) {
        Random random = Random.create();
		boolean isImmune = random.nextBetween(1, 100) <= EnchantmentDecayConfig.getLootDecayImmuneProbability();
		boolean isDecayed = random.nextBetween(1, 100) <= EnchantmentDecayConfig.getLootDecayProbability();
		if (isImmune) {
			EnchantmentDecay.setDecayImmune(nbt, true);
		} else if (isDecayed) {
			int decayMax = EnchantmentDecayConfig.getLootMaxDecayProportion() * EnchantmentDecayConfig.getEnchantmentMaxDecay(e) / 100;
			if (decayMax > 0) {
				EnchantmentDecay.setDecay(nbt, random.nextBetween(1, decayMax));
			}
		}
	}

	public static boolean isDecayImmune(@NotNull NbtCompound compound) {
		if (!compound.contains(DECAY_IMMUNE_KEY, NbtElement.BYTE_TYPE)) return false;
		return compound.getBoolean(DECAY_IMMUNE_KEY);
	}

	public static void setDecayImmune(@NotNull NbtCompound compound, boolean b) {
		compound.putBoolean(DECAY_IMMUNE_KEY, b);
	}

	public static boolean hasPositiveDecay(@NotNull NbtCompound compound) {
		return compound.getInt(DECAY_KEY) > 0;
	}

	public static int getDecayFromNbt(@NotNull NbtCompound compound) {
		return compound.getInt(DECAY_KEY);
	}

	public static int getDecay(ItemStack stack, Enchantment enchantment) {
		NbtList list = stack.getEnchantments();
		if (list.isEmpty()) return 0;
		Optional<RegistryKey<Enchantment>> opt = ENCHANTMENT.getKey(enchantment);
		if (opt.isEmpty()) return 0;
		Identifier id = opt.get().getValue();
		for (int i = 0; i < list.size(); i++) {
			NbtCompound compound = list.getCompound(i);
			if (compound == null) continue;
			if (id.equals(EnchantmentHelper.getIdFromNbt(compound))) return getDecayFromNbt(compound);
		}
		return 0;
	}

	@Contract("_ -> new")
	public static @NotNull Identifier id(String s) {
		return new Identifier(MOD_ID, s);
	}
}
