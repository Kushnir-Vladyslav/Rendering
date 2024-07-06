import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.WritableImage;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;


public class Main extends Application {

    private WritableImage writableImage;
    private ImageView imageView;
    private int width = 400;
    private int height = 400;

    private int[] pixels;

    @Override
    public void start(Stage primaryStage) {
        // Створюємо WritableImage об'єкт
        writableImage = new WritableImage(width, height);

        pixels = new int [width * height];


        // Створюємо ImageView для відображення WritableImage
        imageView = new ImageView(writableImage);

        // Створюємо сцену і додаємо ImageView
        StackPane root = new StackPane();
        root.getChildren().add(imageView);
        Scene scene = new Scene(root, width, height);


        primaryStage.setTitle("Random Color Animation App");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Додаємо обробники подій для зміни розміру вікна
        scene.widthProperty().addListener((observable, oldValue, newValue) -> {
            width = newValue.intValue();
            updateImageSize();
        });

        scene.heightProperty().addListener((observable, oldValue, newValue) -> {
            height = newValue.intValue();
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
                writableImage.getPixelWriter().setPixels(0, 0, width, height, PixelFormat.getIntArgbInstance(), pixels, 0, width);

                last = now;
            }
        };
        animationTimer.start();
    }

    private void updateImageSize() {
        writableImage = new WritableImage(width, height);
        pixels = new int[width * height];
        imageView.setImage(writableImage);
    }

    private double sum = 0;
    private void updatePixels(long time) {
        // Записуємо випадкові пікселі в WritableImage
        sum += (double)time / 10000000;
        System.out.println(1 / ((double)time / 1000000000));
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // Генеруємо випадковий колір
                pixels[y * width + x] = toRGBA(255, (x + (int)sum) % 256, y % 256, 0);
//                        to32BitInteger(0, (x + (int)sum) % 256, y % 256, 0);
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
