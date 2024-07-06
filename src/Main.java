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


    private int[] pixels;

    @Override
    public void start(Stage primaryStage) {
        // Створюємо WritableImage об'єкт
        writableImage = new WritableImage(GlobalState.getScreenWidth(), GlobalState.getScreenHeight());

        pixels = new int [GlobalState.getScreenWidth() * GlobalState.getScreenHeight()];


        // Створюємо ImageView для відображення WritableImage
        imageView = new ImageView(writableImage);

        // Створюємо сцену і додаємо ImageView
        StackPane root = new StackPane();
        root.getChildren().add(imageView);
        Scene scene = new Scene(root, GlobalState.getScreenWidth(), GlobalState.getScreenHeight());


        primaryStage.setTitle("Random Color Animation App");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Додаємо обробники подій для зміни розміру вікна
        scene.widthProperty().addListener((observable, oldValue, newValue) -> {
            GlobalState.setScreenWidth(newValue.intValue());
            updateImageSize();
        });

        scene.heightProperty().addListener((observable, oldValue, newValue) -> {
            GlobalState.setScreenHeight(newValue.intValue());
            updateImageSize();
        });

        // Запускаємо анімацію
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
                        PixelFormat.getIntArgbInstance(), pixels, 0, GlobalState.getScreenWidth());

                last = now;
            }
        };
        animationTimer.start();
    }

    private void updateImageSize() {
        writableImage = new WritableImage( GlobalState.getScreenWidth(), GlobalState.getScreenHeight());
        pixels = new int[ GlobalState.getScreenWidth() * GlobalState.getScreenHeight()];
        imageView.setImage(writableImage);
    }

    private double sum = 0;
    private void updatePixels(long time) {
        // Записуємо випадкові пікселі в WritableImage
        sum += (double)time / 10000000;
//        System.out.println(1 / ((double)time / 1000000000));
        for (int y = 0; y < GlobalState.getScreenHeight(); y++) {
            for (int x = 0; x < GlobalState.getScreenWidth(); x++) {
                // Генеруємо випадковий колір
                pixels[y * GlobalState.getScreenWidth() + x] = 0xFF000000;
//                        toRGBA(255, (x + (int)sum) % 256, y % 256, 0);
            }
        }

        for (float depth = 1.5f; depth < 1100; depth += 0.1f) {
            Triangle3 trian = new Triangle3(new Vec3(-1.f, -0.5f, depth),
                    new Vec3(1.f, -0.5f, depth),
                    new Vec3(0f, 0.5f, depth));

            for (int i = 0; i < 3; i++) {
                Vec2 v2 = trian.getPoint3(i).add(new Vec3((float)cos(toRadians(sum)),(float)sin(toRadians(sum)),0)).Prespective();
                if(v2.x() > 0f && v2.x() < GlobalState.getScreenWidth() &&
                v2.y() > 0 && v2.y() < GlobalState.getScreenHeight()) {
                    int pixelID = pixels.length - ((int) v2.y() + 1) * GlobalState.getScreenWidth() + (int) v2.x();
                    pixels[pixelID] = 0XFF00FF00;
                }
            }
        }
    }
    public static int toRGBA(int alpha, int red , int green , int blue ) {
        return (alpha << 24) | (red << 16) | (green << 8) | blue;
    }



    public static void main(String[] args) {
        launch(args);
    }
}
