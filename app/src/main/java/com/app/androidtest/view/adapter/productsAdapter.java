package com.app.androidtest.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.app.androidtest.R;
import com.app.androidtest.custom.ItemMoveCallback;
import com.app.androidtest.repo.remote.model.Product;
import com.app.androidtest.view.interfaces.ItemTouchHelperAdapter;
import com.app.androidtest.view.interfaces.StartDragListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class productsAdapter extends RecyclerView.Adapter<productsAdapter.MyViewHolder>
        implements ItemTouchHelperAdapter {
    private String imageUrl;
    private List<Product> list;
    private Context context;
    private StartDragListener listener;
    private boolean adjustSize;

    public productsAdapter(Context context, List<Product> list, String imageUrl, StartDragListener listener) {
        this.list = list;
        this.imageUrl = imageUrl;
        this.context = context;
        this.listener = listener;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.products_item, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;

    }


    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Product data = list.get(position);
        Glide.with(context).load(data.getImage().contains("/storage")?
                data.getImage():data.getImage().contains("http") ? data.getImage() : imageUrl + "/" + data.getImage())
                .diskCacheStrategy(DiskCacheStrategy.DATA).into(holder.productImage);
        holder.productName.setText(data.getTitle());
        holder.descVal.setText(data.getDesc());
        holder.productImage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() ==
                        MotionEvent.ACTION_DOWN) {
                    if (listener!=null) {
                        listener.requestDrag(holder);
                    }
                }
                return false;
            }
        });
        if (adjustSize){
            ConstraintLayout.LayoutParams imageViewParams = new ConstraintLayout.LayoutParams(
                    ViewGroup.MarginLayoutParams.WRAP_CONTENT,
                    ViewGroup.MarginLayoutParams.WRAP_CONTENT);
            holder.productImage.setLayoutParams(imageViewParams);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setData(List<Product> products) {
        int preSize = this.list.size();
        this.list.clear();
        notifyItemRangeRemoved(0,preSize);
        this.list.addAll(products);
        notifyItemRangeInserted(0,products.size());

    }

    @Override
    public void onItemDismiss(int position) {
        list.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        Collections.swap(list, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    public void adjustSizes() {
        adjustSize=adjustSize?false:true;
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView productImage;
        public TextView productName, descVal;
        public ImageButton share,delete;

        public MyViewHolder(View view) {
            super(view);
            productImage =  view.findViewById(R.id.productImage);
            productName =  view.findViewById(R.id.productName);
            descVal =  view.findViewById(R.id.descVal);
            share =  view.findViewById(R.id.share);
            delete =  view.findViewById(R.id.delete);

            if (listener==null){
                share.setVisibility(View.INVISIBLE);
                delete.setVisibility(View.INVISIBLE);
            }

            share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.share(list.get(getAdapterPosition()));
                }
            });

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.delete(list.get(getAdapterPosition()));
                }
            });
        }
    }

    public void removeItem(int position) {
        list.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreItem(Product item, int position) {
        list.add(position, item);
        notifyItemInserted(position);
    }

    public List<Product> getData() {
        return list;
    }




}