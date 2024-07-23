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

        //анімація, по колу рухіється декілька трикутників на різній відстані
//        for (float depth = 12; depth > 1.5f; depth--) {
//            Triangle3 triangle = new Triangle3(
//                    new Vec3(-1.f + cos, -0.5f + sin, depth),
//                    new Vec3(1.f + cos, -0.5f + sin, depth),
//                    new Vec3(0f + cos, 0.5f + sin, depth)
//            );
//
//            triangle.draw(Colors[((int)depth) % Colors.length]);
//        }

        //альтернативна анімація, поколу рухіється трикутник то наближаючись то віддаляючись
        float depth = 2f + (float)(sum / 100 % 16);
        if (depth > 10) depth = 20.f - depth;
        Triangle3 triangle = new Triangle3(


                new Vec3(0f + cos, 0.5f + sin, depth),
                new Vec3(1.f + cos, -0.5f + sin, depth + cos),
                new Vec3(-1.f + cos, -0.5f + sin, depth + sin),
                receiveColors((float)sum)
        );

        triangle.draw();

    }

    //генерує кольори для кутів, які поступово змінюються
    private static Vec3[] receiveColors(float delt) {
        delt /= 100;
        delt %= 3;
        if (delt < 1.f) {
            return new Vec3[] {new Vec3(1 - delt, delt, 0), new Vec3(0, 1 - delt, delt), new Vec3(delt, 0, 1 - delt)};
        } else if (delt < 2.f) {
            delt -= 1.f;
            return new Vec3[] {new Vec3(0, 1 - delt, delt), new Vec3(delt, 0, 1 - delt), new Vec3(1 - delt, delt, 0)};
        } else {
            delt -= 2.f;
            return new Vec3[] {new Vec3(delt, 0, 1 - delt), new Vec3(1 - delt, delt, 0), new Vec3(0, 1 - delt, delt)};
        }
    }

    //масив можливих кольорів
    Vec3[] Colors = {new Vec3(1, 0, 0),
            new Vec3(1, 1, 0),
            new Vec3(0, 1, 0),
            new Vec3(0, 1, 1),
            new Vec3(0, 0, 1),
            new Vec3(1, 0, 1)};

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
