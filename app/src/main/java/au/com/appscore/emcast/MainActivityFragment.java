package au.com.appscore.emcast;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import AdapterClasses.MyAdapter;
import HttpConnection.ConnectionDetector;
import HttpConnection.HttpurlConnection;
import HttpConnection.JsonParser;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    //baseUrl
    private static String base_url = "http://emcast.appscorehosting.com.au/api/podcasts/list";
    public RecyclerView rvPodcast;
    public MyAdapter adapter;
    private static String content;
    private ProgressBar progressBar;
    private GetJsonData getJsonData;
    private JsonParser jsonParser;
    private ConnectionDetector cd;
    private Activity activity;
    private HttpurlConnection conn;
    public static Boolean noerror=true;

    public MainActivityFragment() {
    }

    private BroadcastReceiver networkReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getExtras() != null) {
                NetworkInfo ni = (NetworkInfo) intent.getExtras().get(ConnectivityManager.EXTRA_NETWORK_INFO);
                if (ni != null && ni.getState() == NetworkInfo.State.CONNECTED) {
//                     we're connected
                    downloadData();
                }
                else {
                    Toast toast = Toast.makeText(getActivity(), "No Internet Connection", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
            }
            // we're not connected
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cd = new ConnectionDetector(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_main, container, false);
        progressBar = (ProgressBar) view.findViewById(R.id.progressbar);
        rvPodcast = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        rvPodcast.setLayoutManager(new LinearLayoutManager(activity));
        return view;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        downloadData();
        activity=getActivity();
    }
    void downloadData()
    {
        if(cd.isConnectingToInternet()) {
            getJsonData = new GetJsonData(this);
            getJsonData.execute(base_url);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        activity.registerReceiver(networkReceiver, filter);
    }

    @Override
    public void onStop() {
        super.onStop();

    }

    @Override
    public void onPause() {
        super.onPause();
        activity.unregisterReceiver(networkReceiver);
        Log.d("pause", "yes");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("onDestroy", "yes");
        if(cd.isConnectingToInternet())
        {
            getJsonData.cancel(true);
            getJsonData = null;
        }


    }

    public class GetJsonData extends AsyncTask<String, Void, Void> {
        public static final String TAG = "fetchJsonData";
        private MainActivityFragment context;
        public GetJsonData(MainActivityFragment context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(String... args) {
            Log.d(TAG,"A==> ThId : " + Thread.currentThread().getId()+"T: "+System.currentTimeMillis());
            conn=null;
            conn = new HttpurlConnection((MainActivity)activity);
            content = conn.getConnection(args[0]);
            Log.d(TAG,"B==> ThId : " + Thread.currentThread().getId()+"T: "+System.currentTimeMillis());
            if (content != null) {
                jsonParser = new JsonParser();
                jsonParser.parseData(content);
                Log.d(TAG,"C==> ThId : " + Thread.currentThread().getId()+"T: "+System.currentTimeMillis());
                Log.d("content", content);
            }
            Log.d(TAG,"D ==> ThId :" + Thread.currentThread().getId()+"T: "+System.currentTimeMillis());
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            // Attach the adapter to the recyclerview to populate items
            if (content != null&&noerror) {
                Log.d(TAG, "E==> ThId : " + Thread.currentThread().getId() + "T: " + System.currentTimeMillis());
                adapter = new MyAdapter(activity, JsonParser.arrayList);
                rvPodcast.setAdapter(adapter);
                progressBar.setVisibility(View.GONE);
                MainActivity.viewupdated=true;
                Log.d(TAG,"F==> ThId : " + Thread.currentThread().getId()+"T: "+System.currentTimeMillis());
            }
                 else{
                downloadData();
                noerror=true;
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            conn=null;
            jsonParser=null;
        }
    }


}
