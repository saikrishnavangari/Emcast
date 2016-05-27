package ModelClasses;

import java.io.Serializable;

/**
 * Created by krrish on 8/03/2016.
 */
public class Podcast_list implements Serializable {

    public int podcast_id;
    public String podcast_title;
    public String podcast_category;
    public String podcast_description;
    public int existence_time;
    public String audio_url;
    public String video_url;
    public String rating;

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public int getPodcast_id() {
        return podcast_id;
    }

    public void setPodcast_id(int podcast_id) {
        this.podcast_id = podcast_id;
    }

    public String getPodcast_title() {
        return podcast_title;
    }

    public void setPodcast_title(String podcast_title) {
        this.podcast_title = podcast_title;
    }

    public String getPodcast_category() {
        return podcast_category;
    }

    public void setPodcast_category(String podcast_category) {
        this.podcast_category = podcast_category;
    }

    public String getPodcast_description() {
        return podcast_description;
    }

    public void setPodcast_description(String podcast_description) {
        this.podcast_description = podcast_description;
    }

    public int getExistence_time() {
        return existence_time;
    }

    public void setExistence_time(int existence_time) {
        this.existence_time = existence_time;
    }

    public String getAudio_url() {
        return audio_url;
    }

    public void setAudio_url(String audio_url) {
        this.audio_url = audio_url;
    }

    public String getVideo_url() {
        return video_url;
    }

    public void setVideo_url(String video_url) {
        this.video_url = video_url;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
