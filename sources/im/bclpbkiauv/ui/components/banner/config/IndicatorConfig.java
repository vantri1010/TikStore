package im.bclpbkiauv.ui.components.banner.config;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class IndicatorConfig {
    private boolean attachToBanner = true;
    private int currentPosition;
    private int gravity = 1;
    private float height = BannerConfig.INDICATOR_HEIGHT;
    private int indicatorSize;
    private float indicatorSpace = BannerConfig.INDICATOR_SPACE;
    private Margins margins;
    private int normalColor = BannerConfig.INDICATOR_NORMAL_COLOR;
    private float normalWidth = BannerConfig.INDICATOR_NORMAL_WIDTH;
    private float radius = BannerConfig.INDICATOR_RADIUS;
    private int selectedColor = BannerConfig.INDICATOR_SELECTED_COLOR;
    private float selectedWidth = BannerConfig.INDICATOR_SELECTED_WIDTH;

    @Retention(RetentionPolicy.SOURCE)
    public @interface Direction {
        public static final int CENTER = 1;
        public static final int LEFT = 0;
        public static final int RIGHT = 2;
    }

    public static class Margins {
        public int bottomMargin;
        public int leftMargin;
        public int rightMargin;
        public int topMargin;

        public Margins() {
            this(BannerConfig.INDICATOR_MARGIN);
        }

        public Margins(int marginSize) {
            this(marginSize, marginSize, marginSize, marginSize);
        }

        public Margins(int leftMargin2, int topMargin2, int rightMargin2, int bottomMargin2) {
            this.leftMargin = leftMargin2;
            this.topMargin = topMargin2;
            this.rightMargin = rightMargin2;
            this.bottomMargin = bottomMargin2;
        }
    }

    public Margins getMargins() {
        if (this.margins == null) {
            setMargins(new Margins());
        }
        return this.margins;
    }

    public IndicatorConfig setMargins(Margins margins2) {
        this.margins = margins2;
        return this;
    }

    public int getIndicatorSize() {
        return this.indicatorSize;
    }

    public IndicatorConfig setIndicatorSize(int indicatorSize2) {
        this.indicatorSize = indicatorSize2;
        return this;
    }

    public int getNormalColor() {
        return this.normalColor;
    }

    public IndicatorConfig setNormalColor(int normalColor2) {
        this.normalColor = normalColor2;
        return this;
    }

    public int getSelectedColor() {
        return this.selectedColor;
    }

    public IndicatorConfig setSelectedColor(int selectedColor2) {
        this.selectedColor = selectedColor2;
        return this;
    }

    public float getIndicatorSpace() {
        return this.indicatorSpace;
    }

    public IndicatorConfig setIndicatorSpace(float indicatorSpace2) {
        this.indicatorSpace = indicatorSpace2;
        return this;
    }

    public int getCurrentPosition() {
        return this.currentPosition;
    }

    public IndicatorConfig setCurrentPosition(int currentPosition2) {
        this.currentPosition = currentPosition2;
        return this;
    }

    public float getNormalWidth() {
        return this.normalWidth;
    }

    public IndicatorConfig setNormalWidth(float normalWidth2) {
        this.normalWidth = normalWidth2;
        return this;
    }

    public float getSelectedWidth() {
        return this.selectedWidth;
    }

    public IndicatorConfig setSelectedWidth(float selectedWidth2) {
        this.selectedWidth = selectedWidth2;
        return this;
    }

    public int getGravity() {
        return this.gravity;
    }

    public IndicatorConfig setGravity(int gravity2) {
        this.gravity = gravity2;
        return this;
    }

    public boolean isAttachToBanner() {
        return this.attachToBanner;
    }

    public IndicatorConfig setAttachToBanner(boolean attachToBanner2) {
        this.attachToBanner = attachToBanner2;
        return this;
    }

    public float getRadius() {
        return this.radius;
    }

    public IndicatorConfig setRadius(float radius2) {
        this.radius = radius2;
        return this;
    }

    public float getHeight() {
        return this.height;
    }

    public IndicatorConfig setHeight(float height2) {
        this.height = height2;
        return this;
    }
}
