package com.example.nirlan.popcornmovies;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * GridView adapter to ImageViews items.
 * Created by nirlan on 11.09.16.
 */
public class ImageAdapter extends BaseAdapter {

    private final String LOG_TAG = ImageAdapter.class.getSimpleName();

    private Context mContext;
    public List<Item> movieData = new ArrayList<>();

    public ImageAdapter(Context context) {
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return movieData.size();
    }

    @Override
    public Object getItem(int position) {
        return movieData.get(position).getMovieUrlPath();
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.grid_item, parent, false);
        }

        ImageView imageView = (ImageView) convertView.findViewById(R.id.grid_image_item);

        final String POSTER_FINAL_URL = movieData.get(position)
                .getMovieUrlPath()
                .trim();

        Log.v(LOG_TAG, movieData.get(position).getMovieUrlPath());

        Picasso
                .with(mContext)
                .load(POSTER_FINAL_URL)
                .fit()
                .into(imageView);

        return convertView;
    }

    public void add(String sa, String sb) {
        Item item = new Item(sa, sb);
        movieData.add(item);
    }

    public void clear() {
        movieData.clear();
    }

    public String getMovieIdFromList(int position) {
        return movieData.get(position).getMovieId();
    }

    public class Item {
        String itemId;
        String itemPath;

        public Item(String itemId, String itemPath) {
            this.itemId = itemId;
            this.itemPath = itemPath;
        }

        public String getMovieId() {
            return itemId;
        }

        public String getMovieUrlPath() {
            return itemPath;
        }
    }
}


