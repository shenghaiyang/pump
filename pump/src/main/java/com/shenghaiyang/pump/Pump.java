package com.shenghaiyang.pump;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;

/**
 * @author shenghaiyang
 */
public final class Pump {

    @NonNull
    public static <T extends ViewBinding> T inflate(@NonNull Class<T> viewBindingClass, @NonNull LayoutInflater inflater) {
        return inflate(viewBindingClass, inflater, null, false);
    }

    @NonNull
    public static <T extends ViewBinding> T inflate(@NonNull Class<T> viewBindingClass, @NonNull LayoutInflater inflater, @Nullable ViewGroup root) {
        return inflate(viewBindingClass, inflater, root, null != root);
    }

    @NonNull
    @SuppressWarnings("unchecked")
    public static <T extends ViewBinding> T inflate(@NonNull Class<T> viewBindingClass, @NonNull LayoutInflater inflater, @Nullable ViewGroup root, boolean attachToRoot) {
        ViewBindingInflater viewBindingInflater = InflaterStore.getInstance().get(viewBindingClass);
        boolean attach = null != root && attachToRoot;
        return (T) viewBindingInflater.inflate(inflater, root, attach);
    }

    private Pump() {
        throw new AssertionError("No instance.");
    }

}
