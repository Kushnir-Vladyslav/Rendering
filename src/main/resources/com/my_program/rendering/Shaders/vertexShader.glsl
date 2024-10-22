#version 330 core
layout(location = 0) in vec3 aPos; // Позиція вершин
layout(location = 1) in vec2 aTexCoord; // Текстурні координати

out vec2 TexCoord; // Передаємо текстурні координати у фрагментний шейдер

void main() {
    gl_Position = vec4(aPos, 1.0);
    TexCoord = aTexCoord;
}
