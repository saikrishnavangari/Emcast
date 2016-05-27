package au.com.appscore.emcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.VideoView;

import HttpConnection.ConnectionDetector;
import ModelClasses.Podcast_list;
import custommediacontroller.CustomMediaController;

/**
 * Created by krrish on 1/04/2016.
 */
public class FullscreenActivity extends AppCompatActivity {
    public Podcast_list podcastList;
    private ProgressBar progressBar;
    private  Intent intent;
    private String url;
    private VideoView videoView;
    private int onpausePlayPosition;
    private  ConnectionDetector cd =null;
    private CustomMediaController customMediaController;
    private Boolean videocompleted=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.fullscreen);
        intent=getIntent();
        Log.d("oncreate","create");
        podcastList = (Podcast_list) intent.getSerializableExtra("podcastObject");
        onpausePlayPosition=intent.getIntExtra("onpausePlay", 0);
        url=podcastList.getVideo_url();
        videoView = (VideoView) findViewById(R.id.fullscreen_videoview);
        progressBar= (ProgressBar) findViewById(R.id.progressbar);
        customMediaController= new CustomMediaController(this);
        customMediaController.changeButtonBejavior(true);
        customMediaController.setAnchorView(videoView);
        videoView.setMediaController(customMediaController);
        customMediaController.fullScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        cd = new ConnectionDetector(this);


    }
    @Override
    protected void onStart() {
        super.onStart();

        if(cd.isConnectingToInternet()) {
            if(url!=null){
                videoView.setVideoURI(Uri.parse(url));
                videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    public void onCompletion(MediaPlayer mp) {
                        Log.d("videoCompletion","completed");
                       if(cd.isConnectingToInternet()) {
                           Intent intent = new Intent();
                           intent.putExtra("MESSAGE", "done");
                           setResult(2, intent);
                           finish();//finishing activity
                       }
                        else {
                           Log.d("videoCompletion", "elsepart");
                           videocompleted = true;
                       }
                    }
                });
            }
            Log.d("onstart","start");
            videoView.requestFocus();
            progressBar.setVisibility(View.VISIBLE);
            videoView.seekTo(onpausePlayPosition);
            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
                        @Override
                        public void onVideoSizeChanged(MediaPlayer mp, int arg1, int arg2) {
                            // TODO Auto-generated method stub
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                }
            });
            videoView.start();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkReceiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(networkReceiver);
    }
    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onStop() {
        super.onStop();
        videoView.pause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
    public int getvideoPosition(){
        return videoView.getCurrentPosition();
    }
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }
    private BroadcastReceiver networkReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getExtras() != null) {
                NetworkInfo ni = (NetworkInfo) intent.getExtras().get(ConnectivityManager.EXTRA_NETWORK_INFO);
                if (ni != null && ni.getState() == NetworkInfo.State.CONNECTED) {
                    // we're connected

                        Log.d("broadcast","insideif");
                        onStart();
                        if(!customMediaController.isEnabled())
                            customMediaController.setEnabled(true);
                }
                    // we're not connected
                else {
                    Toast toast = Toast.makeText(context, "No Internet Connection", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    if(videoView.isPlaying()) {
                        videoView.pause();
                        customMediaController.setEnabled(false);
                    }
                }
            }
        }

    };
}
