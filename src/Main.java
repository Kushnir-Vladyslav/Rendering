import com.sun.javafx.iio.gif.GIFImageLoader2;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.PixelFormat;
import javafx.scene.input.KeyCode;
import javafx.scene.image.WritableImage;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.toRadians;


public class Main extends Application {

    private WritableImage writableImage;
    private ImageView imageView;


    @Override
    public void start(Stage primaryStage) {

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

        GlobalState.Objects.add(new DrawableObject(
                // Масив вершин трикутників
        new Vec4[] {
                // передня стінка куба
                new Vec4( -0.5f, -0.5f, -0.5f),
                new Vec4( -0.5f, 0.5f, -0.5f),
                new Vec4( 0.5f, 0.5f, -0.5f),
                new Vec4( 0.5f, -0.5f, -0.5f),
                // задня стінка куба
                new Vec4( -0.5f, -0.5f, 0.5f),
                new Vec4( -0.5f, 0.5f, 0.5f),
                new Vec4( 0.5f, 0.5f, 0.5f),
                new Vec4( 0.5f, -0.5f, 0.5f),
        },
        //масив кольорів вершин трикутників
        new Vec4[] {
                new Vec4( 1, 0, 0),
                new Vec4( 0, 1, 0),
                new Vec4( 0, 0, 1),
                new Vec4( 1, 0, 1),

                new Vec4( 1, 1, 0),
                new Vec4( 0, 1, 1),
                new Vec4( 1, 0, 1),
                new Vec4( 1, 1, 1),
        },
        // масив індексів вершин для відмальовування сторін
         new int[] {
                // передня сторона
                0, 1, 2,
                2, 3, 0,

                // задня сторона
                6, 5, 4,
                4, 7, 6,

                // ліва сторона
                4, 5, 1,
                1, 0, 4,

                // права сторона
                3, 2, 6,
                6, 7, 3,

                // верхня сторона
                1, 5, 6,
                6, 2, 1,

                // нижня сторона
                4, 0, 3,
                3, 7, 4,
        }
        ));

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
    }

    //основна функція відрисовки
    private void updatePixels(float time) {


//        System.out.println(GlobalState.Time);
        fillBackground(0xFF000000);

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



        Matrix4 tr =  Matrix4.scaleMatrix4(1, 1, 1).mult(
                Matrix4.rotationMatrix4(GlobalState.Time * 100, GlobalState.Time * 100, GlobalState.Time * 100)
        ).mult(
                Matrix4.translationMatrix4(0, 0, 2).mult( GlobalState.camera.getCameraTransform())
        );

        for (int i = 0; i < GlobalState.Objects.size(); i++) {
            GlobalState.Objects.get(i).draw(tr);
        }

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
                GlobalState.DepthBuffer[y * GlobalState.getScreenWidth() + x] = 0.f;
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
