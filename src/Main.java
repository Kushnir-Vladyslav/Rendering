import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.WritableImage;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import static java.lang.Math.*;
import static java.lang.Math.toRadians;


public class Main extends Application {

    private WritableImage writableImage;
    private ImageView imageView;


    @Override
    public void start(Stage primaryStage) {

        writableImage = new WritableImage(GlobalState.getScreenWidth(), GlobalState.getScreenHeight());

        GlobalState.Pixels = new int [GlobalState.getScreenWidth() * GlobalState.getScreenHeight()];

        imageView = new ImageView(writableImage);

        StackPane root = new StackPane();
        root.getChildren().add(imageView);
        Scene scene = new Scene(root, GlobalState.getScreenWidth(), GlobalState.getScreenHeight());


        primaryStage.setTitle("Simple render");
        primaryStage.setScene(scene);
        primaryStage.show();

        //обробники подій зміни розміру вікна
        scene.widthProperty().addListener((observable, oldValue, newValue) -> {
            GlobalState.setScreenWidth(newValue.intValue());
            updateImageSize();
        });

        scene.heightProperty().addListener((observable, oldValue, newValue) -> {
            GlobalState.setScreenHeight(newValue.intValue());
            updateImageSize();
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
                updatePixels(now - last);
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
        imageView.setImage(writableImage);
    }

    private double sum = 0;

    //основна функція відрисовки
    private void updatePixels(long time) {

        sum += (double)time / 10000000;
//        System.out.println(1 / ((double)time / 1000000000));

        fillBackground(0xFF000000);

        float cos = (float)cos(toRadians(sum));
        float sin = (float)sin(toRadians(sum));

        for (float depth = 12; depth > 1.5f; depth--) {
            Triangle3 triangle = new Triangle3(
                    new Vec3(-1.f + cos, -0.5f + sin, depth),
                    new Vec3(1.f + cos, -0.5f + sin, depth),
                    new Vec3(0f + cos, 0.5f + sin, depth)
            );

            triangle.draw(Colors[((int)depth) % Colors.length]);
        }
    }

    //масив можливих кольорів
    int[] Colors = {0XFFFF0000, 0XFFFFFF00, 0XFF00FF00, 0XFF00FFFF, 0XFF0000FF, 0XFFFF00FF};

    //функція для закрашування фону
    public static void fillBackground (int color) {
        for (int y = 0; y < GlobalState.getScreenHeight(); y++) {
            for (int x = 0; x < GlobalState.getScreenWidth(); x++) {
                GlobalState.Pixels[y * GlobalState.getScreenWidth() + x] = color;
            }
        }
    }

    //генерація кольору на основі 4-х чисел до 255
    public static int toRGBA(int alpha, int red , int green , int blue ) {
        return (alpha << 24) | (red << 16) | (green << 8) | blue;
    }



    public static void main(String[] args) {
        launch(args);
    }
}
