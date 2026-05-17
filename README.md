# Luminous Gear

**Smith glowing effects onto any item.**

Luminous Gear adds two smithing materials:

- **Enchantment Glow**: adds vanilla enchantment glint to the target item.
- **Texture Light**: marks the target item with a configurable radiance level. The default recipe stores light level 2 for dynamic-light integrations.

The mod uses custom smithing recipes that copy the base item, so pack authors can add more glow recipes through datapacks.

## Default smithing recipes

Put the glow material in the smithing template slot and any target item in the base slot. The addition slot is empty for the default recipes.

```json
{
  "type": "luminous_gear:glow_smithing",
  "template": { "item": "luminous_gear:enchantment_glow" },
  "glow_type": "enchantment"
}
```

```json
{
  "type": "luminous_gear:glow_smithing",
  "template": { "item": "luminous_gear:texture_light" },
  "glow_type": "texture_light",
  "light_level": 2
}
```

## Colored glint datapack recipes

Datapacks can add a glint color value:

```json
{
  "type": "luminous_gear:glow_smithing",
  "template": { "item": "luminous_gear:enchantment_glow" },
  "glow_type": "enchantment",
  "color": 16755200
}
```

The vanilla default glint recipe omits `color`.

## Notes for pack authors

Vanilla Minecraft does not provide a general per-item surface-emissive material system for arbitrary existing item textures. Luminous Gear stores a radiance component for dynamic-light/shader integrations and keeps the vanilla path simple instead of pretending vanilla can make every item texture emissive by itself.

## Requirements

- Minecraft 1.21.1
- NeoForge
