package digital.meow.copaapp;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.ButterKnife;

/**
 * Created by Romulo on 13/05/2015.
 */
public class PhotoFilterAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {

    private Context context;
    private int itemsCount = 0;
//    private ArrayList<String> list;
    private ArrayList<Doodle> list;
    private int counter;

    public PhotoFilterAdapter(Context context) {
        this.list = DoodleList.headDoodle;
        this.itemsCount = list.size();
        this.context = context;
        counter = 0;
    }

//    public PhotoFilterAdapter(Context context, ArrayList<String> list) {
//        this.list = list;
//        this.itemsCount = list.size();
//        this.counter = 0;
//        this.context = context;
//    }

    public PhotoFilterAdapter(Context context, ArrayList<Doodle> list) {
        this.list = list;
        this.itemsCount = list.size();
        this.counter = 0;
        this.context = context;
    }


    // aqui vai carregar os efeitinhos
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(context).inflate(R.layout.item_photo_filter, parent, false);

        Doodle d = list.get(counter);
        ImageView img = (ImageView)view.findViewById(R.id.ivDoodle);
        img.setImageResource(d.getResIcon());
        view.setTag(d.getmDesc());
        counter++;

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = v.getTag().toString();
                Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
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
