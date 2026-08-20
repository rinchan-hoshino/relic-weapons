#version 150

#moj_import <fog.glsl>

uniform sampler2D Sampler0;
uniform vec4 ColorModulator;
uniform float FogStart;
uniform float FogEnd;
uniform float GlintAlpha;

in float vertexDistance;
in vec4 vertexColor;
in vec2 texCoord0;

out vec4 fragColor;

void main() {
    vec4 mask = texture(Sampler0, texCoord0);
    if (mask.a < 0.1) {
        discard;
    }
    float intensity = max(mask.r, max(mask.g, mask.b));
    float fade = linear_fog_fade(vertexDistance, FogStart, FogEnd) * GlintAlpha;
    fragColor = vec4(vertexColor.rgb * ColorModulator.rgb * intensity * fade,
        mask.a * vertexColor.a * ColorModulator.a);
}
