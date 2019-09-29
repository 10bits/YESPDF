package com.aaron.yespdf.common;

import android.os.Bundle;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.aaron.base.base.BaseActivity;

/**
 * @author Aaron aaronzzxup@gmail.com
 */
public abstract class CommonActivity extends BaseActivity {

    protected Toolbar toolbar;

    @LayoutRes protected abstract int layoutId();

    protected abstract Toolbar createToolbar();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layoutId());
        toolbar = createToolbar();
        setSupportActionBar(toolbar);
        UiManager.setStatusBar(this, toolbar);
    }
}
