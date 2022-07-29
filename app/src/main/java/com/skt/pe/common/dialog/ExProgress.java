package com.skt.pe.common.dialog;

import com.skt.pe.common.conf.Constants;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by heejung on 2017-05-31.
 */

 public class ExProgress extends Dialog {
    final static String TAG = "ExProgress";
    public static final String PROGRESS_LAYOUT_NAME = "common_progress";
    public static final String IMAGE_LOGO = "iv_logo";
    public static final String TEXT_CONTENT = "tv_content";
    
	
    ImageView iv_logo;
    TextView tv_content;

     public ExProgress(Context context){

        super(context);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(getResId(PROGRESS_LAYOUT_NAME, Constants.TYPE_LAYOUT));
        Log.e(TAG, "--------------------ex progress--------------------");
        iv_logo = (ImageView)findViewById(getResId(IMAGE_LOGO, Constants.TYPE_ID));
        tv_content = (TextView)findViewById(getResId(TEXT_CONTENT, Constants.TYPE_ID));

        Animation animation = new AlphaAnimation(1, 0);
        animation.setDuration(2000);
        animation.setInterpolator(new LinearInterpolator());
        animation.setRepeatCount(Animation.INFINITE);
        animation.setRepeatMode(Animation.REVERSE);

        tv_content.setAnimation(animation);
        iv_logo.startAnimation(animation);
       
    }

    private int getResId(String id, String type) {
 		return getContext().getResources().getIdentifier(id, type, getContext().getPackageName());
 	}
     
    public void setMessage(String msg){
    	tv_content.setText(msg);
    }
    public void setMessage(int msg){
    	tv_content.setText(msg);
    }
}
