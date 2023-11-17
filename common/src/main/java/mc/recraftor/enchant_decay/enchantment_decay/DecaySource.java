package mc.recraftor.enchant_decay.enchantment_decay;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.*;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.TagKey;

import java.util.*;

public final class DecaySource {
    private final String id;
    private final TagKey<Enchantment> tag;
    private final TagKey<DamageType> damageSource;
    private static final Map<String, DecaySource> sourceMap = new TreeMap<>();

    public static final DecaySource AQUA_AFFINITY = register("aqua_affinity");
    public static final DecaySource ATTACK = register("attack");
    public static final DecaySource BLAST = register("blast");
    public static final DecaySource BURN_WALKING = register("burn_walking");
    public static final DecaySource BREATHING = register("breathing");
    public static final DecaySource DAMAGE = register("damage");
    @SuppressWarnings("unused")
    public static final DecaySource FALL = register("fall");
    public static final DecaySource FIRE = register("fire");
    public static final DecaySource FISHING = register("fishing");
    public static final DecaySource FROST_WALK = register("frost_walk");
    @SuppressWarnings("unused")
    public static final DecaySource GET_SHOT = register("get_shot");
    public static final DecaySource HURT = register("hurt");
    public static final DecaySource JUMP = register("jump");
    public static final DecaySource KILL = register("kill");
    public static final DecaySource LOAD = register("load");
    public static final DecaySource MAGIC = register("magic");
    public static final DecaySource MINING = register("mining");
    public static final DecaySource REPAIR = register("repair");
    public static final DecaySource RESIST_DAMAGE = register("resist_damage");
    public static final DecaySource SHOOT = register("shoot");
    public static final DecaySource SNEAK = register("sneak");
    public static final DecaySource SOUL_WALK = register("soul_walk");
    public static final DecaySource THORNS = register("thorns");
    public static final DecaySource WATER_MOVEMENT = register("water_movement");


    public static final DecaySource BLACKLIST = register("decay_blacklist");

    private static final RegistryWrapper.Delegating<DamageType> wrapper;

    static {
        RegistryWrapper.Impl<DamageType> impl = BuiltinRegistries.createWrapperLookup().getWrapperOrThrow(RegistryKeys.DAMAGE_TYPE);
        wrapper = new RegistryWrapper.Delegating<>(impl);
    }

    private DecaySource(String s) {
        this.id = s;
        this.tag = TagKey.of(RegistryKeys.ENCHANTMENT, EnchantmentDecay.id("decay/"+s));
        this.damageSource = TagKey.of(RegistryKeys.DAMAGE_TYPE, EnchantmentDecay.id("decay/"+s));
    }

    public String getId() {
        return id;
    }

    public TagKey<Enchantment> getTag() {
        return tag;
    }

    public TagKey<DamageType> getDamageSources() {
        return damageSource;
    }

    public static DecaySource register(String id) {
        if (id == null || id.isEmpty() || id.isBlank()) {
            throw new UnsupportedOperationException();
        }
        if (sourceMap.containsKey(id)) {
            return sourceMap.get(id);
        }
        DecaySource source = new DecaySource(id);
        sourceMap.put(id, source);
        return source;
    }

    public static Collection<DecaySource> resolveDamageSources(RegistryEntry<DamageType> type) {
        List<DecaySource> out = new ArrayList<>(4);
        for (DecaySource source : sourceMap.values()) {
            if (source.damageSource == null) continue;
            Optional<RegistryEntryList.Named<DamageType>> opt = wrapper.getOptional(source.damageSource);
            if (opt.isEmpty()) continue;
            if (opt.get().contains(type)) out.add(source);
        }
        return out;
    }

    public static Optional<DecaySource> get(String id) {
        return Optional.ofNullable(sourceMap.get(id));
    }
}
