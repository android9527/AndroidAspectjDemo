package io.github.android9527.apt_api;

import android.view.View;

/**
 * Created by Litp on 2017/9/19.
 */

public abstract interface ViewInject<T> {
    void inject(T t, View view);
}