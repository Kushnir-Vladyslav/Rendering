package com.my_program.rendering;

import de.javagl.jgltf.model.*;
import de.javagl.jgltf.model.io.GltfModelReader;
import de.javagl.jgltf.model.v2.MaterialModelV2;


import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;



public class modelReader implements AutoCloseable  {
    // тимчаасова папка в яку буде збережено файли якщо вони в jar файлі
    private final Path tempDir;

    private String resourcePrefix;

    public modelReader () throws IOException {
        //створення тимчасової папки
        tempDir = Files.createTempDirectory("gltf-resources");
    }

    public void loaderGLTF(String modelFilePath) throws IOException {

        //отримання посилання на файл
        URL url = getClass().getResource(modelFilePath);
        URI uri;

        try {
            //перевірка чи ресурси в локальній папці чи в запакованні в файлі jar
            if (url.getProtocol().equals("jar"))
            {
                //отримання посилання на папку в якій знаходяться ресурси
                String resourceFolderPath = Paths.get(modelFilePath).getParent().toString().replace("\\", "/");
                this.resourcePrefix = resourceFolderPath;

                //виавнтаження файлів в тимчаосву папку
                extractResources();

                String newModelFilePath = tempDir.toString().replace("\\", "/");

                newModelFilePath += "/" + Paths.get(modelFilePath).getFileName();

                //отримання посилання на файл в тимчасовй папці
                uri = Paths.get(newModelFilePath).toUri();

            } else {
                uri = url.toURI();
            }


        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        //створення читатча, та читання файлів
        GltfModelReader reader = new GltfModelReader();
        GltfModel gltfModel = reader.read(uri);

        //Отримуємо список моделей сіток
        List<MeshModel> meshes = gltfModel.getMeshModels();

        for (int i = 0; i < meshes.size(); i++) {
            MeshModel mesh = meshes.get(i);

            //Отримуємо список примітивів для поточної сітки
            List<MeshPrimitiveModel> primitives = mesh.getMeshPrimitiveModels();

            for (int j = 0; j < primitives.size(); j++) {
                MeshPrimitiveModel primitive = primitives.get(j);

                //отримуємо вершини
                AccessorModel position =
                        primitive.getAttributes().get("POSITION");

                AccessorFloatData positionData =
                        (AccessorFloatData) position.getAccessorData();

                int numElements = positionData.getNumElements();
                int numComponents = positionData.getNumComponentsPerElement();

                vec4D[] VerticesBuffer;

                if (numElements > 0 && numComponents == 3) {

                    VerticesBuffer = new vec4D[positionData.getNumElements()];

                    for (int e = 0; e < numElements; e++) {
                        VerticesBuffer[e] = new vec4D(
                                positionData.get(e, 0),
                                positionData.get(e, 1),
                                positionData.get(e, 2));
                    }
                } else {
                    continue;
                }

                //отримуємо кординати для текстук
                AccessorModel UI =
                        primitive.getAttributes().get("TEXCOORD_0");

                AccessorFloatData UIData =
                        (AccessorFloatData) UI.getAccessorData();

                numElements = UIData.getNumElements();
                numComponents = UIData.getNumComponentsPerElement();

                vec2D[] ObjectUv;

                if (numElements > 0 && numComponents == 2) {
                    ObjectUv = new vec2D[numElements];

                    for (int e = 0; e < numElements; e++) {
                        float x = UIData.get(e, 0) % 1;
                        float y = UIData.get(e, 1) % 1;

                        if ( x < 0) {
                            x += 1.f;
                        }

                        if (y < 0) {
                            y += 1.f;
                        }

                        ObjectUv[e] = new vec2D(x, y);
                    }

                } else {
                    continue;
                }

                //отримуємо список індексів
                AccessorShortData indicesData =
                        (AccessorShortData) primitive.getIndices().getAccessorData();

                numElements = indicesData.getNumElements();

                int[] IndexBuffer;

                if (numElements > 0) {
                    IndexBuffer = new int[numElements];
                    for (int e = 0; e < numElements; e++) {
                        IndexBuffer[e] = indicesData.get(e);
                    }
                } else {
                    continue;
                }

                //оттримуємо текстуру
                texture Texture = null;

                MaterialModelV2 material = (MaterialModelV2) primitive.getMaterialModel();
                if (material != null) {
                    TextureModel baseColorTexture = material.getBaseColorTexture();
                    if (baseColorTexture != null) {
                        ImageModel image = baseColorTexture.getImageModel();
                        if (image != null) {
                            // Тепер у вас є дані зображення (imageData) та його MIME-тип

                            String resourceFolderPath = Paths.get(modelFilePath).getParent().toString().replace("\\", "/");
                            resourceFolderPath += "/" + image.getUri();
                            BufferedImage Image = ImageIO.read(getClass().getResource(resourceFolderPath));

                            // Отримати розмір зображення
                            int Width = Image.getWidth();
                            int Height = Image.getHeight();

                            int[] Texels = new int[Width * Height];
                            for (int y = 0; y < Height; y++) {
                                for (int x = 0; x < Width; x++) {
                                    // Отримати колір пікселя в координатах
                                    Texels[(Height - y - 1) * Width + x] = Image.getRGB(x, y);
                                }
                            }

                            Texture = new texture(Height, Width, Texels);
                        }
                    }
                }

                //якщо теустуру не заванатажено створюємо стандартну (шахматну)
                if (Texture == null) {
                    Texture = new texture();
                }

                GlobalState.Objects.add(new drawableObject(VerticesBuffer, ObjectUv, IndexBuffer, Texture));

            }
        }
    }

    private void extractResources() throws IOException {
        URL resourceUrl =  getClass().getResource(resourcePrefix);


        if (resourceUrl == null) {
            throw new FileNotFoundException("Resource folder not found: " + resourcePrefix);
        }

        //Перебір всіх файлів jar файлі та копіювання файлів з потрібної папки в тимчасову
        String jarPath = resourceUrl.getPath().substring(5, resourceUrl.getPath().indexOf("!"));
        try (JarFile jar = new JarFile(jarPath)) {
            Enumeration<JarEntry> entries = jar.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                if (entry.getName().contains(resourcePrefix) && !entry.isDirectory()) {
                    String fileName = Paths.get(entry.getName()).getFileName().toString();
                    Path targetPath = tempDir.resolve(fileName);
                    try (InputStream is = jar.getInputStream(entry)) {
                        Files.copy(is, targetPath, StandardCopyOption.REPLACE_EXISTING);
                    }
                }
            }
        }
    }



    @Override
    public void close() throws Exception {
        //видалення всіх файлів в тичасовій папці і тимчосової папки
        Files.walkFileTree(tempDir, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }
}
