import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.WritableImage;
import javafx.scene.image.ImageView;
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

        // перевірка правельності відображення трикутників що перетинаються

        Matrix4 tr = Matrix4.scaleMatrix4(1, 1, 1).mult(
                Matrix4.rotationMatrix4(GlobalState.Time * 100, GlobalState.Time * 100, GlobalState.Time * 100)
        ).mult(
                Matrix4.translationMatrix4(0, 0, 2)
        );

//        Triangle3 triangle1 = new Triangle3(
//                new Vec4(0f, 0.5f , 0.f ),
//                new Vec4(0.5f, -0.5f, 0.f),
//                new Vec4(-0.5f, -0.5f, 0.f),
//                tr.clone(),
//                new Vec4[] {new Vec4(1, 0,0 ), new Vec4(0, 1,0 ), new Vec4(0, 0,1 )}
//        );
//
//        triangle1.draw();
//
//        Triangle3 triangle2 = new Triangle3(
//                new Vec4(0f, 0.5f , 0.f ),
//                new Vec4(0.5f, -0.5f, 0.2f),
//                new Vec4(-0.5f, -0.5f, -0.2f),
//                tr.clone(),
//                new Vec4[] {new Vec4(1, 1,0 ), new Vec4(0, 1,1 ), new Vec4(1, 0,1 )}
//        );
//
//        triangle2.draw();
//
//        Triangle3 triangle3 = new Triangle3(
//                new Vec4(0f, -0.5f , -0.4f ),
//                new Vec4(-1.5f, 0.5f, 2.0f),
//                new Vec4(1.5f, 0.5f, 2.f),
//                tr.clone(),
//                new Vec4[] {new Vec4(1, 1,0 ), new Vec4(0, 1,1 ), new Vec4(1, 0,1 )}
//        );
//
//        triangle3.draw();


        // Масив вершин трикутників
        Vec4[] VerticesBuffer = {
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
        };

        //масив кольорів вершин трикутників
        Vec4[] ColorsBuffer = {
                new Vec4( 1, 0, 0),
                new Vec4( 0, 1, 0),
                new Vec4( 0, 0, 1),
                new Vec4( 1, 0, 1),

                new Vec4( 1, 1, 0),
                new Vec4( 0, 1, 1),
                new Vec4( 1, 0, 1),
                new Vec4( 1, 1, 1),
        };

        // масив індексів вершин для відмальовування сторін
        int[] IndexBuffer = {
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
        };

        for (int i = 0; i < IndexBuffer.length; i+= 3) {
            new Triangle3(
                    VerticesBuffer[IndexBuffer[i]],
                    VerticesBuffer[IndexBuffer[i + 1]],
                    VerticesBuffer[IndexBuffer[i + 2]],
                    tr.clone(),
                    new Vec4[] {ColorsBuffer[IndexBuffer[i]], ColorsBuffer[IndexBuffer[i + 1]], ColorsBuffer[IndexBuffer[i + 2]]}
            ).draw();
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
