package digital.meow.copaapp;

/**
 * Created by Romulo on 21/05/2015.
 */
public class Doodle {

    public String getmDesc() {
        return mDesc;
    }

    public void setmDesc(String mDesc) {
        this.mDesc = mDesc;
    }

    public int getResIcon() {
        return resIcon;
    }

    public void setResIcon(int resIcon) {
        this.resIcon = resIcon;
    }

    public int getResDoodle() {
        return resDoodle;
    }

    public void setResDoodle(int resDoodle) {
        this.resDoodle = resDoodle;
    }

    private String mDesc;
    private int resIcon;
    private int resDoodle;

    public Doodle(String desc, int icon, int doodle){
        mDesc = desc;
        resIcon = icon;
        resDoodle = doodle;
    }


}
