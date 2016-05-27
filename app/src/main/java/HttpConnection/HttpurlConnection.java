package HttpConnection;

import android.app.Activity;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by krrish on 8/03/2016.
 */
public class HttpurlConnection {
    public static final String TAG = "fetchJsonData";
    private Activity activity;
    private HttpURLConnection con;
    public HttpurlConnection(FragmentActivity activity) {
        this.activity=activity;
    }

    //establish httpurlconnection with the server and return the json content
    public  String getConnection(String Url) {
        String address = Url;
        String content= " ";
        con=null;
        BufferedReader reader = null;
        try {
            URL url = new URL(address);
            Log.d("getConnection","T1: " + System.currentTimeMillis());
            con = (HttpURLConnection) url.openConnection();
            Log.d("getConnection","T2: " + System.currentTimeMillis());
            reader = new BufferedReader(new InputStreamReader(con.getInputStream(),"UTF-8"));
            String line;
            while ((line = reader.readLine())!= null) {
                content=content+line;
            }
            Log.d("getConnection","T3: " + System.currentTimeMillis());
            Log.d("contentraw",content);
            return content;
        } catch (Exception e) {
            return null;
        }finally {
            if(con!=null)
                con.disconnect();
            if(reader!=null){
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
