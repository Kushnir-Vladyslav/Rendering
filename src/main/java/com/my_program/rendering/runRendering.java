package com.my_program.rendering;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.WritableImage;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.toRadians;


public class runRendering extends Application {

    private WritableImage writableImage;
    private ImageView imageView;


    @Override
    public void start(Stage primaryStage) {

        writableImage = new WritableImage(GS.getScreenWidth(), GS.getScreenHeight());

        GS.Pixels = new int [GS.getScreenWidth() * GS.getScreenHeight()];
        GS.DepthBuffer = new float [GS.getScreenWidth() * GS.getScreenHeight()];

        imageView = new ImageView(writableImage);

        StackPane root = new StackPane();
        root.getChildren().add(imageView);
        Scene scene = new Scene(root, GS.getScreenWidth(), GS.getScreenHeight());

        primaryStage.setTitle("Simple render");
        primaryStage.setScene(scene);
        primaryStage.show();

        //завантаження моделі
        try ( modelReader md = new modelReader();) {
            md.loaderGLTF("Models/Sponza/Sponza.gltf");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

//        //куб
//        GS.Objects.add(new DrawableObject(
//                // Масив вершин трикутників
//                new Vec4D[] {
//                        // передня стінка куба
//                        new Vec4D( -0.5f, -0.5f, -0.5f),
//                        new Vec4D( -0.5f, 0.5f, -0.5f),
//                        new Vec4D( 0.5f, 0.5f, -0.5f),
//                        new Vec4D( 0.5f, -0.5f, -0.5f),
//                        // задня стінка куба
//                        new Vec4D( -0.5f, -0.5f, 0.5f),
//                        new Vec4D( -0.5f, 0.5f, 0.5f),
//                        new Vec4D( 0.5f, 0.5f, 0.5f),
//                        new Vec4D( 0.5f, -0.5f, 0.5f),
//                },
//                //масив вершин на текстурі
//                new Vec2D[] {
//                        new Vec2D( 0, 0),
//                        new Vec2D( 1, 0),
//                        new Vec2D( 1, 1),
//                        new Vec2D( 0, 1),
//
//                        new Vec2D( 0, 0),
//                        new Vec2D( 1, 0),
//                        new Vec2D( 1, 1),
//                        new Vec2D( 0, 1),
//
//                },
//                // масив індексів вершин для відмальовування сторін
//                new int[] {
//                        // передня сторона
//                        0, 1, 2,
//                        2, 3, 0,
//
//                        // задня сторона
//                        6, 5, 4,
//                        4, 7, 6,
//
//                        // ліва сторона
//                        4, 5, 1,
//                        1, 0, 4,
//
//                        // права сторона
//                        3, 2, 6,
//                        6, 7, 3,
//
//                        // верхня сторона
//                        1, 5, 6,
//                        6, 2, 1,
//
//                        // нижня сторона
//                        4, 0, 3,
//                        3, 7, 4,
//                },
//                new Texture()
//        ));

//        //піраміда
//        com.my_program.rendering.GS.Objects.add( new com.my_program.rendering.DrawableObject(
//                // Масив вершин трикутників
//                new com.my_program.rendering.Vec4D[] {
//                        // вершини основи піраміди
//                        new com.my_program.rendering.Vec4D(-0.5f, 0.0f, -0.5f),  // 0
//                        new com.my_program.rendering.Vec4D(0.5f, 0.0f, -0.5f),   // 1
//                        new com.my_program.rendering.Vec4D(0.5f, 0.0f, 0.5f),    // 2
//                        new com.my_program.rendering.Vec4D(-0.5f, 0.0f, 0.5f),   // 3
//                        // вершина піраміди
//                        new com.my_program.rendering.Vec4D(0.0f, 1.0f, 0.0f),    // 4
//                },
//                // Масив кольорів вершин трикутників
//                new com.my_program.rendering.Vec2D[] {
//                        new com.my_program.rendering.Vec2D( 0, 0),
//                        new com.my_program.rendering.Vec2D( 1, 0),
//                        new com.my_program.rendering.Vec2D( 1, 1),
//                        new com.my_program.rendering.Vec2D( 0, 1),
//                        new com.my_program.rendering.Vec2D(1, 1),
//                },
//                // Масив індексів вершин для відмальовування сторін
//                new int[] {
//                        // основа
//                        0, 2, 3,
//                        0, 1, 2,
//
//                        // передня сторона
//                        0, 4, 1,
//
//                        // права сторона
//                        1, 4, 2,
//
//                        // задня сторона
//                        2, 4, 3,
//
//                        // ліва сторона
//                        3, 4, 0,
//                },
//                new com.my_program.rendering.Texture()
//        ));

//        // кубик Д20
//        com.my_program.rendering.GS.Objects.add( new com.my_program.rendering.DrawableObject(
//        // Масив вершин ікосаедра
//        new com.my_program.rendering.Vec4D[] {
//                new com.my_program.rendering.Vec4D(0, 0, 1),  // вершина 0
//                new com.my_program.rendering.Vec4D(0.8944f, 0, 0.4472f),  // вершина 1
//                new com.my_program.rendering.Vec4D(0.2764f, 0.8506f, 0.4472f),  // вершина 2
//                new com.my_program.rendering.Vec4D(-0.7236f, 0.5257f, 0.4472f),  // вершина 3
//                new com.my_program.rendering.Vec4D(-0.7236f, -0.5257f, 0.4472f),  // вершина 4
//                new com.my_program.rendering.Vec4D(0.2764f, -0.8506f, 0.4472f),  // вершина 5
//                new com.my_program.rendering.Vec4D(0.7236f, 0.5257f, -0.4472f),  // вершина 6
//                new com.my_program.rendering.Vec4D(-0.2764f, 0.8506f, -0.4472f),  // вершина 7
//                new com.my_program.rendering.Vec4D(-0.8944f, 0, -0.4472f),  // вершина 8
//                new com.my_program.rendering.Vec4D(-0.2764f, -0.8506f, -0.4472f),  // вершина 9
//                new com.my_program.rendering.Vec4D(0.7236f, -0.5257f, -0.4472f),  // вершина 10
//                new com.my_program.rendering.Vec4D(0, 0, -1),  // вершина 11
//        },
//// Масив кольорів вершин трикутників
//                new com.my_program.rendering.Vec2D[] {
//                        new com.my_program.rendering.Vec2D( 0, 0),
//                        new com.my_program.rendering.Vec2D( 1, 0),
//                        new com.my_program.rendering.Vec2D( 1, 1),
//                        new com.my_program.rendering.Vec2D( 0, 1),
//                        new com.my_program.rendering.Vec2D(1, 1),
//                        new com.my_program.rendering.Vec2D( 1, 0),
//                        new com.my_program.rendering.Vec2D( 0, 0),
//                        new com.my_program.rendering.Vec2D( 1, 0),
//                        new com.my_program.rendering.Vec2D( 1, 1),
//                        new com.my_program.rendering.Vec2D( 0, 1),
//                        new com.my_program.rendering.Vec2D(1, 1),
//                        new com.my_program.rendering.Vec2D( 1, 0),
//                },
//// Масив індексів вершин для відмальовування сторін
//                new int[] {
//                        0, 1, 2,
//                        0, 2, 3,
//                        0, 3, 4,
//                        0, 4, 5,
//                        0, 5, 1,
//
//                        1, 6, 2,
//                        2, 7, 3,
//                        3, 8, 4,
//                        4, 9, 5,
//                        5, 10, 1,
//
//                        1, 10, 6,
//                        2, 6, 7,
//                        3, 7, 8,
//                        4, 8, 9,
//                        5, 9, 10,
//
//                        6, 11, 7,
//                        7, 11, 8,
//                        8, 11, 9,
//                        9, 11, 10,
//                        10, 11, 6,
//                },
//                new com.my_program.rendering.Texture()
//));

//         //Трикутник
//        com.my_program.rendering.GS.Objects.add(new com.my_program.rendering.DrawableObject(
//                // Масив вершин трикутників
//                new com.my_program.rendering.Vec4D[] {
//                        // передня стінка куба
//                        new com.my_program.rendering.Vec4D( -0.5f, -0.5f, -0.5f),
//                        new com.my_program.rendering.Vec4D( -0.5f, 0.5f, -0.5f),
//                        new com.my_program.rendering.Vec4D( 0.5f, 0.5f, -0.5f),
//                },
//                //масив вершин на текстурі
//                new com.my_program.rendering.Vec2D[] {
//                        new com.my_program.rendering.Vec2D( 0, 0),
//                        new com.my_program.rendering.Vec2D( 1, 0),
//                        new com.my_program.rendering.Vec2D( 1, 1),
//
//                },
//                // масив індексів вершин для відмальовування сторін
//                new int[] {
//                        // передня сторона
//                        0, 1, 2
//                },
//                new com.my_program.rendering.Texture()
//        ));


        //обробники подій зміни розміру вікна
        scene.widthProperty().addListener((observable, oldValue, newValue) -> {
            GS.setScreenWidth(newValue.intValue());
            updateImageSize();
            GS.camera.updatePerspective();
        });

        scene.heightProperty().addListener((observable, oldValue, newValue) -> {
            GS.setScreenHeight(newValue.intValue());
            updateImageSize();
            GS.camera.updatePerspective();
        });

        //Обробка натискання клавіш
        scene.setOnKeyPressed((event) -> {
            switch (event.getCode()) {
                case UP, W -> GS.IsUp = true;
                case DOWN, S -> GS.IsDown = true;
                case LEFT, A -> GS.IsLeft = true;
                case RIGHT, D -> GS.IsRight = true;
            }
        });

        scene.setOnKeyReleased((event) -> {
            switch (event.getCode()) {
                case UP, W -> GS.IsUp = false;
                case DOWN, S -> GS.IsDown = false;
                case LEFT, A -> GS.IsLeft = false;
                case RIGHT, D -> GS.IsRight = false;
            }
        });

        //Обробка натискання лівої кнопки миші і руху
        scene.setOnMousePressed((event) -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                GS.camera.MousePressed(
                        (float) event.getX() / GS.getScreenWidth(),
                        (float) event.getY() / GS.getScreenHeight());
            }
        });

