package HttpConnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import ModelClasses.Podcast_list;
import au.com.appscore.emcast.MainActivityFragment;

/**
 * Created by krrish on 8/03/2016.
 */
public class JsonParser {
    static public ArrayList<Podcast_list> arrayList;
    public  void parseData(String content) {
        //Arraylist to store the dat
         arrayList = new ArrayList<Podcast_list>();
        try {
            JSONObject jsonObject = new JSONObject(content);
            //dataarray variable to store the contentarray
            JSONArray dataArray = jsonObject.getJSONArray("data");
            for (int i = 0; i < dataArray.length(); i++) {
                Podcast_list podcastData = new Podcast_list();
                JSONObject dataObject = dataArray.getJSONObject(i);
                podcastData.setPodcast_id(dataObject.getInt("podcast_id"));
                podcastData.setPodcast_title(dataObject.getString("podcast_title"));
                podcastData.setPodcast_category(dataObject.getString("podcast_category"));
                podcastData.setPodcast_description(dataObject.getString("podcast_description"));
                podcastData.setExistence_time(dataObject.getInt("existence_time"));
                podcastData.setAudio_url(dataObject.getString("audio_url"));
                podcastData.setVideo_url(dataObject.getString("video_url"));
                arrayList.add(podcastData);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            MainActivityFragment.noerror=true;
        }
        Collections.sort(arrayList,new CustomComparator());
    }
public class CustomComparator implements Comparator<Podcast_list> {
    @Override
    public int compare(Podcast_list lhs, Podcast_list rhs) {
        Integer obj1 = new Integer(lhs.getExistence_time());
        Integer obj2 = new Integer(rhs.getExistence_time());
        return obj1.compareTo(obj2);
    }
}

    }

