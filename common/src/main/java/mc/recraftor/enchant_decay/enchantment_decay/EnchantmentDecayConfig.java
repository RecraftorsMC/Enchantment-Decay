package mc.recraftor.enchant_decay.enchantment_decay;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.util.Identifier;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public final class EnchantmentDecayConfig {
    private EnchantmentDecayConfig() {}

    private static final Path PATH;
    private static final Properties PROPERTIES;
    private static final String COMMENTS = """
            Properties file for the Enchantment Decay mod.
            By default, only some vanilla enchant have max decay and decay probability overrides, as they may decay much
            faster, depending on their nature. E.g. the depth strider enchantment may decay at every tick if in water
            """;
    private static final String BASE_DECAY_PROBABILITY_KEY = "decay.probability.%s.%s";
    private static final String BASE_ENCHANT_MAX_DECAY_KEY = "decay.max.%s.%s";
    private static final String DEFAULT_PROBABILITY_KEY = "decay.probability.default";
    private static final String DEFAULT_MAX_DECAY_KEY = "decay.max.default";
    private static final String LOOT_IMMUNE_DECAY_PROBABILITY_KEY = "loot.decay.immune.probability";
    private static final String LOOT_DECAY_PROBABILITY_KEY = "loot.decay.probability";
    private static final String LOOT_DECAY_MAX_PROPORTION_KEY = "loot.decay.max_percentile";
    private static final int DEFAULT_MAX_DECAY_VALUE = 20;
    private static final int DEFAULT_DECAY_PROBABILITY_VALUE = 10;
    private static final int DEFAULT_LOOT_IMMUNE_DECAY_PROBABILITY = 15;
    private static final int DEFAULT_LOOT_DECAY_PROBABILITY = 30;
    private static final int DEFAULT_LOOT_DECAY_MAX_PROPORTION = 50;

    static {
        PATH = new File("config", EnchantmentDecay.MOD_ID+".properties").toPath();

        Properties defaults = new Properties();
        defaults.setProperty(LOOT_IMMUNE_DECAY_PROBABILITY_KEY, String.valueOf(DEFAULT_LOOT_IMMUNE_DECAY_PROBABILITY));
        defaults.setProperty(LOOT_DECAY_PROBABILITY_KEY, String.valueOf(DEFAULT_LOOT_DECAY_PROBABILITY));
        defaults.setProperty(LOOT_DECAY_MAX_PROPORTION_KEY, String.valueOf(DEFAULT_LOOT_DECAY_MAX_PROPORTION));
        defaults.setProperty(DEFAULT_MAX_DECAY_KEY, String.valueOf(DEFAULT_MAX_DECAY_VALUE));
        defaults.setProperty(DEFAULT_PROBABILITY_KEY, String.valueOf(DEFAULT_DECAY_PROBABILITY_VALUE));
        defaults.setProperty(getEnchantDecayProbabilityKey(Enchantments.EFFICIENCY), "8");
        defaults.setProperty(getEnchantMaxDecayKey(Enchantments.EFFICIENCY), "50");
        defaults.setProperty(getEnchantMaxDecayKey(Enchantments.PROTECTION), "30");
        defaults.setProperty(getEnchantMaxDecayKey(Enchantments.FIRE_PROTECTION), "30");
        defaults.setProperty(getEnchantMaxDecayKey(Enchantments.FEATHER_FALLING), "30");
        defaults.setProperty(getEnchantMaxDecayKey(Enchantments.BLAST_PROTECTION), "30");
        defaults.setProperty(getEnchantMaxDecayKey(Enchantments.PROJECTILE_PROTECTION), "30");
        defaults.setProperty(getEnchantMaxDecayKey(Enchantments.THORNS), "50");
        defaults.setProperty(getEnchantDecayProbabilityKey(Enchantments.DEPTH_STRIDER), "5");
        defaults.setProperty(getEnchantMaxDecayKey(Enchantments.DEPTH_STRIDER), "100");
        defaults.setProperty(getEnchantDecayProbabilityKey(Enchantments.SOUL_SPEED), "8");
        defaults.setProperty(getEnchantMaxDecayKey(Enchantments.SOUL_SPEED), "50");
        defaults.setProperty(getEnchantDecayProbabilityKey(Enchantments.SWIFT_SNEAK), "8");
        defaults.setProperty(getEnchantMaxDecayKey(Enchantments.SWIFT_SNEAK), "50");
        defaults.setProperty(getEnchantMaxDecayKey(Enchantments.UNBREAKING), "50");

        PROPERTIES = new Properties(defaults);
        PROPERTIES.setProperty(LOOT_IMMUNE_DECAY_PROBABILITY_KEY, String.valueOf(DEFAULT_LOOT_IMMUNE_DECAY_PROBABILITY));
        PROPERTIES.setProperty(LOOT_DECAY_PROBABILITY_KEY, String.valueOf(DEFAULT_LOOT_DECAY_PROBABILITY));
        PROPERTIES.setProperty(LOOT_DECAY_MAX_PROPORTION_KEY, String.valueOf(DEFAULT_LOOT_DECAY_MAX_PROPORTION));
        PROPERTIES.setProperty(DEFAULT_MAX_DECAY_KEY, String.valueOf(DEFAULT_MAX_DECAY_VALUE));
        PROPERTIES.setProperty(DEFAULT_PROBABILITY_KEY, String.valueOf(DEFAULT_DECAY_PROBABILITY_VALUE));
        PROPERTIES.setProperty(getEnchantDecayProbabilityKey(Enchantments.EFFICIENCY), "8");
        PROPERTIES.setProperty(getEnchantMaxDecayKey(Enchantments.EFFICIENCY), "50");
        PROPERTIES.setProperty(getEnchantMaxDecayKey(Enchantments.PROTECTION), "30");
        PROPERTIES.setProperty(getEnchantMaxDecayKey(Enchantments.FIRE_PROTECTION), "30");
        PROPERTIES.setProperty(getEnchantMaxDecayKey(Enchantments.FEATHER_FALLING), "30");
        PROPERTIES.setProperty(getEnchantMaxDecayKey(Enchantments.BLAST_PROTECTION), "30");
        PROPERTIES.setProperty(getEnchantMaxDecayKey(Enchantments.PROJECTILE_PROTECTION), "30");
        PROPERTIES.setProperty(getEnchantMaxDecayKey(Enchantments.THORNS), "50");
        PROPERTIES.setProperty(getEnchantDecayProbabilityKey(Enchantments.DEPTH_STRIDER), "1");
        PROPERTIES.setProperty(getEnchantMaxDecayKey(Enchantments.DEPTH_STRIDER), "100");
        PROPERTIES.setProperty(getEnchantDecayProbabilityKey(Enchantments.SWIFT_SNEAK), "8");
        PROPERTIES.setProperty(getEnchantMaxDecayKey(Enchantments.SWIFT_SNEAK), "50");
        PROPERTIES.setProperty(getEnchantMaxDecayKey(Enchantments.UNBREAKING), "50");

        try {
            if (!(Files.exists(PATH) && Files.isRegularFile(PATH) && Files.isReadable(PATH))) {
                if (PATH.toFile().createNewFile())
                    EnchantmentDecay.LOGGER.info("Created {} config file", EnchantmentDecay.MOD_ID);
                else EnchantmentDecay.LOGGER.error("Couldn't create {} config file", EnchantmentDecay.MOD_ID);
                PROPERTIES.store(new FileWriter(PATH.toFile()), COMMENTS);
            } else {
                PROPERTIES.load(new FileReader(PATH.toFile()));
            }
        } catch (IOException e) {
            EnchantmentDecay.LOGGER.error(e);
        }
    }

    public static int getLootDecayImmuneProbability() {
        try {
            return Integer.parseInt(PROPERTIES.getProperty(LOOT_IMMUNE_DECAY_PROBABILITY_KEY));
        } catch (NumberFormatException e) {
            PROPERTIES.setProperty(LOOT_IMMUNE_DECAY_PROBABILITY_KEY, String.valueOf(DEFAULT_LOOT_IMMUNE_DECAY_PROBABILITY));
            return DEFAULT_LOOT_IMMUNE_DECAY_PROBABILITY;
        }
    }

    public static int getLootDecayProbability() {
        try {
            return Integer.parseInt(PROPERTIES.getProperty(LOOT_DECAY_PROBABILITY_KEY));
        } catch (NumberFormatException e) {
            PROPERTIES.setProperty(LOOT_DECAY_PROBABILITY_KEY, String.valueOf(DEFAULT_LOOT_DECAY_PROBABILITY));
            return DEFAULT_LOOT_DECAY_PROBABILITY;
        }
    }

    public static int getLootMaxDecayProportion() {
        try {
            return Math.max(0, Math.min(99, Integer.parseInt(PROPERTIES.getProperty(LOOT_DECAY_MAX_PROPORTION_KEY))));
        } catch (NumberFormatException e) {
            PROPERTIES.setProperty(LOOT_DECAY_MAX_PROPORTION_KEY, String.valueOf(DEFAULT_LOOT_DECAY_MAX_PROPORTION));
            return DEFAULT_LOOT_DECAY_MAX_PROPORTION;
        }
    }

    public static int getDefaultMaxDecay() {
        try {
            return Integer.parseInt(PROPERTIES.getProperty(DEFAULT_MAX_DECAY_KEY));
        } catch (NumberFormatException e) {
            PROPERTIES.setProperty(DEFAULT_MAX_DECAY_KEY, String.valueOf(DEFAULT_MAX_DECAY_VALUE));
            return DEFAULT_MAX_DECAY_VALUE;
        }
    }

    public static int getDefaultDecayProbability() {
        try {
            return Integer.parseInt(PROPERTIES.getProperty(DEFAULT_PROBABILITY_KEY));
        } catch (NumberFormatException e) {
            PROPERTIES.setProperty(DEFAULT_PROBABILITY_KEY, String.valueOf(DEFAULT_DECAY_PROBABILITY_VALUE));
            return DEFAULT_DECAY_PROBABILITY_VALUE;
        }
    }

    private static String getEnchantMaxDecayKey(Enchantment enchantment) {
        Identifier id = EnchantmentHelper.getEnchantmentId(enchantment);
        return String.format(BASE_ENCHANT_MAX_DECAY_KEY, id.getNamespace(), id.getPath());
    }

    private static String getEnchantDecayProbabilityKey(Enchantment enchantment) {
        Identifier id = EnchantmentHelper.getEnchantmentId(enchantment);
        return String.format(BASE_DECAY_PROBABILITY_KEY, id.getNamespace(), id.getPath());
    }

    public static int getEnchantmentMaxDecay(Enchantment enchantment) {
        String key = getEnchantMaxDecayKey(enchantment);
        if (!PROPERTIES.containsKey(key)) {
            try {
                return Integer.parseInt(PROPERTIES.getProperty(key));
            } catch (NumberFormatException e) {
                PROPERTIES.setProperty(key, String.valueOf(getDefaultMaxDecay()));
            }
        }
        return getDefaultMaxDecay();
    }

    public static int getEnchantmentDecayProbability(Enchantment enchantment) {
        String key = getEnchantDecayProbabilityKey(enchantment);
        if (!PROPERTIES.containsKey(key)) {
            try {
                return Integer.parseInt(PROPERTIES.getProperty(key));
            } catch (NumberFormatException e) {
                PROPERTIES.setProperty(key, String.valueOf(getDefaultDecayProbability()));
            }
        }
        return getDefaultDecayProbability();
    }

    static void init() {}

    public static void save() throws IOException {
        try (FileWriter writer = new FileWriter(PATH.toFile())) {
            PROPERTIES.store(writer, COMMENTS);
        }
    }
}
