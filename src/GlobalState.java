public class GlobalState {
    private static int screenWidth = 400;
    private static int screenHeight = 400;


    public static int getScreenWidth() {
        return screenWidth;
    }

    public static int getScreenHeight() {
        return screenHeight;
    }

    public static void setScreenWidth(int width) {
        screenWidth = width;
    }

    public static void setScreenHeight(int height) {
        screenHeight = height;
    }

    public static void setScreenSize(int width, int height) {
        screenWidth = width;
        screenHeight = height;
    }
}
