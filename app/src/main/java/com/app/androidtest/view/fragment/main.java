package com.app.androidtest.view.fragment;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDeepLinkBuilder;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.annimon.stream.Stream;
import com.app.androidtest.AppSession;
import com.app.androidtest.R;
import com.app.androidtest.custom.ImageFilePath;
import com.app.androidtest.custom.ItemMoveCallback;
import com.app.androidtest.repo.local.Cartdb;
import com.app.androidtest.repo.remote.model.Product;
import com.app.androidtest.view.adapter.productsAdapter;
import com.app.androidtest.view.interfaces.StartDragListener;
import com.app.androidtest.viewModel.userOperationsViewModel;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import io.supercharge.shimmerlayout.ShimmerLayout;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

public class main extends Fragment implements StartDragListener {
    private ShimmerLayout imagesShimmer;
    private RecyclerView imagesRecycler;
    private ImageButton openCam,reArrange;
    private TextInputEditText advancedSearch;
    private ImageView noDataImage;
    private TextView noDataText;

    private View view;

    private productsAdapter adapter;

    private ItemTouchHelper.Callback callback;

    private ItemTouchHelper touchHelper;

    private userOperationsViewModel viewModel;

    private Cartdb dataSource;

    private static final int PICK_IMAGE = 1;

    private NotificationManager notifManager;

    private NotificationChannel mChannel;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.main,null,false);

        imagesShimmer = view.findViewById(R.id.imagesShimmer);
        imagesRecycler = view.findViewById(R.id.imagesRecycler);
        openCam = view.findViewById(R.id.openCam);
        reArrange = view.findViewById(R.id.reArrange);
        advancedSearch = view.findViewById(R.id.advancedSearch);
        noDataImage = view.findViewById(R.id.noDataImage);
        noDataText = view.findViewById(R.id.noDataText);

        openCam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check_forall_permissions();
            }
        });

        reArrange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (adapter!=null){
                    adapter.adjustSizes();
                }
            }
        });

        advancedSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String query = editable.toString();
                if (query.isEmpty()){
                    viewModel.getProducts(dataSource);
                }else {
                    viewModel.getProductsQuery(dataSource,query);
                }
            }
        });

        dataSource = Cartdb.getCartdb(getContext());

        viewModel = new ViewModelProvider(this).get(userOperationsViewModel.class);

        viewModel.getCategoriesRes.observe(getViewLifecycleOwner(),getCategoriesRes -> {
            if (getCategoriesRes.getErrorFlag()==0){
                getActivity().getSharedPreferences("userdata", MODE_PRIVATE).edit()
                        .putString("imageURL", getCategoriesRes.getResult().getImageURL())
                        .commit();
                AppSession.imageURL = getActivity().getSharedPreferences("userdata", MODE_PRIVATE).getString("imageURL","");
                viewModel.addProducts(dataSource,getCategoriesRes.getResult().getCategories().get(0).getProducts());
            }else {
                Snackbar.make(getView(),getCategoriesRes.getMessage(), BaseTransientBottomBar.LENGTH_LONG).show();
            }
        });

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
                    products=Stream.of(products).sortBy(product -> product.getId()).toList();
                }

                if (adapter==null){
                    adapter=new productsAdapter(getContext(),products,AppSession.imageURL,this);
                    callback = new ItemMoveCallback(adapter);
                    touchHelper = new ItemTouchHelper(callback);
                    touchHelper.attachToRecyclerView(imagesRecycler);
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

        viewModel.getProducts(dataSource);

        ScheduledExecutorService scheduler =
                Executors.newSingleThreadScheduledExecutor();

        scheduler.scheduleAtFixedRate
                (new Runnable() {
                    public void run() {
                        viewModel.getCategories();
                    }
                }, 0, 10, TimeUnit.MINUTES);

        return view;
    }

    @Override
    public void requestDrag(RecyclerView.ViewHolder viewHolder) {
        touchHelper.startDrag(viewHolder);
    }

    @Override
    public void afterDrag(Product product, Product product1) {
        /*product.setId(product1.getId());
        product1.setId(product.getId());
        viewModel.updateProducts(dataSource,product);
        viewModel.updateProducts(dataSource,product1);*/
    }

    @Override
    public void delete(Product product) {
        NavHostFragment.findNavController(main.this).navigate(mainDirections
                .actionFeedToDeleteImage(product).setProduct(product));
    }

    @Override
    public void share(Product product) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, product.getImage().contains("/storage")?
                product.getImage():product.getImage().contains("http") ? product.getImage() : AppSession.imageURL + "/" + product.getImage());
        startActivity(Intent.createChooser(intent, "Share"));
    }

    private void check_forall_permissions() {
        Dexter.withActivity(getActivity())
                .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            // do you work now
                            selectImage();

                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // permission is denied permenantly, navigate user to app settings
                            Toast.makeText(getContext(), getResources().getText(R.string.access_media_denied), Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();

                    }


                })
                .onSameThread()
                .check();
    }

    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && null != data) {
            String path = ImageFilePath.getPath(getContext(), data.getData());

            Product product = new Product();
            long l = ThreadLocalRandom.current().nextInt(100, 1000);
            product.setId(l);
            product.setCategory(getResources().getString(R.string.untitled)+" "+l);
            product.setTitle(getResources().getString(R.string.untitled)+" "+l);
            product.setImage(path);
            product.setDesc(getResources().getString(R.string.untitled)+" "+l);
            viewModel.addProduct(dataSource,product);
            viewModel.getProducts(dataSource);
            notification();
        }
    }

    private void notification() {


        NotificationCompat.Builder builder;
        if (notifManager == null) {
            notifManager = (NotificationManager) getActivity().getSystemService
                    (Context.NOTIFICATION_SERVICE);
        }

        //intent = new Intent (this, codes.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            if (mChannel == null) {
                NotificationChannel mChannel = new NotificationChannel
                        ("0",  getResources().getString(R.string.imageAdded), importance);
                mChannel.setDescription(getResources().getString(R.string.imageAdded));
                mChannel.enableVibration(true);
                mChannel.setVibrationPattern(new long[]
                        {100, 200, 300, 400, 500, 400, 300, 200, 400});
                notifManager.createNotificationChannel(mChannel);
            }
            builder = new NotificationCompat.Builder(getContext(), "0");

            builder.setSmallIcon(R.drawable.ic_round_photo_camera_24) // required
                    .setContentText(getResources().getString(R.string.imageAdded))// required
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setSound(RingtoneManager.getDefaultUri
                            (RingtoneManager.TYPE_NOTIFICATION))
                    .setVibrate(new long[]{100, 200, 300, 400,
                            500, 400, 300, 200, 400});
        } else {

            builder = new NotificationCompat.Builder(getContext());


            Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            builder.setSmallIcon(R.drawable.ic_round_photo_camera_24);
            //builder.setContentTitle();
            builder.setContentText(getResources().getString(R.string.imageAdded));
            builder.setColor((getResources().getColor(R.color.white)));
            builder.setSound(sound);
            builder.setVibrate(new long[]{100, 200, 300, 400,
                    500, 400, 300, 200, 400});


// notificationID allows you to update the notification later on.


        }
        Notification notification = builder.build();
        int id = (int) System.currentTimeMillis();
        notifManager.notify(id, notification);

    }

}
