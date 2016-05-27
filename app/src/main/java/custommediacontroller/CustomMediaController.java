package custommediacontroller;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.MediaController;

import au.com.appscore.emcast.FullscreenActivity;
import au.com.appscore.emcast.Mediaplayingmp4;
import au.com.appscore.emcast.R;

/**
 * Created by krrish on 1/04/2016.
 */
public class CustomMediaController extends MediaController {
    private Context context;
    private boolean VedioFull=false;
    private int onpausePlayPostion;
    public static ImageView fullScreen=null;
    public CustomMediaController(Context context) {
        super(context);
        this.context=context;
    }

    public void changeButtonBejavior(boolean isForSmal){
        VedioFull = isForSmal;
    }
    @Override
    public void setAnchorView(View view) {
        super.setAnchorView(view);
        fullScreen = new ImageView(context);
        fullScreen.setImageResource(R.drawable.fullscreen_button);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.gravity =  Gravity.RIGHT;
        params.setMargins(0,30,15,0);
        addView(fullScreen, params);
        fullScreen.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if(!VedioFull) {
                    onpausePlayPostion=((Mediaplayingmp4) v.getContext()).getvideoPosition();
                    Intent intent = new Intent(v.getContext(), FullscreenActivity.class);
                    intent.putExtra("podcastObject", Mediaplayingmp4.podcastList);
                    intent.putExtra("onpausePlay", onpausePlayPostion);
                    Log.d("position", String.valueOf(onpausePlayPostion));
                    ((Mediaplayingmp4) v.getContext()).startActivityForResult(intent, 2);

                }
                else
                {
                    onpausePlayPostion=((FullscreenActivity) v.getContext()).getvideoPosition();
                    VedioFull=false;
                    Intent intent=new Intent();
                    intent.putExtra("onpausePlay", onpausePlayPostion);
                    ((FullscreenActivity) v.getContext()).setResult(3,intent);
                    ((FullscreenActivity) v.getContext()). finish();

                }
            }
        });
    }
}
