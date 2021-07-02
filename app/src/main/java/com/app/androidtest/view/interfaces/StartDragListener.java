package com.app.androidtest.view.interfaces;

import androidx.recyclerview.widget.RecyclerView;

import com.app.androidtest.repo.remote.model.Product;

public interface StartDragListener {
    void requestDrag(RecyclerView.ViewHolder viewHolder);

    void afterDrag(Product product, Product product1);

    void delete(Product product);

    void share(Product product);
}
