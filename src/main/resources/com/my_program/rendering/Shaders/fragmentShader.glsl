#version 330 core
out vec4 FragColor;

in vec2 TexCoord; // Отримуємо текстурні координати з вершинного шейдера

uniform sampler2D texture1; // Сам текстурний об'єкт

void main() {
    FragColor = texture(texture1, TexCoord); // Беремо колір з текстури
}
