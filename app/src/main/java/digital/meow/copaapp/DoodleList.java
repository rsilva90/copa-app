package digital.meow.copaapp;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by Romulo on 20/05/2015.
 */
public class DoodleList {

    // atualizar essa lista quando tiver os negocio de vdd
    public static ArrayList<Doodle> headDoodle = new ArrayList<Doodle>(){{
        add(new Doodle("Biro Biro", R.drawable.img_ico_biro, R.drawable.img_ico_biro));
        add(new Doodle("Valderrama", R.drawable.img_ico_valde, R.drawable.img_ico_valde));
        add(new Doodle("Cartola", R.drawable.img_ico_tophat, R.drawable.img_ico_tophat));
    }};

    public static ArrayList<Doodle> faceDoodle = new ArrayList<Doodle>(){{
        add(new Doodle("Bigode", R.drawable.img_ico_biro, R.drawable.img_ico_biro));
        add(new Doodle("Tapa-olho", R.drawable.img_ico_valde, R.drawable.img_ico_valde));
        add(new Doodle("Ronaldo", R.drawable.img_ico_tophat, R.drawable.img_ico_tophat));
        add(new Doodle("Monóculo", R.drawable.img_ico_tophat, R.drawable.img_ico_tophat));
    }};

    public static ArrayList<Doodle> otherDoodle = new ArrayList<Doodle>(){{
        add(new Doodle("Bola", R.drawable.img_ico_biro, R.drawable.img_ico_biro));
        add(new Doodle("Vuvuzela", R.drawable.img_ico_valde, R.drawable.img_ico_valde));
        add(new Doodle("Chuteira", R.drawable.img_ico_tophat, R.drawable.img_ico_tophat));
    }};

    public static ArrayList<String> headDoodles = new ArrayList<String>(){{
        add("Biro Biro");
        add("Valderrama");
        add("Cartola");
    }};


    public static ArrayList<String> faceDoodles = new ArrayList<String>(){{
        add("Bigode");
        add("Tapa-olho");
        add("Ronaldo");
        add("Monóculo");
    }};


    public static ArrayList<String> otherDoodles = new ArrayList<String>(){{
        add("Bola");
        add("Vuvuzela");
        add("Chuteira");
    }};

}
