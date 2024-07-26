public class GlobalState {
    //початковий розмір вікна, може змінюватись користувачем
    private static int ScreenWidth = 400;
    private static int ScreenHeight = 400;

    //масив пікселів що відображаються на екрані
    public static int[] Pixels;

    //масив глибин пікселів
    public static float [] DepthBuffer;

    //Час від початку запуску програми
    public static float Time = 0.f;

    public static int getScreenWidth() {
        return ScreenWidth;
    }

    public static int getScreenHeight() {
        return ScreenHeight;
    }

    public static void setScreenWidth(int Width) {
        ScreenWidth = Width;
    }

    public static void setScreenHeight(int Height) {
        ScreenHeight = Height;
    }

    public static void setScreenSize(int Width, int Height) {
        ScreenWidth = Width;
        ScreenHeight = Height;
    }
}
