package com.baidu.mapapi.animation;

import android.view.animation.Interpolator;
import com.baidu.mapapi.animation.Animation;
import com.baidu.mapsdkplatform.comapi.a.c;
import com.baidu.mapsdkplatform.comapi.a.f;

public class RotateAnimation extends Animation {
    public RotateAnimation(float f, float f2) {
        if (f < 0.0f || f2 < 0.0f) {
            throw new NullPointerException("BDMapSDKException: the degrees can't less than zero");
        }
        this.bdAnimation = new f(f, f2);
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
