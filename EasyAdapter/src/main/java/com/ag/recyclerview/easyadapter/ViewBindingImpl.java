package com.ag.recyclerview.easyadapter;
public interface ViewBindingImpl<V, M> extends ItemAdapter.ViewBinding<V, M> {
    @Override
    default int itemType(int position) {
        return 0;
    }

    @Override
    default boolean bindItem(V view, M item, int position, int itemType) {
        return false;
    }
}
