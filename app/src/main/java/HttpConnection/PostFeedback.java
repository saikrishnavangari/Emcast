package HttpConnection;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import ModelClasses.Podcast_list;

/**
 * Created by krrish on 15/03/2016.
 */
public class PostFeedback extends AsyncTask<Podcast_list, Void, Void> {
    String baseUrl = "http://emcast.appscorehosting.com.au/api/podcasts/addFeedback";
    Podcast_list podcastObject;
    AlertDialog alertDialog;
    Context context;

    public PostFeedback(Context context) {
        this.context = context;
    }

    protected Void doInBackground(Podcast_list... params) {
        try {
            podcastObject=params[0];
            URL url = new URL(baseUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            JSONObject obj = new JSONObject();
            obj.put("podcast_id", podcastObject.getPodcast_id());
            obj.put("rating", podcastObject.getRating());
            OutputStream writer = conn.getOutputStream();
            writer.write(obj.toString().getBytes());
            writer.flush();
            writer.close();
            //Read Response
            InputStream is = conn.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            String line;
            StringBuffer response = new StringBuffer();
            while ((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

    }
}
