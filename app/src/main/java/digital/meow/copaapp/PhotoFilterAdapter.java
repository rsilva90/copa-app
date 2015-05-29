package digital.meow.copaapp;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsoluteLayout;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import butterknife.ButterKnife;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * Created by Romulo on 13/05/2015.
 */
public class PhotoFilterAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {

    private Context context;
    private int itemsCount = 0;
//    private ArrayList<String> list;
    private ArrayList<Doodle> list;
    private int counter;
    private Activity activity;

    public PhotoFilterAdapter(Context context, Activity activity) {
        this.list = DoodleList.headDoodle;
        this.itemsCount = list.size();
        this.context = context;
        counter = 0;
        this.activity=activity;
    }

//    public PhotoFilterAdapter(Context context, ArrayList<String> list) {
//        this.list = list;
//        this.itemsCount = list.size();
//        this.counter = 0;
//        this.context = context;
//    }

    public PhotoFilterAdapter(Context context, ArrayList<Doodle> list, Activity activity) {
        this.list = list;
        this.itemsCount = list.size();
        this.counter = 0;
        this.context = context;
        this.activity=activity;
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

                TouchImageView iv = new TouchImageView(context);
                iv.setLayoutParams(new AbsoluteLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT,0,0));
                iv.setImage(res);

                fl.addView(iv);

                //Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
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
}
