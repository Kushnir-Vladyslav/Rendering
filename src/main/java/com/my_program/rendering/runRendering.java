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

    public FFFFFFFFF ff = new FFFFFFFFF();

    @Override
    public void start(Stage primaryStage) {



        if (GlobalState.RenderingMethod == GlobalState.renderingMethod.GPU) {
            GlobalState.openGL = new OpenGl_prob();

        }

        writableImage = new WritableImage(GlobalState.getScreenWidth(), GlobalState.getScreenHeight());

        GlobalState.Pixels = new int [GlobalState.getScreenWidth() * GlobalState.getScreenHeight()];
        GlobalState.DepthBuffer = new float [GlobalState.getScreenWidth() * GlobalState.getScreenHeight()];

        imageView = new ImageView(writableImage);

        StackPane root = new StackPane();
        root.getChildren().add(imageView);
        Scene scene = new Scene(root, GlobalState.getScreenWidth(), GlobalState.getScreenHeight());

        primaryStage.setTitle("Simple render");
        primaryStage.setScene(scene);
        primaryStage.show();

        //завантаження моделі
        try ( modelReader md = new modelReader();) {
            md.loaderGLTF("Models/Duck/Duck.gltf");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        //-------------------------------
        ff.init();
        //-------------------------------

        //обробники подій зміни розміру вікна
        scene.widthProperty().addListener((observable, oldValue, newValue) -> {
            GlobalState.setScreenWidth(newValue.intValue());
            updateImageSize();
        });

        scene.heightProperty().addListener((observable, oldValue, newValue) -> {
            GlobalState.setScreenHeight(newValue.intValue());
            updateImageSize();
        });

        //Обробка натискання клавіш
        scene.setOnKeyPressed((event) -> {
            switch (event.getCode()) {
                case UP, W -> GlobalState.IsUp = true;
                case DOWN, S -> GlobalState.IsDown = true;
                case LEFT, A -> GlobalState.IsLeft = true;
                case RIGHT, D -> GlobalState.IsRight = true;
            }
        });

        scene.setOnKeyReleased((event) -> {
            switch (event.getCode()) {
                case UP, W -> GlobalState.IsUp = false;
                case DOWN, S -> GlobalState.IsDown = false;
                case LEFT, A -> GlobalState.IsLeft = false;
                case RIGHT, D -> GlobalState.IsRight = false;
            }
        });

        //Обробка натискання лівої кнопки миші і руху
        scene.setOnMousePressed((event) -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                GlobalState.camera.MousePressed(
                        (float) event.getX() / GlobalState.getScreenWidth(),
                        (float) event.getY() / GlobalState.getScreenHeight());
            }
        });

        scene.setOnMouseDragged((event) -> {
            if (event.isPrimaryButtonDown()) {
                GlobalState.camera.MouseMoved(
                        (float) event.getX() / GlobalState.getScreenWidth(),
                        (float) event.getY() / GlobalState.getScreenHeight());
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

                GlobalState.Time += (float) (now - last) / 1000000000;

                updatePixels((float) (now - last) / 1000000000);

                writableImage.getPixelWriter().setPixels(0, 0,
                        GlobalState.getScreenWidth(), GlobalState.getScreenHeight(),
                        PixelFormat.getIntArgbInstance(), GlobalState.Pixels, 0, GlobalState.getScreenWidth());

                last = now;
            }
        };
        animationTimer.start();
    }

    //оновлення вікна після зміни розміру
    private void updateImageSize() {
        writableImage = new WritableImage( GlobalState.getScreenWidth(), GlobalState.getScreenHeight());
        GlobalState.Pixels = new int[ GlobalState.getScreenWidth() * GlobalState.getScreenHeight()];
        GlobalState.DepthBuffer = new float [GlobalState.getScreenWidth() * GlobalState.getScreenHeight()];
        imageView.setImage(writableImage);
        GlobalState.camera.updatePerspective();
        if (GlobalState.RenderingMethod == GlobalState.renderingMethod.GPU) {
            GlobalState.openGL.windowUpDate();
        }
    }

    //основна функція відрисовки
    private void updatePixels(float time)  {

        if (GlobalState.RenderingMethod == GlobalState.renderingMethod.GPU) {
            GlobalState.openGL.clear(0, 1,0);
        } else {
            fillBackground(0xFF000000);
        }


        float cos = (float) cos(toRadians(GlobalState.Time));
        float sin = (float) sin(toRadians(GlobalState.Time));

        // Рух камери в відповідності до натисненої кнопки
        if (GlobalState.IsUp) {
            GlobalState.camera.Position.add(
                    GlobalState.camera.lookAt().mult(
                            time * GlobalState.Speed));
        }
        if (GlobalState.IsRight) {
            GlobalState.camera.Position.add(
                    GlobalState.camera.right().mult(
                            time * GlobalState.Speed));
        }
        if (GlobalState.IsLeft) {
            GlobalState.camera.Position.sub(
                    GlobalState.camera.right().mult(
                            time * GlobalState.Speed));
        }
        if (GlobalState.IsDown) {
            GlobalState.camera.Position.sub(
                    GlobalState.camera.lookAt().mult(
                            time * GlobalState.Speed));
        }

        GlobalState.Time = 0;
        //формування матриці перетворення для обєкту, порядок: розмір, поворот, переміщення
        matrix4D tr =  matrix4D.scaleMatrix4(1, 1, 1).mult(
                matrix4D.rotationMatrix4(GlobalState.Time * 100, GlobalState.Time * 100, GlobalState.Time * 100)
        ).mult(
                matrix4D.translationMatrix4(0, 0, 4)
        );

        //монження матриці трансформації обєкту з матрицею трансформації камери, послідовність: камера, обєкт
        tr = matrix4D.mult(GlobalState.camera.getCameraTransform(), tr);

        //множення матриці трансофрмації обєку і камери з матрицею трасформації перспективи, послідовність: перспектив, інш.
        tr = matrix4D.mult(GlobalState.camera.getPerspectiveMatrix(), tr);

        for (int i = 0; i < GlobalState.Objects.size(); i++) {
            GlobalState.Objects.get(i).draw(tr);
        }



        if (GlobalState.RenderingMethod == GlobalState.renderingMethod.GPU) {
            GlobalState.openGL.getResultOfRenderingOpenGL();
        }

        ff.loop();

    }



    private float cosm(float angle) {
        return (float) Math.cos(toRadians(angle));
    }
    private float sinm(float angle) {
        return (float) Math.sin(toRadians(angle));
    }

    //функція для закрашування фону, та оновлення масиву глибин
    public static void fillBackground (int color) {
        for (int y = 0; y < GlobalState.getScreenHeight(); y++) {
            for (int x = 0; x < GlobalState.getScreenWidth(); x++) {
                GlobalState.Pixels[y * GlobalState.getScreenWidth() + x] = color;
                GlobalState.DepthBuffer[y * GlobalState.getScreenWidth() + x] = 1.f;
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
