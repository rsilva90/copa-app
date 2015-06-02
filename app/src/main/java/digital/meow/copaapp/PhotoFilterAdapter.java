package digital.meow.copaapp;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.InputDevice;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsoluteLayout;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

import java.util.ArrayList;

import butterknife.ButterKnife;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * Created by Romulo on 13/05/2015.
 */
public class PhotoFilterAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private int itemsCount = 0;
//    private ArrayList<String> list;
    private ArrayList<Doodle> list;
    private int counter;
    private Activity activity;

    private float mPosX;
    private float mPosY;

    private float mLastTouchX;
    private float mLastTouchY;

    private static final int INVALID_POINTER_ID = -1;
    private int mActivePointerId = INVALID_POINTER_ID;

    private ScaleGestureDetector mScaleDetector;
    private float mScaleFactor = 1.f;

    public PhotoFilterAdapter(Context context, Activity activity) {
        this.list = DoodleList.headDoodle;
        this.itemsCount = list.size();
        this.context = context;
        counter = 0;
        this.activity=activity;
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
    }

    public PhotoFilterAdapter(Context context, ArrayList<Doodle> list, Activity activity) {
        this.list = list;
        this.itemsCount = list.size();
        this.counter = 0;
        this.context = context;
        this.activity=activity;
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
    }

    // aqui vai carregar os efeitinhos
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(context).inflate(R.layout.item_photo_filter, parent, false);

        Doodle d = list.get(counter);
        ImageView img = (ImageView)view.findViewById(R.id.ivDoodle);
        img.setImageResource(d.getResIcon());
        view.setTag(d.getResDoodle());
        counter++;

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = v.getTag().toString();
                int res = Integer.parseInt(text);

                AbsoluteLayout fl = (AbsoluteLayout) activity.findViewById(R.id.flDoodles);

                int i = fl.getChildCount();

                StickerView iv = new StickerView(context);
                iv.setLayoutParams(new AbsoluteLayout.LayoutParams(AbsoluteLayout.LayoutParams.WRAP_CONTENT, AbsoluteLayout.LayoutParams.WRAP_CONTENT, 0,0));
//                iv.setBackgroundResource(res);
                iv.setWaterMark(BitmapFactory.decodeResource(context.getResources(), res));
                iv.setOnStickerDeleteListener(new StickerView.OnStickerDeleteListener() {
                    @Override
                    public void onDelete(View v) {
                        AbsoluteLayout fl = (AbsoluteLayout) activity.findViewById(R.id.flDoodles);
                        fl.removeView(v);
                    }
                });

//                SubsamplingScaleImageView iv = new SubsamplingScaleImageView(context);
//                iv.setImage(ImageSource.resource(res));

                        fl.addView(iv);
            }
        });
        return new PhotoFilterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
    }

    @Override
    public int getItemCount() {
        return itemsCount;
    }

    public static class PhotoFilterViewHolder extends RecyclerView.ViewHolder {

        public PhotoFilterViewHolder(View view) {
            super(view);
            ButterKnife.inject(this, view);
        }
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {


        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            mScaleFactor *= detector.getScaleFactor();
            // Don't let the object get too small or too large.
            mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 5.0f));


            //invalidate();
            return true;
        }
    }
}
