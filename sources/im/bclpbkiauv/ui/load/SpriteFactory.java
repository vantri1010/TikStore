package im.bclpbkiauv.ui.load;

import im.bclpbkiauv.ui.load.sprite.Sprite;
import im.bclpbkiauv.ui.load.style.ChasingDots;
import im.bclpbkiauv.ui.load.style.Circle;
import im.bclpbkiauv.ui.load.style.CubeGrid;
import im.bclpbkiauv.ui.load.style.DoubleBounce;
import im.bclpbkiauv.ui.load.style.FadingCircle;
import im.bclpbkiauv.ui.load.style.FoldingCube;
import im.bclpbkiauv.ui.load.style.MultiplePulse;
import im.bclpbkiauv.ui.load.style.MultiplePulseRing;
import im.bclpbkiauv.ui.load.style.Pulse;
import im.bclpbkiauv.ui.load.style.PulseRing;
import im.bclpbkiauv.ui.load.style.RotatingCircle;
import im.bclpbkiauv.ui.load.style.RotatingPlane;
import im.bclpbkiauv.ui.load.style.ThreeBounce;
import im.bclpbkiauv.ui.load.style.WanderingCubes;
import im.bclpbkiauv.ui.load.style.Wave;

public class SpriteFactory {

    /* renamed from: im.bclpbkiauv.ui.load.SpriteFactory$1  reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$im$bclpbkiauv$ui$load$Style;

        static {
            int[] iArr = new int[Style.values().length];
            $SwitchMap$im$bclpbkiauv$ui$load$Style = iArr;
            try {
                iArr[Style.ROTATING_PLANE.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$im$bclpbkiauv$ui$load$Style[Style.DOUBLE_BOUNCE.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$im$bclpbkiauv$ui$load$Style[Style.WAVE.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$im$bclpbkiauv$ui$load$Style[Style.WANDERING_CUBES.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$im$bclpbkiauv$ui$load$Style[Style.PULSE.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$im$bclpbkiauv$ui$load$Style[Style.CHASING_DOTS.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$im$bclpbkiauv$ui$load$Style[Style.THREE_BOUNCE.ordinal()] = 7;
            } catch (NoSuchFieldError e7) {
            }
            try {
                $SwitchMap$im$bclpbkiauv$ui$load$Style[Style.CIRCLE.ordinal()] = 8;
            } catch (NoSuchFieldError e8) {
            }
            try {
                $SwitchMap$im$bclpbkiauv$ui$load$Style[Style.CUBE_GRID.ordinal()] = 9;
            } catch (NoSuchFieldError e9) {
            }
            try {
                $SwitchMap$im$bclpbkiauv$ui$load$Style[Style.FADING_CIRCLE.ordinal()] = 10;
            } catch (NoSuchFieldError e10) {
            }
            try {
                $SwitchMap$im$bclpbkiauv$ui$load$Style[Style.FOLDING_CUBE.ordinal()] = 11;
            } catch (NoSuchFieldError e11) {
            }
            try {
                $SwitchMap$im$bclpbkiauv$ui$load$Style[Style.ROTATING_CIRCLE.ordinal()] = 12;
            } catch (NoSuchFieldError e12) {
            }
            try {
                $SwitchMap$im$bclpbkiauv$ui$load$Style[Style.MULTIPLE_PULSE.ordinal()] = 13;
            } catch (NoSuchFieldError e13) {
            }
            try {
                $SwitchMap$im$bclpbkiauv$ui$load$Style[Style.PULSE_RING.ordinal()] = 14;
            } catch (NoSuchFieldError e14) {
            }
            try {
                $SwitchMap$im$bclpbkiauv$ui$load$Style[Style.MULTIPLE_PULSE_RING.ordinal()] = 15;
            } catch (NoSuchFieldError e15) {
            }
        }
    }

    public static Sprite create(Style style) {
        switch (AnonymousClass1.$SwitchMap$im$bclpbkiauv$ui$load$Style[style.ordinal()]) {
            case 1:
                return new RotatingPlane();
            case 2:
                return new DoubleBounce();
            case 3:
                return new Wave();
            case 4:
                return new WanderingCubes();
            case 5:
                return new Pulse();
            case 6:
                return new ChasingDots();
            case 7:
                return new ThreeBounce();
            case 8:
                return new Circle();
            case 9:
                return new CubeGrid();
            case 10:
                return new FadingCircle();
            case 11:
                return new FoldingCube();
            case 12:
                return new RotatingCircle();
            case 13:
                return new MultiplePulse();
            case 14:
                return new PulseRing();
            case 15:
                return new MultiplePulseRing();
            default:
                return null;
        }
    }
}
