#type vertex
#version 330 core
layout (location = 0) in vec3 aPos;
layout (location = 1) in vec4 aColor;
layout (location = 2) in vec2 texCoord;
layout (location = 3) in float texIndex;

uniform mat4 uProjection;
uniform mat4 uView;

out vec4 fColor;
out vec2 fTexCoord;
out float fTexIndex;

void main() {
    fColor = aColor;
    fTexCoord = texCoord;
    fTexIndex = texIndex;
    gl_Position = uProjection * uView * vec4(aPos, 1.0);
}

#type fragment
#version 330 core

in vec4 fColor;
in vec2 fTexCoord;
in float fTexIndex;

uniform sampler2D uTextures[8];

out vec4 color;

void main() {
    int index = int(fTexIndex);
    if (index == -1) {
        color = fColor;
    } else {
        color = fColor * texture(uTextures[int(fTexIndex)], fTexCoord);
    }
}
