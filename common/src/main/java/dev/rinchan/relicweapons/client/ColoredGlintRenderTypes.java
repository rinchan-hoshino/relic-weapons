package dev.rinchan.relicweapons.client;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;

/** Fixed glint batches; each vertex carries its profile RGB so arbitrary colors can share a batch safely. */
public final class ColoredGlintRenderTypes {
    private static final RenderType ITEM = create("item", false, false, false);
    private static final RenderType ITEM_TRANSLUCENT = create("item_translucent", false, true, false);
    private static final RenderType ENTITY = create("entity", true, true, false);
    private static final RenderType ENTITY_DIRECT = create("entity_direct", true, false, false);
    private static final RenderType ARMOR = create("armor", true, false, true);

    private ColoredGlintRenderTypes() {
    }

    public static RenderType item() {
        return ITEM;
    }

    public static RenderType itemTranslucent() {
        return ITEM_TRANSLUCENT;
    }

    public static RenderType entity() {
        return ENTITY;
    }

    public static RenderType entityDirect() {
        return ENTITY_DIRECT;
    }

    public static RenderType armor() {
        return ARMOR;
    }

    private static RenderType create(String id, boolean entityTexture, boolean itemTarget, boolean armor) {
        RenderType.CompositeState.CompositeStateBuilder builder = RenderType.CompositeState.builder()
            .setShaderState(new RenderStateShard.ShaderStateShard(RelicShaders::coloredGlint))
            .setTextureState(new RenderStateShard.TextureStateShard(
                entityTexture ? ItemRenderer.ENCHANTED_GLINT_ENTITY : ItemRenderer.ENCHANTED_GLINT_ITEM,
                true,
                false))
            .setWriteMaskState(RenderStateShard.COLOR_WRITE)
            .setCullState(RenderStateShard.NO_CULL)
            .setDepthTestState(RenderStateShard.EQUAL_DEPTH_TEST)
            .setTransparencyState(RenderStateShard.GLINT_TRANSPARENCY)
            .setTexturingState(entityTexture
                ? RenderStateShard.ENTITY_GLINT_TEXTURING
                : RenderStateShard.GLINT_TEXTURING);
        if (itemTarget) {
            builder.setOutputState(RenderStateShard.ITEM_ENTITY_TARGET);
        }
        if (armor) {
            builder.setLayeringState(RenderStateShard.VIEW_OFFSET_Z_LAYERING);
        }
        return RenderType.create(
            "relic_weapons_colored_" + id,
            DefaultVertexFormat.POSITION_TEX_COLOR,
            VertexFormat.Mode.QUADS,
            1536,
            false,
            false,
            builder.createCompositeState(false));
    }
}
