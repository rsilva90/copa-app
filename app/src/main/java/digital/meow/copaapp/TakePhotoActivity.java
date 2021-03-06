package digital.meow.copaapp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.AbsoluteLayout;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.commonsware.cwac.camera.CameraHostProvider;
import com.commonsware.cwac.camera.CameraView;
import com.commonsware.cwac.camera.PictureTransaction;
import com.commonsware.cwac.camera.SimpleCameraHost;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;


public class TakePhotoActivity extends ActionBarActivity implements RevealBackgroundView.OnStateChangeListener,
        CameraHostProvider, View.OnClickListener {

    public static final String ARG_REVEAL_START_LOCATION = "reveal_start_location";

    private static final Interpolator ACCELERATE_INTERPOLATOR = new AccelerateInterpolator();
    private static final Interpolator DECELERATE_INTERPOLATOR = new DecelerateInterpolator();
    private static final int STATE_TAKE_PHOTO = 0;
    private static final int STATE_SETUP_PHOTO = 1;

    RevealBackgroundView vRevealBackground;
    View vTakePhotoRoot;
    View vShutter;
    SubsamplingScaleImageView ivTakenPhoto;
    ViewSwitcher vUpperPanel;
    ViewSwitcher vLowerPanel;
    CameraView cameraView;
    RecyclerView rvFilters;
    AbsoluteLayout flBorders;
    AbsoluteLayout flDoodles;
    Button btnTakePhoto;
    ImageButton headDoodles;
    ImageButton faceDoodles;
    ImageButton btnAccept;
    ImageButton otherDoodles;
    SquaredFrameLayout square;

    private boolean pendingIntro;
    private int currentState;

    private File photoPath;

    public static void startCameraFromLocation(int[] startingLocation, Activity startingActivity) {
        Intent intent = new Intent(startingActivity, TakePhotoActivity.class);
        intent.putExtra(ARG_REVEAL_START_LOCATION, startingLocation);
        startingActivity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_take_photo);

        vRevealBackground = (RevealBackgroundView)findViewById(R.id.vRevealBackground);
        btnTakePhoto = (Button)findViewById(R.id.btnTakePhoto);
        rvFilters = (RecyclerView)findViewById(R.id.rvFilters);
        flDoodles = (AbsoluteLayout)findViewById(R.id.flDoodles);
        flBorders = (AbsoluteLayout)findViewById(R.id.flBorders);
        cameraView = (CameraView)findViewById(R.id.cameraView);
        vLowerPanel = (ViewSwitcher)findViewById(R.id.vLowerPanel);
        vUpperPanel = (ViewSwitcher)findViewById(R.id.vUpperPanel);
        ivTakenPhoto = (SubsamplingScaleImageView)findViewById(R.id.ivTakenPhoto);
        vShutter = (View)findViewById(R.id.vShutter);
        square = (SquaredFrameLayout)findViewById(R.id.vPhotoRoot);
        vTakePhotoRoot = (View)findViewById(R.id.vPhotoRoot);
        headDoodles = (ImageButton)findViewById(R.id.headDoodles);
        faceDoodles = (ImageButton)findViewById(R.id.faceDoodles);
        otherDoodles = (ImageButton)findViewById(R.id.otherDoodles);
        btnAccept = (ImageButton)findViewById(R.id.btnAccept);

        btnTakePhoto.setOnClickListener(this);
        headDoodles.setOnClickListener(this);
        faceDoodles.setOnClickListener(this);
        otherDoodles.setOnClickListener(this);
        btnAccept.setOnClickListener(this);

        updateStatusBarColor();
        updateState(STATE_TAKE_PHOTO);
        setupRevealBackground(savedInstanceState);
        setupPhotoFilters();

        vUpperPanel.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                vUpperPanel.getViewTreeObserver().removeOnPreDrawListener(this);
                pendingIntro = true;
                vUpperPanel.setTranslationY(-vUpperPanel.getHeight());
                vLowerPanel.setTranslationY(vLowerPanel.getHeight());
                return true;
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void updateStatusBarColor() {
        if (Utils.isAndroid5()) {
            getWindow().setStatusBarColor(0xff111111);
        }
    }

    private void setupRevealBackground(Bundle savedInstanceState) {
        vRevealBackground.setFillPaintColor(0xFF16181a);
        vRevealBackground.setOnStateChangeListener(this);
        if (savedInstanceState == null) {
            final int[] startingLocation = getIntent().getIntArrayExtra(ARG_REVEAL_START_LOCATION);
            vRevealBackground.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    vRevealBackground.getViewTreeObserver().removeOnPreDrawListener(this);
                    vRevealBackground.startFromLocation(startingLocation);
                    return true;
                }
            });
        } else {
            vRevealBackground.setToFinishedFrame();
        }
    }

    private void setupPhotoFilters() {
        PhotoFilterAdapter photoFiltersAdapter = new PhotoFilterAdapter(this, this);
        rvFilters.setHasFixedSize(true);
        rvFilters.setAdapter(photoFiltersAdapter);
        rvFilters.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
    }

    private void setupPhotoFilters(ArrayList<Doodle> list) {
        PhotoFilterAdapter photoFiltersAdapter = new PhotoFilterAdapter(this, list, this);
        rvFilters.setHasFixedSize(true);
        rvFilters.setAdapter(photoFiltersAdapter);
        rvFilters.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
    }


    @Override
    protected void onResume() {
        super.onResume();
        cameraView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraView.onPause();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btnTakePhoto:
                btnTakePhoto.setEnabled(false);
                cameraView.takePicture(true, true);
                animateShutter();
                break;
            case R.id.btnAccept:
                SaveFileToPublish();
                break;
            case R.id.headDoodles:
//                Toast.makeText(this, "Cabeça", Toast.LENGTH_SHORT).show();
                setupPhotoFilters(DoodleList.headDoodle);
                updateState(STATE_SETUP_PHOTO);
                break;
            case R.id.faceDoodles:
//                Toast.makeText(this, "Rosto", Toast.LENGTH_SHORT).show();
                setupPhotoFilters(DoodleList.faceDoodle);
                updateState(STATE_SETUP_PHOTO);
                break;
            case R.id.otherDoodles:
//                Toast.makeText(this, "Outros", Toast.LENGTH_SHORT).show();
                setupPhotoFilters(DoodleList.otherDoodle);
                updateState(STATE_SETUP_PHOTO);
                break;
        }
    }

    private void SaveFileToPublish(){
        Bitmap bmp = screenShot(square);
        File mFile = ImageHelper.getOutputMediaFile(ImageHelper.MEDIA_TYPE_IMAGE);
        if (mFile == null){
            Log.d("Copa APP", "Error creating media file, check storage permissions: ");
            return;
        }

        try {
            FileOutputStream fos = new FileOutputStream(mFile);

            //b is the Bitmap
            // calculate how many bytes our image consists of.
            int bytes = bmp.getByteCount();
            //or we can calculate bytes this way. Use a different value than 4 if you don't use 32bit images.
            // int bytes = b.getWidth()*b.getHeight()*4;

            ByteBuffer buffer = ByteBuffer.allocate(bytes); //Create a new buffer
            bmp.copyPixelsToBuffer(buffer); //Move the byte data to the buffer

            byte[] array = buffer.array(); //Get the underlying array containing the data.

            fos.write(array);
            fos.close();
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(mFile)));
        } catch (FileNotFoundException e) {
            Log.d("Copa APP", "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d("Copa APP", "Error accessing file: " + e.getMessage());
        }
        Toast.makeText(this, "Cheque a galeria", Toast.LENGTH_SHORT).show();

        //PublishActivity.openWithPhotoUri(this, Uri.fromFile(photoPath));
    }

    public Bitmap screenShot(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(),
                view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        //view.draw(canvas);
        ivTakenPhoto.draw(canvas);
        return bitmap;
    }

    private void animateShutter() {
        vShutter.setVisibility(View.VISIBLE);
        vShutter.setAlpha(0.f);

        ObjectAnimator alphaInAnim = ObjectAnimator.ofFloat(vShutter, "alpha", 0f, 0.8f);
        alphaInAnim.setDuration(100);
        alphaInAnim.setStartDelay(100);
        alphaInAnim.setInterpolator(ACCELERATE_INTERPOLATOR);

        ObjectAnimator alphaOutAnim = ObjectAnimator.ofFloat(vShutter, "alpha", 0.8f, 0f);
        alphaOutAnim.setDuration(200);
        alphaOutAnim.setInterpolator(DECELERATE_INTERPOLATOR);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playSequentially(alphaInAnim, alphaOutAnim);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                vShutter.setVisibility(View.GONE);
            }
        });
        animatorSet.start();
    }

    @Override
    public void onStateChange(int state) {
        if (RevealBackgroundView.STATE_FINISHED == state) {
            vTakePhotoRoot.setVisibility(View.VISIBLE);
            if (pendingIntro) {
                startIntroAnimation();
            }
        } else {
            vTakePhotoRoot.setVisibility(View.INVISIBLE);
        }
    }

    private void startIntroAnimation() {
        vUpperPanel.animate().translationY(0).setDuration(400).setInterpolator(DECELERATE_INTERPOLATOR);
        vLowerPanel.animate().translationY(0).setDuration(400).setInterpolator(DECELERATE_INTERPOLATOR).start();
    }

    @Override
    public com.commonsware.cwac.camera.CameraHost getCameraHost() {
        return new CameraHost(this);
    }

    class CameraHost extends SimpleCameraHost {

        private Camera.Size previewSize;

        public CameraHost(Context ctxt) {
            super(ctxt);
        }

        @Override
        public boolean useFullBleedPreview() {
            return true;
        }

        @Override
        public Camera.Size getPictureSize(PictureTransaction xact, Camera.Parameters parameters) {
            return previewSize;
        }

        @Override
        public Camera.Parameters adjustPreviewParameters(Camera.Parameters parameters) {
            Camera.Parameters parameters1 = super.adjustPreviewParameters(parameters);
            previewSize = parameters1.getPreviewSize();
            return parameters1;
        }

        @Override
        public void saveImage(PictureTransaction xact, final Bitmap bitmap) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showTakenPicture(bitmap);
                }
            });
        }

        @Override
        public void saveImage(PictureTransaction xact, byte[] image) {
            super.saveImage(xact, image);
            photoPath = getPhotoPath();
            Context x = getBaseContext();
            x.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(photoPath)));
        }
    }

    private void showTakenPicture(Bitmap bitmap) {
        vUpperPanel.showNext();
        vLowerPanel.showNext();
        ivTakenPhoto.setImage(ImageSource.bitmap(bitmap));// setImageBitmap(bitmap);
        updateState(STATE_SETUP_PHOTO);
    }

    @Override
    public void onBackPressed() {
        if (currentState == STATE_SETUP_PHOTO) {
            btnTakePhoto.setEnabled(true);
            vUpperPanel.showNext();
            vLowerPanel.showNext();
            flDoodles.removeAllViews();
            updateState(STATE_TAKE_PHOTO);
        } else {
            super.onBackPressed();
        }
    }

    private void updateState(int state) {
        currentState = state;
        if (currentState == STATE_TAKE_PHOTO) {
            vUpperPanel.setInAnimation(this, R.anim.slide_in_from_right);
            vLowerPanel.setInAnimation(this, R.anim.slide_in_from_right);
            vUpperPanel.setOutAnimation(this, R.anim.slide_out_to_left);
            vLowerPanel.setOutAnimation(this, R.anim.slide_out_to_left);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    ivTakenPhoto.setVisibility(View.GONE);
                }
            }, 400);
        } else if (currentState == STATE_SETUP_PHOTO) {
            vUpperPanel.setInAnimation(this, R.anim.slide_in_from_left);
            vLowerPanel.setInAnimation(this, R.anim.slide_in_from_left);
            vUpperPanel.setOutAnimation(this, R.anim.slide_out_to_right);
            vLowerPanel.setOutAnimation(this, R.anim.slide_out_to_right);
            ivTakenPhoto.setVisibility(View.VISIBLE);
        }
    }
}
