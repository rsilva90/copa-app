package digital.meow.copaapp;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;

public class DisplayUtil {
	
	/** ????   */
	private static int DisplayWidthPixels = 0;
	/** ????   */
	private static int DisplayheightPixels = 0;

	/**
     * ??????
     * @param context
     */
    private static void getDisplayMetrics(Context context) {
		DisplayMetrics dm = new DisplayMetrics();
		((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
		DisplayWidthPixels = dm.widthPixels;// ??
		DisplayheightPixels = dm.heightPixels;// ??
	}

	/**
	 * ??????
	 * @param context
	 * @return
	 */
	public static int getDisplayWidthPixels(Context context) {
		if (context == null) {
			return -1;
		}
		if (DisplayWidthPixels == 0) {
			getDisplayMetrics(context);
		}
		return DisplayWidthPixels;
	}

	/**
	 * ??????
	 * @param context
	 * @return
	 */
	public static int getDisplayheightPixels(Context context) {
		if (context == null) {
			return -1;
		}
		if (DisplayheightPixels == 0) {
			getDisplayMetrics(context);
		}
		return DisplayheightPixels;
	}

	/**
     * ?px????dip?dp?
     *
     * @param pxValue
     * @return
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * ?dip?dp????px?
     *
     * @param dipValue
     * @return
     */
    public static int dip2px(Context context, float dipValue) {
        if(context == null){
            return 0;
        }
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * ?px????sp?
     *
     * @param pxValue
     * @return
     */
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * ?sp????px?
     * @param spValue
     * @param spValue   ?DisplayMetrics????scaledDensity?
     * @return 
     */  
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;  
        return (int) (spValue * fontScale + 0.5f);  
    }  
}
