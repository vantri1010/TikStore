package im.bclpbkiauv.ui.components.banner.indicator;

import android.view.View;
import im.bclpbkiauv.ui.components.banner.config.IndicatorConfig;
import im.bclpbkiauv.ui.components.banner.listener.OnPageChangeListener;

public interface Indicator extends OnPageChangeListener {
    IndicatorConfig getIndicatorConfig();

    View getIndicatorView();

    void onPageChanged(int i, int i2);
}
