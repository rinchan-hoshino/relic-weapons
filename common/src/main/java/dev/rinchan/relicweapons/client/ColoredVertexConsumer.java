package dev.rinchan.relicweapons.client;

import com.mojang.blaze3d.vertex.VertexConsumer;

/** Writes one profile color into the colored-glint batch without changing model geometry or UVs. */
final class ColoredVertexConsumer implements VertexConsumer {
    private final VertexConsumer delegate;
    private final int red;
    private final int green;
    private final int blue;

    ColoredVertexConsumer(VertexConsumer delegate, int rgb) {
        this.delegate = delegate;
        this.red = (rgb >>> 16) & 0xFF;
        this.green = (rgb >>> 8) & 0xFF;
        this.blue = rgb & 0xFF;
    }

    @Override
    public VertexConsumer addVertex(float x, float y, float z) {
        delegate.addVertex(x, y, z);
        return this;
    }

    @Override
    public VertexConsumer setColor(int ignoredRed, int ignoredGreen, int ignoredBlue, int alpha) {
        delegate.setColor(red, green, blue, alpha);
        return this;
    }

    @Override
    public VertexConsumer setUv(float u, float v) {
        delegate.setUv(u, v);
        return this;
    }

    @Override
    public VertexConsumer setUv1(int u, int v) {
        delegate.setUv1(u, v);
        return this;
    }

    @Override
    public VertexConsumer setUv2(int u, int v) {
        delegate.setUv2(u, v);
        return this;
    }

    @Override
    public VertexConsumer setNormal(float x, float y, float z) {
        delegate.setNormal(x, y, z);
        return this;
    }
}
