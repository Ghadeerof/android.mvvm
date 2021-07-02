package com.app.androidtest.view.fragment;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.annimon.stream.Stream;
import com.app.androidtest.AppSession;
import com.app.androidtest.R;
import com.app.androidtest.custom.ItemMoveCallback;
import com.app.androidtest.custom.SwipeToDeleteCallback;
import com.app.androidtest.repo.local.Cartdb;
import com.app.androidtest.repo.remote.model.Product;
import com.app.androidtest.view.adapter.productsAdapter;
import com.app.androidtest.viewModel.userOperationsViewModel;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.Comparator;
import java.util.stream.Collectors;

import io.supercharge.shimmerlayout.ShimmerLayout;

public class trash extends Fragment {
    ShimmerLayout imagesShimmer;
    RecyclerView imagesRecycler;
    ImageView noDataImage;
    TextView noDataText;

    View view;

    userOperationsViewModel viewModel;

    productsAdapter adapter;

    Cartdb dataSource;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.trash,null,false);

        imagesShimmer = view.findViewById(R.id.imagesShimmer);
        imagesRecycler = view.findViewById(R.id.imagesRecycler);
        noDataImage = view.findViewById(R.id.noDataImage);
        noDataText = view.findViewById(R.id.noDataText);

        dataSource = Cartdb.getCartdb(getContext());

        viewModel = new ViewModelProvider(this).get(userOperationsViewModel.class);

        viewModel.getDeletedProducts(dataSource);

        viewModel.getProductsRes.observe(getViewLifecycleOwner(),products -> {
            if (!products.isEmpty()){
                imagesShimmer.stopShimmerAnimation();
                imagesShimmer.setVisibility(View.GONE);
                imagesRecycler.setVisibility(View.VISIBLE);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    products=products.stream()
                            .sorted(Comparator.comparingLong(Product::getId))
                            .collect(Collectors.toList());
                }else {
                    products= Stream.of(products).sortBy(product -> product.getId()).toList();
                }

                if (adapter==null){

                    adapter=new productsAdapter(getContext(),products, AppSession.imageURL,null);
                    SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(getContext()) {
                        @Override
                        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {


                            final int position = viewHolder.getAdapterPosition();
                            final Product item = adapter.getData().get(position);

                            adapter.removeItem(position);

                            viewModel.deleteProduct(dataSource,item);

                            Snackbar snackbar = Snackbar
                                    .make(getView(), "Item was removed from the list.", Snackbar.LENGTH_LONG);
                            snackbar.setAction("UNDO", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    viewModel.addProduct(dataSource,item);
                                    adapter.restoreItem(item, position);
                                    imagesRecycler.scrollToPosition(position);

                                }
                            });

                            snackbar.setActionTextColor(Color.YELLOW);
                            snackbar.show();

                        }
                    };

                    ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
                    itemTouchhelper.attachToRecyclerView(imagesRecycler);
                    imagesRecycler.setAdapter(adapter);
                }else {
                    adapter.setData(products);
                }

                noDataImage.setVisibility(View.GONE);
                noDataText.setVisibility(View.GONE);

            }else {
                imagesShimmer.stopShimmerAnimation();
                imagesShimmer.setVisibility(View.GONE);
                noDataImage.setVisibility(View.VISIBLE);
                noDataText.setVisibility(View.VISIBLE);
            }
        });



        return view;
    }


}
