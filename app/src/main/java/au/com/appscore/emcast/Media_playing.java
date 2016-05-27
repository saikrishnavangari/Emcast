package au.com.appscore.emcast;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import HttpConnection.ConnectionDetector;
import HttpConnection.PostFeedback;
import ModelClasses.Podcast_list;

public class Media_playing extends AppCompatActivity  {
    private boolean stopmediaplayer = false;
    private Podcast_list podcastList;
    private String mediaUri, categor, rating;
    private MediaPlayer mediaPlayer;
    private TextView category, description, title, toolbarTitle;
    private ImageView play, back, forward, pause;
    private int forwardTime = 1000, backwardTime = 1000, startTime;
    private Dialog dialog;
    private Handler myHandler = new Handler();
    private ProgressBar progressBar, progressBarHorz;
    private ImageView temp, feedback, image_cllicked;
    private ImageView image_ids[] = new ImageView[10];
    private ConnectionDetector cd;
    private boolean cangoback = false,img3active = true;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_playing);
        Intent intent = getIntent();
        podcastList = (Podcast_list) intent.getSerializableExtra("podcastObject");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.btn_back);

        category = (TextView) findViewById(R.id.category);
        title = (TextView) findViewById(R.id.titiletext);
        description = (TextView) findViewById(R.id.description);
        toolbarTitle = (TextView) findViewById(R.id.toolbar_title);
        play = (ImageView) findViewById(R.id.playButton);
        pause = (ImageView) findViewById(R.id.pauseButton);
        back = (ImageView) findViewById(R.id.backButton);
        forward = (ImageView) findViewById(R.id.forwardButton);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        progressBarHorz = (ProgressBar) findViewById(R.id.progressBarHorz);

        categor = podcastList.getPodcast_category();
        category.setText(categor);
        title.setText(podcastList.getPodcast_title());
        toolbarTitle.setText(podcastList.getPodcast_title());
        description.setText(podcastList.getPodcast_description());
        mediaUri = podcastList.getAudio_url();
        progressBarHorz.setVisibility(View.VISIBLE);
        progressBar.setClickable(false);
        cd = new ConnectionDetector(this);
        if(cd.isConnectingToInternet()) {
            try {
                startloading();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public void startloading() throws IOException {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        if(mediaUri.isEmpty()){
            Toast toast = Toast.makeText(this, "No Audio file", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
            progressBarHorz.setVisibility(View.GONE);
        }
        else {

            mediaPlayer.setDataSource(mediaUri);
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    startplaying();
                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            mediaPlayer.stop();
                            mediaPlayer.reset();
                            startDialog();
                        }
                    });
                }
            });
       /* play_media = new playMedia();
        play_media.execute();*/
            myHandler.postDelayed(UpdateSongTime, 2000);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkReceiver, filter);
        if (stopmediaplayer)
            mediaPlayer.start();
        myHandler.postDelayed(UpdateSongTime, 2000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(networkReceiver);
        Log.d("pause", "pause");
    }
    @Override
    protected void onStop() {
        super.onStop();
        if (mediaPlayer != null) {
            stopmediaplayer = true;
            MainActivity.media_position = mediaPlayer.getCurrentPosition();
            myHandler.removeCallbacks(UpdateSongTime);
            mediaPlayer.pause();
        }
        Log.d("stop", "stop");
    }

    @Override
    protected void onDestroy() {
        if(mediaPlayer!=null) {
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        Log.d("destroy", "destroy");
        super.onDestroy();
        play.setOnClickListener(null);
        pause.setOnClickListener(null);
        forward.setOnClickListener(null);
        back.setOnClickListener(null);

    }


    void startplaying(){
    if (mediaUri.isEmpty()) {
                progressBarHorz.setVisibility(View.INVISIBLE);
                Toast toast = Toast.makeText(Media_playing.this, "No Audio file", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                cangoback = true;
                return;
            } else {
                progressBar.setMax(mediaPlayer.getDuration());
                progressBar.setProgress(0);
                if (MainActivity.media_position > 0) {
                    progressBar.setProgress(MainActivity.media_position);
                    mediaPlayer.seekTo(MainActivity.media_position);
                }
                if(mediaPlayer!=null)
                    mediaPlayer.start();
                progressBarHorz.setVisibility(View.INVISIBLE);
                play.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mediaPlayer.start();
                        play.setVisibility(View.INVISIBLE);
                        pause.setVisibility(View.VISIBLE);
                    }
                });
                pause.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mediaPlayer.pause();
                        pause.setVisibility(View.INVISIBLE);
                        play.setVisibility(View.VISIBLE);

                    }
                });

                back.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mediaPlayer == null) {
                            return;
                        }
                        mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() - backwardTime);
                        progressBar.setProgress(mediaPlayer.getCurrentPosition() - backwardTime);
                    }
                });
                forward.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mediaPlayer == null) {
                            return;
                        }
                        int forward = mediaPlayer.getCurrentPosition() + forwardTime;
                        mediaPlayer.seekTo(forward);
                        progressBar.setProgress(mediaPlayer.getCurrentPosition());
                    }
                });

                back.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (event.getAction() == MotionEvent.ACTION_UP) {
                            return true;

                        } else {
                            if (mediaPlayer == null) {
                                return true;
                            }
                            int backward = mediaPlayer.getCurrentPosition() - 500;
                            mediaPlayer.seekTo(backward);
                            progressBar.setProgress(mediaPlayer.getCurrentPosition());
                        }
                        return true;
                    }
                });
                forward.setOnTouchListener(new View.OnTouchListener() {
                    @Override public boolean onTouch(View v, MotionEvent event){
                        if (event.getAction() == MotionEvent.ACTION_UP) {
                            return true;

                        } else {
                            if (mediaPlayer == null) {
                                return true;
                            }
                            int forward = mediaPlayer.getCurrentPosition() + 500;
                            mediaPlayer.seekTo(forward);
                            progressBar.setProgress(mediaPlayer.getCurrentPosition());
                        }
                        return true;
                    }
                });
            }
            cangoback = true;
        }
    private Runnable UpdateSongTime = new Runnable() {
        public void run() {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                startTime = mediaPlayer.getCurrentPosition();
                progressBar.setProgress(startTime);
                myHandler.postDelayed(this, 2000);
            }
        }
    };
    void alertbuilderDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView;
        LayoutInflater inflater = this.getLayoutInflater();
        if(cd.isConnectingToInternet()){
            dialogView = inflater.inflate(R.layout.alertsuccess, null);
            builder.setView(dialogView);
            TextView mainpage= (TextView) dialogView.findViewById(R.id.mainpage);
            mainpage.setOnClickListener(new View.OnClickListener() {
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
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ad.cancel();
                }
            });

        }
    }

    @Override
    public void onBackPressed() {
        Log.d("back", "pressed");
        if (cangoback)
            super.onBackPressed();
    }

    class imageclick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (img3active) {
                img3active = false;
                dialog.findViewById(R.id.three_inactive).setVisibility(View.VISIBLE);
                dialog.findViewById(R.id.three_active).setVisibility(View.INVISIBLE);
            }
            image_cllicked = (ImageView) dialog.findViewById(v.getId());
            int x, length;
            String tag = String.valueOf(image_cllicked.getTag());
            length = tag.length();
            rating = tag.substring(tag.length() - 1);
            if (temp != null) {
                temp.setVisibility(View.INVISIBLE);
            }
            x = Integer.parseInt(tag.substring(length - 1, length));
            temp = image_ids[x + 4];
            temp.setVisibility(View.VISIBLE);

        }
    }

    void startDialog() {
        dialog = new Dialog(this);
        // preparing dialog view
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.feedback);
        image_ids[0] = (ImageView) dialog.findViewById(R.id.one_inactive);
        image_ids[1] = (ImageView) dialog.findViewById(R.id.two_inactive);
        image_ids[2] = (ImageView) dialog.findViewById(R.id.three_inactive);
        image_ids[3] = (ImageView) dialog.findViewById(R.id.four_inactive);
        image_ids[4] = (ImageView) dialog.findViewById(R.id.five_inactive);
        feedback = (ImageView) dialog.findViewById(R.id.feedback);

        image_ids[5] = (ImageView) dialog.findViewById(R.id.one_active);
        image_ids[6] = (ImageView) dialog.findViewById(R.id.two_active);
        image_ids[7] = (ImageView) dialog.findViewById(R.id.three_active);
        image_ids[8] = (ImageView) dialog.findViewById(R.id.four_active);
        image_ids[9] = (ImageView) dialog.findViewById(R.id.five_active);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable
                (android.graphics.Color.TRANSPARENT));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(true);
        dialog.show();

        feedback.setOnClickListener(new feedbackClick());
        temp = new ImageView(getApplicationContext());
        for (int i = 0; i < image_ids.length; i++) {
            image_ids[i].setOnClickListener(new imageclick());
        }
    }

    class feedbackClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (rating != null)
                podcastList.setRating(rating);
            else
                podcastList.setRating("3");
            if(cd.isConnectingToInternet()) {
                PostFeedback postFeedback = new PostFeedback(getApplicationContext());
                postFeedback.execute(podcastList);
            }
            alertbuilderDialog();
        }
    }
    private BroadcastReceiver networkReceiver = new BroadcastReceiver()
    {
        @Override public void onReceive(Context context, Intent intent) {
            if (intent.getExtras() != null) {
                NetworkInfo ni = (NetworkInfo) intent.getExtras().get(ConnectivityManager.EXTRA_NETWORK_INFO);
                if (ni != null && ni.getState() == NetworkInfo.State.CONNECTED) {
                    // we're connected
                    if (mediaPlayer!=null) {
                        mediaPlayer.start();
                        pause.setVisibility(View.VISIBLE);
                        play.setVisibility(View.INVISIBLE);
                        play.setEnabled(true);
                        forward.setEnabled(true);
                        back.setEnabled(true);
                    }

                }

                // we're not connected
                else {
                    Toast toast = Toast.makeText(context, "No Internet Connection", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    if(mediaPlayer!=null)
                    mediaPlayer.pause();
                    pause.setVisibility(View.INVISIBLE);
                    play.setVisibility(View.VISIBLE);
                    play.setEnabled(false);
                    forward.setEnabled(false);
                    back.setEnabled(false);
                }
            }
         }
    };

}
