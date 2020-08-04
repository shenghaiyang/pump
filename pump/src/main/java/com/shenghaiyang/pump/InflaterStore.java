package com.shenghaiyang.pump;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

final class InflaterStore {

    private static final InflaterStore INSTANCE;

    static {
        INSTANCE = new InflaterStore();
    }

    private final Map<Class<?>, ViewBindingInflater> mInflaters;

    private InflaterStore() {
        mInflaters = new HashMap<>();
        //insert code here
    }

    @NonNull
    ViewBindingInflater get(Class<?> viewBindingClass) {
        return mInflaters.get(viewBindingClass);
    }

    static InflaterStore getInstance() {
        return INSTANCE;
    }

}
