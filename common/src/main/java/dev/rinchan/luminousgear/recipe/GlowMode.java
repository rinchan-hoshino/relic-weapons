package dev.rinchan.luminousgear.recipe;

public enum GlowMode {
    ENCHANTMENT("enchantment"),
    TEXTURE_LIGHT("texture_light");

    private final String id;

    GlowMode(String id) {
        this.id = id;
    }

    public String id() {
        return id;
    }

    public static GlowMode fromId(String id) {
        for (GlowMode mode : values()) {
            if (mode.id.equals(id)) {
                return mode;
            }
        }
        return ENCHANTMENT;
    }
}
