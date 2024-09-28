package androidx.palette.graphics;

public final class Target {
    public static final Target DARK_MUTED;
    public static final Target DARK_VIBRANT;
    static final int INDEX_MAX = 2;
    static final int INDEX_MIN = 0;
    static final int INDEX_TARGET = 1;
    static final int INDEX_WEIGHT_LUMA = 1;
    static final int INDEX_WEIGHT_POP = 2;
    static final int INDEX_WEIGHT_SAT = 0;
    public static final Target LIGHT_MUTED;
    public static final Target LIGHT_VIBRANT;
    private static final float MAX_DARK_LUMA = 0.45f;
    private static final float MAX_MUTED_SATURATION = 0.4f;
    private static final float MAX_NORMAL_LUMA = 0.7f;
    private static final float MIN_LIGHT_LUMA = 0.55f;
    private static final float MIN_NORMAL_LUMA = 0.3f;
    private static final float MIN_VIBRANT_SATURATION = 0.35f;
    public static final Target MUTED;
    private static final float TARGET_DARK_LUMA = 0.26f;
    private static final float TARGET_LIGHT_LUMA = 0.74f;
    private static final float TARGET_MUTED_SATURATION = 0.3f;
    private static final float TARGET_NORMAL_LUMA = 0.5f;
    private static final float TARGET_VIBRANT_SATURATION = 1.0f;
    public static final Target VIBRANT;
    private static final float WEIGHT_LUMA = 0.52f;
    private static final float WEIGHT_POPULATION = 0.24f;
    private static final float WEIGHT_SATURATION = 0.24f;
    boolean mIsExclusive = true;
    final float[] mLightnessTargets = new float[3];
    final float[] mSaturationTargets;
    final float[] mWeights = new float[3];

    static {
        Target target = new Target();
        LIGHT_VIBRANT = target;
        setDefaultLightLightnessValues(target);
        setDefaultVibrantSaturationValues(LIGHT_VIBRANT);
        Target target2 = new Target();
        VIBRANT = target2;
        setDefaultNormalLightnessValues(target2);
        setDefaultVibrantSaturationValues(VIBRANT);
        Target target3 = new Target();
        DARK_VIBRANT = target3;
        setDefaultDarkLightnessValues(target3);
        setDefaultVibrantSaturationValues(DARK_VIBRANT);
        Target target4 = new Target();
        LIGHT_MUTED = target4;
        setDefaultLightLightnessValues(target4);
        setDefaultMutedSaturationValues(LIGHT_MUTED);
        Target target5 = new Target();
        MUTED = target5;
        setDefaultNormalLightnessValues(target5);
        setDefaultMutedSaturationValues(MUTED);
        Target target6 = new Target();
        DARK_MUTED = target6;
        setDefaultDarkLightnessValues(target6);
        setDefaultMutedSaturationValues(DARK_MUTED);
    }

    Target() {
        float[] fArr = new float[3];
        this.mSaturationTargets = fArr;
        setTargetDefaultValues(fArr);
        setTargetDefaultValues(this.mLightnessTargets);
        setDefaultWeights();
    }

    Target(Target from) {
        float[] fArr = new float[3];
        this.mSaturationTargets = fArr;
        System.arraycopy(from.mSaturationTargets, 0, fArr, 0, fArr.length);
        float[] fArr2 = from.mLightnessTargets;
        float[] fArr3 = this.mLightnessTargets;
        System.arraycopy(fArr2, 0, fArr3, 0, fArr3.length);
        float[] fArr4 = from.mWeights;
        float[] fArr5 = this.mWeights;
        System.arraycopy(fArr4, 0, fArr5, 0, fArr5.length);
    }

    public float getMinimumSaturation() {
        return this.mSaturationTargets[0];
    }

    public float getTargetSaturation() {
        return this.mSaturationTargets[1];
    }

    public float getMaximumSaturation() {
        return this.mSaturationTargets[2];
    }

