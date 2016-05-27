package au.com.appscore.emcast;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import HttpConnection.ConnectionDetector;
import HttpConnection.PostFeedback;
import ModelClasses.Podcast_list;
import custommediacontroller.CustomMediaController;

public class Mediaplayingmp4 extends AppCompatActivity implements MediaPlayer.OnCompletionListener{
    public static Podcast_list podcastList;
    private boolean onFullscreenclose = false;
    public VideoView videoView;
    private TextView category, toolbarText, title;
    private TextView description;
    private ProgressBar progressBar;
    private Dialog dialog;
    private String rating;
    public static String url;
    private  int onpausePlayPostion = 0;
    private ImageView temp, feedback, btn_close;
    private static ImageView image_ids[] = new ImageView[10];
    private CustomMediaController customMediaController;
    private boolean img3active,videocompleted=false;
    private  int MediaDuration;
    private ConnectionDetector cd=null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mediaplayingmp4);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.btn_back);
        Intent intent = getIntent();
        cd = new ConnectionDetector(this);
        podcastList = (Podcast_list) intent.getSerializableExtra("podcastObject");
        url = podcastList.getVideo_url();

        category = (TextView) findViewById(R.id.category);
        description = (TextView) findViewById(R.id.description);
        toolbarText = (TextView) findViewById(R.id.toolbar_title);
        progressBar= (ProgressBar) findViewById(R.id.progressbar);
        title = (TextView) findViewById(R.id.titiletext);

        Log.d("oncreate", "create");

        customMediaController = new CustomMediaController(this);

        if(cd.isConnectingToInternet())
            playvideoifInternetExists();
        category.setText(podcastList.getPodcast_category());
        description.setText(podcastList.getPodcast_description());
        toolbarText.setText(podcastList.getPodcast_title());
        title.setText(podcastList.getPodcast_title());
    }

    public void playvideoifInternetExists(){
        videoView = (VideoView) findViewById(R.id.videoView);
        customMediaController.setAnchorView(videoView);
        customMediaController.changeButtonBejavior(false);
        videoView.setMediaController(customMediaController);
        videoView.setOnCompletionListener(this);
        if (!url.isEmpty()) {
            videoView.setVideoURI(Uri.parse(url));
            progressBar.setVisibility(View.VISIBLE);
            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    videoView.start();

                    mp.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
                        @Override
                        public void onVideoSizeChanged(MediaPlayer mp, int arg1, int arg2) {
                            // TODO Auto-generated method stub
                            progressBar.setVisibility(View.GONE);

                        }
                    });
                }
            });
        }
        else
            Toast.makeText(this, "there is no video available for this category", Toast.LENGTH_LONG).show();
    }
    public int getvideoPosition(){
        return videoView.getCurrentPosition();
    }
    @Override
    protected void onStart() {
        super.onStart();
        //if app starts from background
        if(!onFullscreenclose&&cd.isConnectingToInternet()) {
            progressBar.setVisibility(View.VISIBLE);
            videoView.requestFocus();
            videoView.seekTo(onpausePlayPostion);
            videoView.start();
            videocompleted=false;
            Log.d("start", "strat");
        }
        else {
           if (cd.isConnectingToInternet()){
                videoView.seekTo(onpausePlayPostion);
            }
        }
        }
    @Override
    protected void onResume() {
        super.onResume();
        if(cd.isConnectingToInternet()) {
            videoView.start();
            Log.d("resume", "resume");
        }
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkReceiver, filter);
    }
    @Override
    protected void onPause() {
        super.onPause();
        Log.d("onpause", "pause");
        if(videoView!=null&& videoView.isPlaying())
        onpausePlayPostion=getvideoPosition();
        unregisterReceiver(networkReceiver);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("stop", "stop");
        if(cd.isConnectingToInternet()) {
            videoView.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("video", "destroy");
        videoView=null;
        customMediaController=null;
    }

    void startDialog() {
        dialog = new Dialog(this);
        img3active=true;
        // preparing dialog view
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.feedback);
        image_ids[0] = (ImageView) dialog.findViewById(R.id.one_inactive);
        image_ids[1] = (ImageView) dialog.findViewById(R.id.two_inactive);
        image_ids[2] = (ImageView) dialog.findViewById(R.id.three_inactive);
        image_ids[3] = (ImageView) dialog.findViewById(R.id.four_inactive);
        image_ids[4] = (ImageView) dialog.findViewById(R.id.five_inactive);

        image_ids[5] = (ImageView) dialog.findViewById(R.id.one_active);
        image_ids[6] = (ImageView) dialog.findViewById(R.id.two_active);
        image_ids[7] = (ImageView) dialog.findViewById(R.id.three_active);
        image_ids[8] = (ImageView) dialog.findViewById(R.id.four_active);
        image_ids[9] = (ImageView) dialog.findViewById(R.id.five_active);

        feedback = (ImageView) dialog.findViewById(R.id.feedback);
        btn_close = (ImageView) dialog.findViewById(R.id.btn_close);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable
                (android.graphics.Color.TRANSPARENT));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(true);
        dialog.show();
        temp = new ImageView(getApplicationContext());
        for (int i = 0; i < image_ids.length; i++) {
            image_ids[i].setOnClickListener(new imageclick());
        }

        feedback.setOnClickListener(new feedbackClick());
        btn_close.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                videoView.pause();
                dialog.dismiss();
            }
        });
    }
    void alertbuilderDialog(){
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            final View dialogView;
            LayoutInflater inflater = this.getLayoutInflater();
            if(cd.isConnectingToInternet()){
                dialogView = inflater.inflate(R.layout.alertsuccess, null);
                builder.setView(dialogView);
                TextView mainpage= (TextView) dialogView.findViewById(R.id.mainpage);
                mainpage.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getBaseContext(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                });
                builder.show();
            }
            else{
                dialogView = inflater.inflate(R.layout.alertfailure, null);
                builder.setView(dialogView);
                TextView cancel= (TextView) dialogView.findViewById(R.id.cancel);
                final AlertDialog ad= builder.show();
                cancel.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                       ad.cancel();
                    }
                });

            }

        }

    @Override
    public void onCompletion(MediaPlayer mp) {
        Log.d("oncompletion", "completed");

        if(cd.isConnectingToInternet())
        startDialog();
        videocompleted=true;
    }

    class feedbackClick implements OnClickListener {@Override public void onClick(View v) {
            if (rating != null)
                podcastList.setRating(rating);
                else
                podcastList.setRating("3");
                PostFeedback postFeedback = new PostFeedback(getApplicationContext());
                postFeedback.execute(podcastList);
                alertbuilderDialog();

        }}

    class imageclick implements OnClickListener {@Override public void onClick(View v) {
            if(img3active){
                img3active=false;
                dialog.findViewById(R.id.three_inactive).setVisibility(View.VISIBLE);
                dialog.findViewById(R.id.three_active).setVisibility(View.INVISIBLE);
            }
            ImageView iv = (ImageView) dialog.findViewById(v.getId());
            int x, length;
            String tag = String.valueOf(iv.getTag());
            length = tag.length();
            rating = tag.substring(tag.length() - 1);
            if (temp != null) {
                temp.setVisibility(View.INVISIBLE);
            }
            x = Integer.parseInt(tag.substring(length - 1, length));
            temp = image_ids[x + 4];
            temp.setVisibility(View.VISIBLE);

        }}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // check if the request code is same as what is passed  here it is 2
        if (requestCode == 2) {
            if(resultCode==2) {
                onFullscreenclose = true;
                videoView.pause();
                startDialog();
            }
            else
            {
                //activity gets started again and on pause play position wll let
                // the video to play from where it left on fullscreen activity
                onFullscreenclose=true;
                onpausePlayPostion=data.getIntExtra("onpausePlay",0);

            }
        }
    }
    private BroadcastReceiver networkReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getExtras() != null) {
                NetworkInfo ni = (NetworkInfo) intent.getExtras().get(ConnectivityManager.EXTRA_NETWORK_INFO);
                if (ni != null && ni.getState() == NetworkInfo.State.CONNECTED) {
                    // we're connected
                    if (!customMediaController.isEnabled()&&!videocompleted) {
                        videoView.start();
                        customMediaController.setEnabled(true);
                        videocompleted=false;
                    }
                    else
                    {
                        playvideoifInternetExists();
                        onStart();
                    }
                }
            // we're not connected
            else {
                    Toast toast = Toast.makeText(context, "No Internet Connection", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();

                    if(videoView!=null&&videoView.isPlaying()) {
                        Log.d("offinternet","broadcastlistner");
                        videoView.pause();
                        customMediaController.setEnabled(false);
                    }
                 }
            }
        }
    };

}
