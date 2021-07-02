package com.app.androidtest.view.bottomSheetDialogs;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.app.androidtest.R;
import com.app.androidtest.repo.local.Cartdb;
import com.app.androidtest.repo.remote.model.Product;
import com.app.androidtest.viewModel.userOperationsViewModel;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class deleteImage extends BottomSheetDialogFragment {
    CardView cardView;
    private BottomSheetBehavior mBehavior;
    private BottomSheetDialog dialog;

    private TextView title;
    private AppCompatButton confirm,cancel;

    private View view;

    private userOperationsViewModel viewModel;

    private Cartdb dataSource;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);
    }

    @SuppressLint("NewApi")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        view = View.inflate(getContext(), R.layout.delete_image, null);
        cardView = view.findViewById(R.id.cardView);
        cardView.setBackgroundResource(R.drawable.card_view_bg);
        LinearLayout linearLayout = view.findViewById(R.id.root);
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) linearLayout.getLayoutParams();
        params.height = getScreenHeight();
        linearLayout.setLayoutParams(params);
        ((View) linearLayout.getParent()).setBackgroundColor(getResources().getColor(android.R.color.transparent));
        dialog.setContentView(view);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        mBehavior = BottomSheetBehavior.from((View) view.getParent());

        Product product = deleteImageArgs.fromBundle(getArguments()).getProduct();

        title=view.findViewById(R.id.title);
        confirm=view.findViewById(R.id.confirm);
        cancel=view.findViewById(R.id.cancel);

        title.setText(product.getTitle());

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(deleteImage.this).navigateUp();
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                product.setDeleted(true);
                viewModel.updateProducts(dataSource,product);
                viewModel.getProducts(dataSource);
                NavHostFragment.findNavController(deleteImage.this).navigateUp();
            }
        });

        dataSource = Cartdb.getCartdb(getContext());

        viewModel = new ViewModelProvider(this).get(userOperationsViewModel.class);


        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        mBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }


}
