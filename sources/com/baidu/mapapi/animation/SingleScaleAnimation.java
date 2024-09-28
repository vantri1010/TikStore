package com.baidu.mapapi.animation;

import android.view.animation.Interpolator;
import com.baidu.mapapi.animation.Animation;
import com.baidu.mapsdkplatform.comapi.a.c;
import com.baidu.mapsdkplatform.comapi.a.j;

public class SingleScaleAnimation extends Animation {

    public enum ScaleType {
        SCALE_X,
        SCALE_Y
    }

    public SingleScaleAnimation(ScaleType scaleType, float... fArr) {
        j jVar;
        if (fArr == null || fArr.length == 0) {
            throw new NullPointerException("BDMapSDKException: the scales is null");
        }
        if (scaleType == ScaleType.SCALE_X) {
            jVar = new j(1, fArr);
        } else if (scaleType == ScaleType.SCALE_Y) {
            jVar = new j(2, fArr);
        } else {
            return;
        }
        this.bdAnimation = jVar;
    }

    public void cancel() {
        this.bdAnimation.b();
    }

    public void setAnimationListener(Animation.AnimationListener animationListener) {
        this.bdAnimation.a(animationListener);
    }

    public void setDuration(long j) {
        this.bdAnimation.a(j);
    }

    public void setInterpolator(Interpolator interpolator) {
        this.bdAnimation.a(interpolator);
    }

    public void setRepeatCount(int i) {
        this.bdAnimation.b(i);
    }

    public void setRepeatMode(Animation.RepeatMode repeatMode) {
        c cVar;
        int i;
        if (repeatMode == Animation.RepeatMode.RESTART) {
            cVar = this.bdAnimation;
            i = 1;
        } else if (repeatMode == Animation.RepeatMode.REVERSE) {
            cVar = this.bdAnimation;
            i = 2;
        } else {
            return;
        }
        cVar.a(i);
    }
}