        scene.setOnMouseDragged((event) -> {
            if (event.isPrimaryButtonDown()) {
                GS.camera.MouseMoved(
                        (float) event.getX() / GS.getScreenWidth(),
                        (float) event.getY() / GS.getScreenHeight());
            }
        });


        //запуск таймеру для анімації
        AnimationTimer animationTimer = new AnimationTimer() {
            private long last = 0;
            @Override
            public void handle(long now) {
                if (last == 0) {
                    last = now;
                    return;
                }

                GS.Time += (float) (now - last) / 1000000000;

                updatePixels((float) (now - last) / 1000000000);
                writableImage.getPixelWriter().setPixels(0, 0,
                        GS.getScreenWidth(), GS.getScreenHeight(),
                        PixelFormat.getIntArgbInstance(), GS.Pixels, 0, GS.getScreenWidth());

                last = now;
            }
        };
        animationTimer.start();
    }

    //оновлення вікна після зміни розміру
    private void updateImageSize() {
        writableImage = new WritableImage( GS.getScreenWidth(), GS.getScreenHeight());
        GS.Pixels = new int[ GS.getScreenWidth() * GS.getScreenHeight()];
        GS.DepthBuffer = new float [GS.getScreenWidth() * GS.getScreenHeight()];
        imageView.setImage(writableImage);
    }

    //основна функція відрисовки
    private void updatePixels(float time) {


//        System.out.println(1f / com.my_program.rendering.GS.Time);
        fillBackground(0xFF000000);

        float cos = (float) cos(toRadians(GS.Time));
        float sin = (float) sin(toRadians(GS.Time));

        // Рух камери в відповідності до натисненої кнопки
        if (GS.IsUp) {
            GS.camera.Position.add(
                    GS.camera.lookAt().mult(
                            time * GS.Speed));
        }
        if (GS.IsRight) {
            GS.camera.Position.add(
                    GS.camera.right().mult(
                            time * GS.Speed));
        }
        if (GS.IsLeft) {
            GS.camera.Position.sub(
                    GS.camera.right().mult(
                            time * GS.Speed));
        }
        if (GS.IsDown) {
            GS.camera.Position.sub(
                    GS.camera.lookAt().mult(
                            time * GS.Speed));
        }

        GS.Time = 0;
        //формування матриці перетворення для обєкту, порядок: розмір, поворот, переміщення
        Matrix4D tr =  Matrix4D.scaleMatrix4(1, 1, 1).mult(
                Matrix4D.rotationMatrix4(GS.Time * 100, GS.Time * 100, GS.Time * 100)
        ).mult(
                Matrix4D.translationMatrix4(0, 0, 4)
        );

        //монження матриці трансформації обєкту з матрицею трансформації камери, послідовність: камера, обєкт
        tr = Matrix4D.mult(GS.camera.getCameraTransform(), tr);

        //множення матриці трансофрмації обєку і камери з матрицею трасформації перспективи, послідовність: перспектив, інш.
        tr = Matrix4D.mult(GS.camera.getPerspectiveMatrix(), tr);

        for (int i = 0; i < GS.Objects.size(); i++) {
            GS.Objects.get(i).draw(tr);
        }



//        //формування матриці перетворення для обєкту, порядок: розмір, поворот, переміщення
//        tr =  com.my_program.rendering.Matrix4D.scaleMatrix4(1, 1, 1).mult(
//                com.my_program.rendering.Matrix4D.rotationMatrix4(com.my_program.rendering.GS.Time * 100, com.my_program.rendering.GS.Time * 100, com.my_program.rendering.GS.Time * 100)
//        ).mult(
//                com.my_program.rendering.Matrix4D.translationMatrix4(0, 0, 10)
//        );
//
//        //монження матриці трансформації обєкту з матрицею трансформації камери, послідовність: камера, обєкт
//        tr = com.my_program.rendering.Matrix4D.mult(com.my_program.rendering.GS.com.my_program.rendering.camera.getCameraTransform(), tr);
//
//        //множення матриці трансофрмації обєку і камери з матрицею трасформації перспективи, послідовність: перспектив, інш.
//        tr = com.my_program.rendering.Matrix4D.mult(com.my_program.rendering.GS.com.my_program.rendering.camera.getPerspectiveMatrix(), tr);
//
//        for (int i = 1; i < 2; i++) {
//            com.my_program.rendering.GS.Objects.get(i).draw(tr);
//        }

    }

    private float cosm(float angle) {
        return (float) Math.cos(toRadians(angle));
    }
    private float sinm(float angle) {
        return (float) Math.sin(toRadians(angle));
    }

    //функція для закрашування фону, та оновлення масиву глибин
    public static void fillBackground (int color) {
        for (int y = 0; y < GS.getScreenHeight(); y++) {
            for (int x = 0; x < GS.getScreenWidth(); x++) {
                GS.Pixels[y * GS.getScreenWidth() + x] = color;
                GS.DepthBuffer[y * GS.getScreenWidth() + x] = 1.f;
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
