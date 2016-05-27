package au.com.appscore.emcast;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;

import HttpConnection.ConnectionDetector;
import ModelClasses.Podcast_list;

public class MainActivity extends AppCompatActivity {
    public static Podcast_list Now_playing;
    public static String media_type;
    public static int media_position;
    public static Boolean viewupdated=false;
    private ConnectionDetector cd;
    private ImageView IV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        cd = new ConnectionDetector(this);
        IV = (ImageView) findViewById(R.id.Nowplaying);
        if (Now_playing != null) {
            IV.setImageResource(R.drawable.icon_nowplaying);
        }
        IV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Now_playing != null&& cd.isConnectingToInternet()) {
                    viewupdated=false;
                    Intent intent=null;
                    if (media_type == "mp3")
                        intent = new Intent(v.getContext(), Media_playing.class);

                    else
                        intent = new Intent(v.getContext(), Mediaplayingmp4.class);

                    intent.putExtra("podcastObject", Now_playing);
                    v.getContext().startActivity(intent);
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return false;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
