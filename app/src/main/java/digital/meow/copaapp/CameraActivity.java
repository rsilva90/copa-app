package digital.meow.copaapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.ParameterMetaData;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class CameraActivity extends Activity implements OnClickListener {

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    Button btnShoot;
    Button btnSwap;
    FrameLayout cameraPreview;
    ImageHelper imageHelper;

    private Camera mCamera;
    private CameraPreview mPreview;
    private int numberCameras = 0;
    private int currentCamera = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        imageHelper = new ImageHelper(this);

        btnShoot = (Button)findViewById(R.id.btnShoot);
        btnSwap = (Button)findViewById(R.id.btnSwapCamera);
        cameraPreview = (FrameLayout)findViewById(R.id.cameraPreview);

        updateCameraPreview(currentCamera);

        btnShoot.setOnClickListener(this);
        btnSwap.setOnClickListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseCamera();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btnShoot:
                mCamera.takePicture(null, null, imageHelper.mPictureCallback);
                goToEffects();
                break;
            case R.id.btnSwapCamera:
                if (numberCameras > 1) {
                    if (currentCamera > 0){
                        currentCamera = 0;
                    }else{
                        currentCamera = 1;
                    }
                }
                updateCameraPreview(currentCamera);
                break;
        }
    }

    private void updateCameraPreview(int currentCamera) {
        try {
            if (mCamera == null){
                mCamera = getCameraInstance();
            }
            else {
                mCamera.release();
                mCamera = Camera.open(currentCamera);
            }

            setCameraDisplayOrientation(this, currentCamera, mCamera);
            mPreview = new CameraPreview(this, mCamera);
            cameraPreview.removeAllViews();
            cameraPreview.addView(mPreview);
        }
        catch (Exception e){

        }
    }

    private void goToEffects(){
        Uri fileUri = Uri.fromFile(imageHelper.getFile());

        
    }

    public Camera getCameraInstance(){
        Camera c = null;
        numberCameras = Camera.getNumberOfCameras();
        try {
            if (numberCameras > 1){
                c = Camera.open(0);
                currentCamera = 0;
            }
            else {
                c = Camera.open(); // attempt to get a Camera instance
            }
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

    private void releaseCamera(){
        if (mCamera != null){
            mCamera.release();        // release the camera for other applications
            mCamera = null;
        }
    }

    public static void setCameraDisplayOrientation(Activity activity, int cameraId, android.hardware.Camera camera) {
        android.hardware.Camera.CameraInfo info =
                new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0: degrees = 0; break;
            case Surface.ROTATION_90: degrees = 90; break;
            case Surface.ROTATION_180: degrees = 180; break;
            case Surface.ROTATION_270: degrees = 270; break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }

//    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {
//
//        @Override
//        public void onPictureTaken(byte[] data, Camera camera) {
//
//            File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
//            if (pictureFile == null){
//                Log.d("Copa APP", "Error creating media file, check storage permissions: ");
//                return;
//            }
//
//            try {
//                FileOutputStream fos = new FileOutputStream(pictureFile);
//                fos.write(data);
//                fos.close();
//                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(pictureFile)));
//            } catch (FileNotFoundException e) {
//                Log.d("Copa APP", "File not found: " + e.getMessage());
//            } catch (IOException e) {
//                Log.d("Copa APP", "Error accessing file: " + e.getMessage());
//            }
//        }
//    };

//    public boolean isExternalStorageWritable() {
//        String state = Environment.getExternalStorageState();
//        if (Environment.MEDIA_MOUNTED.equals(state)) {
//            return true;
//        }
//        return false;
//    }
//
//    /* Checks if external storage is available to at least read */
//    public boolean isExternalStorageReadable() {
//        String state = Environment.getExternalStorageState();
//        if (Environment.MEDIA_MOUNTED.equals(state) ||
//                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
//            return true;
//        }
//        return false;
//    }
//
//    /** Create a file Uri for saving an image or video */
//    private Uri getOutputMediaFileUri(int type){
//        return Uri.fromFile(getOutputMediaFile(type));
//    }
//
//    /** Create a File for saving an image or video */
//    private File getOutputMediaFile(int type){
//        // To be safe, you should check that the SDCard is mounted
//        // using Environment.getExternalStorageState() before doing this.
//
////        String state = Environment.getExternalStorageState();
////        if (Environment.MEDIA_MOUNTED.equals(state)) {
////            Toast.makeText(this, "montado", Toast.LENGTH_SHORT).show();
////        }
//
//        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
//                Environment.DIRECTORY_PICTURES), "Copa APP");
//        // This location works best if you want the created images to be shared
//        // between applications and persist after your app has been uninstalled.
//
//        // Create the storage directory if it does not exist
//        if (!mediaStorageDir.exists()){
//            if(mediaStorageDir.mkdirs()){
//                //Toast.makeText(this, "criou", Toast.LENGTH_SHORT).show();
//            }else {
//                //Toast.makeText(this, "n criou :(", Toast.LENGTH_SHORT).show();
//            }
//        }
//
//        // Create a media file name
//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//        File mediaFile;
//        if (type == MEDIA_TYPE_IMAGE){
//            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
//                    "IMG_"+ timeStamp + ".jpg");
//        } else if(type == MEDIA_TYPE_VIDEO) {
//            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
//                    "VID_"+ timeStamp + ".mp4");
//        } else {
//            return null;
//        }
//
//        return mediaFile;
//    }

    public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
        private SurfaceHolder mHolder;
        private Camera mCamera;

        public CameraPreview(Context context, Camera camera) {
            super(context);
            mCamera = camera;

            // Install a SurfaceHolder.Callback so we get notified when the
            // underlying surface is created and destroyed.
            mHolder = getHolder();
            mHolder.addCallback(this);
            // deprecated setting, but required on Android versions prior to 3.0
            mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }

        public void surfaceCreated(SurfaceHolder holder) {
            // The Surface has been created, now tell the camera where to draw the preview.
            try {
                mCamera.setPreviewDisplay(holder);
                mCamera.startPreview();
            } catch (IOException e) {
                Log.d("w-e", "Error setting camera preview: " + e.getMessage());
            }
        }

        public void surfaceDestroyed(SurfaceHolder holder) {
            // empty. Take care of releasing the Camera preview in your activity.
        }

        public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
            // If your preview can change or rotate, take care of those events here.
            // Make sure to stop the preview before resizing or reformatting it.

            if (mHolder.getSurface() == null){
                // preview surface does not exist
                return;
            }

            // stop preview before making changes
            try {
                mCamera.stopPreview();
            } catch (Exception e){
                // ignore: tried to stop a non-existent preview
            }

            // set preview size and make any resize, rotate or
            // reformatting changes here

            // start preview with new settings
            try {
                mCamera.setPreviewDisplay(mHolder);
                mCamera.startPreview();

            } catch (Exception e){
                Log.d("w-e", "Error starting camera preview: " + e.getMessage());
            }
        }
    }
}

