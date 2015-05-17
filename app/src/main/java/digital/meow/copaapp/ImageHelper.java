package digital.meow.copaapp;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Romulo on 10/05/2015.
 */
public class ImageHelper {

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    static Context mContext;
    public static File mFile;

    public ImageHelper(Context context){
        mContext = context;
    }

    public File getFile(){
        return mFile;
    }

    public static Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

            mFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
            if (mFile == null){
                Log.d("Copa APP", "Error creating media file, check storage permissions: ");
                return;
            }

            try {
                FileOutputStream fos = new FileOutputStream(mFile);
                fos.write(data);
                fos.close();
                mContext.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(mFile)));
            } catch (FileNotFoundException e) {
                Log.d("Copa APP", "File not found: " + e.getMessage());
            } catch (IOException e) {
                Log.d("Copa APP", "Error accessing file: " + e.getMessage());
            }
        }
    };

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    private static Uri getOutputMediaFileUri(int type, String folderName){
        return Uri.fromFile(getOutputMediaFile(type));
    }

    private static File getOutputMediaFile(int type){
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "Copa APP");

        if (!mediaStorageDir.exists()){
            if(mediaStorageDir.mkdirs()){
                //Toast.makeText(this, "criou", Toast.LENGTH_SHORT).show();
            }else {
                //Toast.makeText(this, "n criou :(", Toast.LENGTH_SHORT).show();
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + ".jpg");
        } else if(type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_"+ timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }

}
