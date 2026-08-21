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
    float tintPeak = max(vertexColor.r, max(vertexColor.g, vertexColor.b));
    vec3 visibleTint = tintPeak > 0.0 ? vertexColor.rgb / tintPeak : vec3(1.0);
    float fade = linear_fog_fade(vertexDistance, FogStart, FogEnd) * GlintAlpha;
    // GLINT_TRANSPARENCY uses source-color blending, which squares a dark source.
    // Lift the moving vanilla mask before blending so colored foil remains visible
    // in both GUI-direct and world/entity render paths.
    float visibleIntensity = sqrt(max(intensity, 0.0));
    fragColor = vec4(visibleTint * ColorModulator.rgb * visibleIntensity * fade,
        mask.a * vertexColor.a * ColorModulator.a);
}
