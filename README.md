# Relic Weapons

**Forge ancient-looking relic effects onto any item.**

Relic Weapons adds two smithing materials:

- **Enchantment Glow**: adds vanilla enchantment glint to the target item. The material item itself also glints.
- **Texture Light**: marks the target item with a configurable radiance level, renders the held item surface at full brightness, and exposes the same level for dynamic-light integrations. The default recipe stores light level 2.

The mod uses custom smithing recipes that copy the base item, so pack authors can add more glow recipes through datapacks.

## Default smithing recipes

Put the glow material in the smithing template slot and any target item in the base slot. The addition slot is empty for the default recipes.

```json
{
  "type": "relic_weapons:glow_smithing",
  "template": { "item": "relic_weapons:enchantment_glow" },
  "glow_type": "enchantment"
}
```

```json
{
  "type": "relic_weapons:glow_smithing",
  "template": { "item": "relic_weapons:texture_light" },
  "glow_type": "texture_light",
  "light_level": 2
}
```

## Colored glint datapack recipes

Datapacks can add a glint color value:

```json
{
  "type": "relic_weapons:glow_smithing",
  "template": { "item": "relic_weapons:enchantment_glow" },
  "glow_type": "enchantment",
  "color": 16755200
}
```

The vanilla default glint recipe omits `color`.

## Dynamic light and shader integration

The Texture Light material is registered as a level-2 item light for Sodium Dynamic Lights. Relic Weapons also exposes the same radiance level through an item data component on crafted targets, renders held relic item surfaces at full brightness on the client, and includes a Sodium Dynamic Lights hook so held relic items can emit their configured light.

Vanilla Minecraft does not provide a general per-item surface-emissive material system for arbitrary existing item textures. Relic Weapons keeps the item texture unchanged, then adds a client render pass for visible held-item radiance and a data component for dynamic-light/shader integrations.

## Requirements

- Minecraft 1.21.1
- NeoForge
