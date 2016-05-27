package au.com.appscore.emcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import ModelClasses.Podcast_list;

/**
 * Created by krrish on 7/04/2016.
 */
public class Networklistener extends BroadcastReceiver {
    private  Podcast_list Now_playing;
    @Override
    public void onReceive(Context context, Intent intent) {
        /*Now_playing=MainActivity.Now_playing;
        MainActivity mainActivity=new MainActivity();
        Intent inten2t= new Intent(context,MainActivity.class);
        mainActivity.startActivity(inten2t);*/
    }
}