    public float getMinimumLightness() {
        return this.mLightnessTargets[0];
    }

    public float getTargetLightness() {
        return this.mLightnessTargets[1];
    }

    public float getMaximumLightness() {
        return this.mLightnessTargets[2];
    }

    public float getSaturationWeight() {
        return this.mWeights[0];
    }

    public float getLightnessWeight() {
        return this.mWeights[1];
    }

    public float getPopulationWeight() {
        return this.mWeights[2];
    }

    public boolean isExclusive() {
        return this.mIsExclusive;
    }

    private static void setTargetDefaultValues(float[] values) {
        values[0] = 0.0f;
        values[1] = 0.5f;
        values[2] = 1.0f;
    }

    private void setDefaultWeights() {
        float[] fArr = this.mWeights;
        fArr[0] = 0.24f;
        fArr[1] = 0.52f;
        fArr[2] = 0.24f;
    }

    /* access modifiers changed from: package-private */
    public void normalizeWeights() {
        float sum = 0.0f;
        for (float weight : this.mWeights) {
            if (weight > 0.0f) {
                sum += weight;
            }
        }
        if (sum != 0.0f) {
            int z = this.mWeights.length;
            for (int i = 0; i < z; i++) {
                float[] fArr = this.mWeights;
                if (fArr[i] > 0.0f) {
                    fArr[i] = fArr[i] / sum;
                }
            }
        }
    }

    private static void setDefaultDarkLightnessValues(Target target) {
        float[] fArr = target.mLightnessTargets;
        fArr[1] = 0.26f;
        fArr[2] = 0.45f;
    }

    private static void setDefaultNormalLightnessValues(Target target) {
        float[] fArr = target.mLightnessTargets;
        fArr[0] = 0.3f;
        fArr[1] = 0.5f;
        fArr[2] = 0.7f;
    }

    private static void setDefaultLightLightnessValues(Target target) {
        float[] fArr = target.mLightnessTargets;
        fArr[0] = 0.55f;
        fArr[1] = 0.74f;
    }

    private static void setDefaultVibrantSaturationValues(Target target) {
        float[] fArr = target.mSaturationTargets;
        fArr[0] = 0.35f;
        fArr[1] = 1.0f;
    }

    private static void setDefaultMutedSaturationValues(Target target) {
        float[] fArr = target.mSaturationTargets;
        fArr[1] = 0.3f;
        fArr[2] = 0.4f;
    }

    public static final class Builder {
        private final Target mTarget;

        public Builder() {
            this.mTarget = new Target();
        }

        public Builder(Target target) {
            this.mTarget = new Target(target);
        }

        public Builder setMinimumSaturation(float value) {
            this.mTarget.mSaturationTargets[0] = value;
            return this;
        }

        public Builder setTargetSaturation(float value) {
            this.mTarget.mSaturationTargets[1] = value;
            return this;
        }

        public Builder setMaximumSaturation(float value) {
            this.mTarget.mSaturationTargets[2] = value;
            return this;
        }

        public Builder setMinimumLightness(float value) {
            this.mTarget.mLightnessTargets[0] = value;
            return this;
        }

        public Builder setTargetLightness(float value) {
            this.mTarget.mLightnessTargets[1] = value;
            return this;
        }

        public Builder setMaximumLightness(float value) {
            this.mTarget.mLightnessTargets[2] = value;
            return this;
        }

        public Builder setSaturationWeight(float weight) {
            this.mTarget.mWeights[0] = weight;
            return this;
        }

        public Builder setLightnessWeight(float weight) {
            this.mTarget.mWeights[1] = weight;
            return this;
        }

        public Builder setPopulationWeight(float weight) {
            this.mTarget.mWeights[2] = weight;
            return this;
        }

        public Builder setExclusive(boolean exclusive) {
            this.mTarget.mIsExclusive = exclusive;
            return this;
        }

        public Target build() {
            return this.mTarget;
        }
    }
}
