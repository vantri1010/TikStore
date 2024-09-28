package im.bclpbkiauv.ui.hui.friendscircle_v1.player.state;

public final class ScreenViewState {
    public static final int SCREEN_STATE_FULLSCREEN = 3;
    public static final int SCREEN_STATE_LIST_FULLSCREEN = 2;
    public static final int SCREEN_STATE_NORMAL = 1;
    public static final int SCREEN_STATE_SMALL_WINDOW = 4;

    public static boolean isFullScreen(int screenState) {
        return screenState == 3;
    }

    public static boolean isSmallWindow(int screenState) {
        return screenState == 4;
    }

    public static boolean isNormal(int screenState) {
        return screenState == 1;
    }

    public static boolean isFullScreenList(int screenState) {
        return screenState == 2;
    }
}
