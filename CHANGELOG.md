# Changelog

## 0.3.3

### Fixed

- Register colored-glint buffers when the render system is created instead of mutating its fixed buffer map while drawing an item. This prevents crashes with immutable maps and wrapped buffer sources such as Iris on both NeoForge and Fabric.

## 0.3.0

### Breaking

- Removed the `relic_weapons:enchantment_glow` and `relic_weapons:texture_light` items and their bundled recipes. Relic Weapons is now a content-free visual-effects API; packs choose their own catalysts and acquisition rules.

### Added

- Fabric 1.21.1 artifact alongside the NeoForge build, backed by the same profile, recipe, particle, and rendering implementation.
- Per-item visible RGB enchantment glint for vanilla item models, compasses, and worn humanoid armor.
- Arbitrary registered particle options through `relic_weapons:particle_effect`.
- Particle shape (`point`, `sphere`, `ring`, `trail`), rate, spread, direction (`random`, `up`, `outward`, `forward`, `motion`), speed, and inherited-motion controls.
- `VisualEffectProfile` snapshots and `VisualEffects` apply/capture/copy API.
- Particle support for held items, worn armor, and dropped item entities.
- Data-driven particle mode for `relic_weapons:glow_smithing` recipes using any external template item.

### Kept

- Independent glint override, custom glint-color metadata, radiance levels, full-bright item rendering, and Sodium Dynamic Lights integration.
- Existing `glint_color`, `radiance_level`, and `glow_smithing` IDs for data compatibility.
