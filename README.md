# Enchantment Decay

_Magic is an incredible force, coming from days of old, that allows you to improve your power over the world.
One of the traces remaining of this ancient knowledge that you could stumble upon on your travels is Enchantments.
But as time passed by, this knowledge eroded, and even that you may get from the few remains of the old civilisation
that mastered this art isn't even perfect. And yet, you were able to recreate it. You were able to put together
your own enchanting table, and infuse the very power accumulated throughout your journey into your very tools.
But as expected, all the knowledge you could accumulate is still lacking, and you realize that as you make use of
your enchanted gear, the magic slowly wears off..._

## How it works

Enchantments each have a more or less specific way to affect your life.
This mod will hook itself on many occasions upon which enchants are used, and register their use as a potential
source of decay.
Hence, each time an enchantment affects your gameplay, there will be a chance it decays a small bit. And when the decay
reaches a certain level, the enchantment will go down a level, and the decay reset.

You will be able to see the decay level of you tool next to the enchantment name. But as you will venture the world,
when you discover enchanted loot, there will be a chance it is _immune to decay_. You will recognize these occurences
with a green `ˣ` before the name.

## How to configure

In your game's `config` folder, after the first launch, will be generated a `enchantment_decay.properties` file.
Within this file, you will find everything you need to configure the mod.

### Default values

The configuration file includes a few default values for the mod to use, including:
 * The **default max decay**: This value indicates how much can an enchantment decay, by default
 * The **default decay probability**: This value indicates, out of a hundred, the probability for an enchantment to
   decay upon use, by default. Set to 0 to disable all decay by default.
 * The **loot decay probability**: This value indicates, out of a hundred, the probability for an enchantment
   applied on some generated loot to already have some decay. This will not affect decay immune enchantments.
   Set to 0 to disable all decay on generated loot.
 * The **loot decay max percentile**: This value indicates the maximum percentile an enchantment generated
   upon loot can be decayed, compared to the enchantment's maximum decay.
 * The **decay immune loot probability**: This value indicates, out of a hundred, the probability for an enchant applied
   on some generated loot to be decay-immune.

### Enchantment-specific values

The configuration files also support some per-enchantment specific values:
 * The **max decay**: registered as `decay.max.<namespace>.<path>`, this value indicates the maximum decay value for
   instances of this very enchantment.
 * The **decay probability**: registered as `decay.probability.<namespace>.<path>`, the value indicates, out of a
   hundred, the probability for an instance of this enchantment to decay upon use.

**Example:**

```properties
decay.max.minecraft.efficiency=50
decay.probability.minecraft.efficiency=8
```
The above example would make it so any instance of the vanilla Efficiency enchantment will be able to have up to 49
as a decay value before going down a level, and that upon each use, it will have a 8% chance to decay.

## Enchantments control

This mod uses enchantment tags in order to work. Each tag corresponds to a potential source of decay (suc as `kill` for
killing, `fall` for falling, `hurt` for getting hurt, `damage` for when the gear piece gets damaged, etc.)

All of these enchantments are registered as `enchantment_decay:decay/<name>`, so under the
`data/enchantment_decay/tags/enchantments/decay/` folder within a datapack.

You can simply make use of a datapack in order to overhaul or add to these tags, thus being able to change which
enchantment gets to decay on what occasion.

The `blacklist` tag is the one and only exception, as it defines enchantments that cannot decay.
By default, this tag only contains curses.

## Showcase

A (silent) showcase of this mod can be found [here](https://youtu.be/ynX38MOlzTw)