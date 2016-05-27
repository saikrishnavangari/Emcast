package AdapterClasses;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import ModelClasses.Podcast_list;
import au.com.appscore.emcast.MainActivity;
import au.com.appscore.emcast.Media_playing;
import au.com.appscore.emcast.Mediaplayingmp4;
import au.com.appscore.emcast.R;


/**
 * Created by krrish on 8/03/2016.
 */
public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    private ArrayList mDataset;
    public Context mcontext;
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView categoryTv;
        public TextView titleTv;
        public TextView descriptionTv;
        public TextView existenceTv;
        //Dialog welcomescreen variables
        public TextView DcategoryTv;
        public TextView DtitleTv;
        public TextView DdescriptionTv;
        public TextView DexistenceTv;
        public Context context;
        public Podcast_list viewObject;
        public ImageView mp3IV;
        public ImageView mp4IV;

        public ViewHolder(View itemview, Context context) {
            super(itemview);
            this.context=context;
            itemview.setOnClickListener(this);
            categoryTv= (TextView) itemview.findViewById(R.id.categoryTv);
            descriptionTv= (TextView) itemview.findViewById(R.id.descriptionTv);
            titleTv= (TextView) itemview.findViewById(R.id.titleTv);
            existenceTv= (TextView) itemview.findViewById(R.id.existenceTv);
        }
        public void setDataobject(Podcast_list viewObject){
            this.viewObject=viewObject;
        }
        @Override
        public void onClick(View v) {
            Log.d("clicked", viewObject.getPodcast_category());
            final Dialog dialog = new Dialog(v.getContext());

        // preparing dialog view
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.customdialog);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable
                    (android.graphics.Color.TRANSPARENT));
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.setCancelable(true);
            // intialise dialog welcomescreen variables
            DcategoryTv= (TextView) dialog.findViewById(R.id.DcategoryTv);
            DdescriptionTv= (TextView) dialog.findViewById(R.id.DdescriptionTv);
            DtitleTv= (TextView) dialog.findViewById(R.id.DtitleTv);
            DexistenceTv= (TextView) dialog.findViewById(R.id.DexistenceTv);
            mp3IV= (ImageView) dialog.findViewById(R.id.Mp3Iv);
            mp4IV= (ImageView) dialog.findViewById(R.id.Mp4Iv);
            //set clicked data to dialog welcomescreen

            DcategoryTv.setText(viewObject.getPodcast_category());
            DdescriptionTv.setText(viewObject.getPodcast_description());
            DtitleTv.setText(viewObject.getPodcast_title());
            DexistenceTv.setText(viewObject.getExistence_time() / 24 + " days ago");
            mp3IV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), Media_playing.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("podcastObject", viewObject);
                    MainActivity.Now_playing=viewObject;
                    MainActivity.media_type="mp3";
                    MainActivity.media_position=0;
                    context.startActivity(intent);
                    dialog.dismiss();
                }
            });
            mp4IV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent= new Intent(v.getContext(),Mediaplayingmp4.class);
                    intent.putExtra("podcastObject", viewObject);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    MainActivity.Now_playing=viewObject;
                    MainActivity.media_type="mp4";
                    MainActivity.media_position=0;
                    context.startActivity(intent);
                    dialog.dismiss();
                }
            });
            dialog.show();
        }
    }
    // Provide a suitable constructor (depends on the kind of dataset)
    public MyAdapter(Context context, ArrayList<Podcast_list> myDataset) {
        mDataset = myDataset;
        this.mcontext=context;
    }
    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.populatedata, parent, false);
        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView, context);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyAdapter.ViewHolder holder, int position) {
        // Get the data model based on position
        Podcast_list podcastObject = (Podcast_list)mDataset.get(position);
        holder.setDataobject(podcastObject);
        // Set item views based on the data model
        holder.categoryTv.setText(podcastObject.getPodcast_category());
        holder.titleTv.setText(podcastObject.getPodcast_title());
        holder.descriptionTv.setText(podcastObject.getPodcast_description());
        holder.existenceTv.setText(podcastObject.getExistence_time()/24 +" days ago");
    }

    // Return the total count of items
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}

