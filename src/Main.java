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


         //куб
        GlobalState.Objects.add(new drawableObject(
                // Масив вершин трикутників
        new vec4D[] {
                // передня стінка куба
                new vec4D( -0.5f, -0.5f, -0.5f),
                new vec4D( -0.5f, 0.5f, -0.5f),
                new vec4D( 0.5f, 0.5f, -0.5f),
                new vec4D( 0.5f, -0.5f, -0.5f),
                // задня стінка куба
                new vec4D( -0.5f, -0.5f, 0.5f),
                new vec4D( -0.5f, 0.5f, 0.5f),
                new vec4D( 0.5f, 0.5f, 0.5f),
                new vec4D( 0.5f, -0.5f, 0.5f),
        },
        //масив вершин на текстурі
        new vec2D[] {
                new vec2D( 0, 0),
                new vec2D( 1, 0),
                new vec2D( 1, 1),
                new vec2D( 0, 1),

                new vec2D( 0, 0),
                new vec2D( 1, 0),
                new vec2D( 1, 1),
                new vec2D( 0, 1),

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
        },
                new texture()
        ));

//        //піраміда
//        GlobalState.Objects.add( new drawableObject(
//                // Масив вершин трикутників
//                new vec4D[] {
//                        // вершини основи піраміди
//                        new vec4D(-0.5f, 0.0f, -0.5f),  // 0
//                        new vec4D(0.5f, 0.0f, -0.5f),   // 1
//                        new vec4D(0.5f, 0.0f, 0.5f),    // 2
//                        new vec4D(-0.5f, 0.0f, 0.5f),   // 3
//                        // вершина піраміди
//                        new vec4D(0.0f, 1.0f, 0.0f),    // 4
//                },
//                // Масив кольорів вершин трикутників
//                new vec2D[] {
//                        new vec2D( 0, 0),
//                        new vec2D( 1, 0),
//                        new vec2D( 1, 1),
//                        new vec2D( 0, 1),
//                        new vec2D(1, 1),
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
//                new texture()
//        ));

//        // кубик Д20
//        GlobalState.Objects.add( new drawableObject(
//        // Масив вершин ікосаедра
//        new vec4D[] {
//                new vec4D(0, 0, 1),  // вершина 0
//                new vec4D(0.8944f, 0, 0.4472f),  // вершина 1
//                new vec4D(0.2764f, 0.8506f, 0.4472f),  // вершина 2
//                new vec4D(-0.7236f, 0.5257f, 0.4472f),  // вершина 3
//                new vec4D(-0.7236f, -0.5257f, 0.4472f),  // вершина 4
//                new vec4D(0.2764f, -0.8506f, 0.4472f),  // вершина 5
//                new vec4D(0.7236f, 0.5257f, -0.4472f),  // вершина 6
//                new vec4D(-0.2764f, 0.8506f, -0.4472f),  // вершина 7
//                new vec4D(-0.8944f, 0, -0.4472f),  // вершина 8
//                new vec4D(-0.2764f, -0.8506f, -0.4472f),  // вершина 9
//                new vec4D(0.7236f, -0.5257f, -0.4472f),  // вершина 10
//                new vec4D(0, 0, -1),  // вершина 11
//        },
//// Масив кольорів вершин трикутників
//                new vec2D[] {
//                        new vec2D( 0, 0),
//                        new vec2D( 1, 0),
//                        new vec2D( 1, 1),
//                        new vec2D( 0, 1),
//                        new vec2D(1, 1),
//                        new vec2D( 1, 0),
//                        new vec2D( 0, 0),
//                        new vec2D( 1, 0),
//                        new vec2D( 1, 1),
//                        new vec2D( 0, 1),
//                        new vec2D(1, 1),
//                        new vec2D( 1, 0),
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
//                new texture()
//));

//         //Трикутник
//        GlobalState.Objects.add(new drawableObject(
//                // Масив вершин трикутників
//                new vec4D[] {
//                        // передня стінка куба
//                        new vec4D( -0.5f, -0.5f, -0.5f),
//                        new vec4D( -0.5f, 0.5f, -0.5f),
//                        new vec4D( 0.5f, 0.5f, -0.5f),
//                },
//                //масив вершин на текстурі
//                new vec2D[] {
//                        new vec2D( 0, 0),
//                        new vec2D( 1, 0),
//                        new vec2D( 1, 1),
//
//                },
//                // масив індексів вершин для відмальовування сторін
//                new int[] {
//                        // передня сторона
//                        0, 1, 2
//                },
//                new texture()
//        ));


        //обробники подій зміни розміру вікна
        scene.widthProperty().addListener((observable, oldValue, newValue) -> {
            GlobalState.setScreenWidth(newValue.intValue());
            updateImageSize();
            GlobalState.camera.updatePerspective();
        });

        scene.heightProperty().addListener((observable, oldValue, newValue) -> {
            GlobalState.setScreenHeight(newValue.intValue());
            updateImageSize();
            GlobalState.camera.updatePerspective();
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


//        System.out.println(1f / GlobalState.Time);
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



//        //формування матриці перетворення для обєкту, порядок: розмір, поворот, переміщення
//        tr =  matrix4D.scaleMatrix4(1, 1, 1).mult(
//                matrix4D.rotationMatrix4(GlobalState.Time * 100, GlobalState.Time * 100, GlobalState.Time * 100)
//        ).mult(
//                matrix4D.translationMatrix4(0, 0, 10)
//        );
//
//        //монження матриці трансформації обєкту з матрицею трансформації камери, послідовність: камера, обєкт
//        tr = matrix4D.mult(GlobalState.camera.getCameraTransform(), tr);
//
//        //множення матриці трансофрмації обєку і камери з матрицею трасформації перспективи, послідовність: перспектив, інш.
//        tr = matrix4D.mult(GlobalState.camera.getPerspectiveMatrix(), tr);
//
//        for (int i = 1; i < 2; i++) {
//            GlobalState.Objects.get(i).draw(tr);
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
