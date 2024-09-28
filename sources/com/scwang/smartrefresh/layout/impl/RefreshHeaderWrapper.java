package com.scwang.smartrefresh.layout.impl;

import android.view.View;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.internal.InternalAbstract;

public class RefreshHeaderWrapper extends InternalAbstract implements RefreshHeader {
    public RefreshHeaderWrapper(View wrapper) {
        super(wrapper);
    }
}
