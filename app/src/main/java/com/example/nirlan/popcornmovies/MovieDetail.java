package com.example.nirlan.popcornmovies;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MovieDetail extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_MOVIE_ID = "param1";
    private final String LOG_TAG = MovieDetail.class.getSimpleName();
    // TODO: Rename and change types of parameters
    private String movieId;
    //private ArrayList<String> movieDataList;
    private View view;

    public MovieDetail() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment MovieDetail.
     */
    // TODO: Rename and change types and number of parameters
    public static MovieDetail newInstance(String param1) {
        MovieDetail fragment = new MovieDetail();
        Bundle args = new Bundle();
        args.putString(ARG_MOVIE_ID, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            movieId = getArguments().getString(ARG_MOVIE_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_movie_detail, container, false);

        FetchMovieDetailTask fetch = new FetchMovieDetailTask();
        fetch.execute(movieId);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    public void updateContent(String text) {
        FetchMovieDetailTask fetch = new FetchMovieDetailTask();
        fetch.execute(text);
    }

//    @Override
//    public void onStart() {
//        super.onStart();
//        updateContent(movieId);
//
//        TextView textViewTitle = (TextView) getActivity().findViewById(R.id.movie_title);
//        textViewTitle.setText(movieTitle);
//
//        TextView textViewRate = (TextView) getActivity().findViewById(R.id.movie_rate);
//        textViewRate.setText(movieRate);
//
//        TextView textViewYear = (TextView) getActivity().findViewById(R.id.movie_year);
//        textViewYear.setText(movieYear);
//
//        ImageView imageViewPoster = (ImageView) getActivity().findViewById(R.id.movie_poster);
//        Picasso
//                .with(getContext())
//                .load(moviePosterPath)
//                .fit()
//                .into(imageViewPoster);
//
//        TextView textViewOverview = (TextView) getActivity().findViewById(R.id.movie_overview);
//        textViewOverview.setText(movieOverview);
//
//        TextView textViewRuntime = (TextView) getActivity().findViewById(R.id.movie_runtime);
//        textViewRuntime.setText(movieRuntime);
//    }

    private void setTextMovieDetail(String str, View v, int idTextView) {

        TextView textView = (TextView) v.findViewById(idTextView);
        textView.setText(str);
    }

    public void markAsFavorite() {
        Button button = (Button) getActivity().findViewById(R.id.favorite_button);
    }

    public void playTrailler() {
        Button button = (Button) getActivity().findViewById(R.id.play_trailer_button);
    }

    public class FetchMovieDetailTask extends AsyncTask<String, Void, ArrayList<String>> {

        private final String LOG_TAG = FetchMovieDetailTask.class.getSimpleName();

        private ArrayList<String> getMovieDetailFromJson(String movieJsonStr)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted. The plan
            // is here just fetch the data that will be needed on the main screen. In MovieDetail
            // fragment will be fetched the data for each selected movie, on demand.


            final String TMDB_YEAR = "release_date";
            final String TMDB_RATE = "vote_average";
            final String TMDB_TITLE = "title";
            final String TMDB_POSTER_URL = "poster_path";
            final String TMDB_SYNOPSIS = "overview";
            final String TMDB_RUNTIME = "runtime";

            JSONObject movieJson = new JSONObject(movieJsonStr);

            String movieYear = movieJson.getString(TMDB_YEAR);
            String movieRate = movieJson.getString(TMDB_RATE);
            String movieTitle = movieJson.getString(TMDB_TITLE);
            String moviePosterPath = "http://image.tmdb.org/t/p/w185"
                    + movieJson.getString(TMDB_POSTER_URL);
            String movieOverview = movieJson.getString(TMDB_SYNOPSIS);
            String movieRuntime = movieJson.getString(TMDB_RUNTIME);

            // Creates a ArrayList of movie details, that will be the method output.
            ArrayList<String> resultDetailArray = new ArrayList<>(6);

            resultDetailArray.add(movieTitle);
            resultDetailArray.add(movieYear);
            resultDetailArray.add(movieRate);
            resultDetailArray.add(moviePosterPath);
            resultDetailArray.add(movieOverview);
            resultDetailArray.add(movieRuntime);

            Log.v(LOG_TAG, "\t" + resultDetailArray);

            return resultDetailArray;
        }

        protected ArrayList<String> doInBackground(String... params) {

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String movieJsonStr = null;
//
            String movieId = params[0];

            try {
                // Construct the URL for the "The Movie Database" query
                // Possible parameters are available at TMDB's API page, at
                // http://docs.themoviedb.apiary.io/#

                final String MOVIE_BASE_URL =
                        "http://api.themoviedb.org/3/movie/"
                                + movieId
                                + "?";

//                final String MOVIE_BASE_URL_TRAILER =
//                        "http://api.themoviedb.org/3/movie/"
//                        + movieID
//                        + "/videos?";

                final String APPID_PARAM = "api_key";

                Uri builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                        .appendQueryParameter(APPID_PARAM, BuildConfig.TMDB_API_KEY)
                        .build();

                URL url = new URL(builtUri.toString());

                // Create the request to TMDB, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuilder buffer = new StringBuilder();
                if (inputStream == null) {
                    // Nothing to do so.
                    movieJsonStr = null;
                }

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it makes debugging a lot easier if you print out the completed
                    // buffer for debugging
                    buffer.append(line)
                    .append("\n");

                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    movieJsonStr = null;
                }
                movieJsonStr = buffer.toString();

                //Log.v(LOG_TAG, "Json String " + movieJsonStr);

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error", e);
                // If the code didn't successfully get the movie data, there's no point in attempting
                // to parse it.
                movieJsonStr = null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                return getMovieDetailFromJson(movieJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<String> result) {

            if (result != null) {

//                if (movieDataList != null) {
//                    movieDataList.clear();
//                }
//                movieDataList = result;
                String movieTitle = result.get(0);
                String movieYear = result.get(1);
                String movieRate = result.get(2);
                String moviePosterPath = result.get(3);
                String movieOverview = result.get(4);
                String movieRuntime = result.get(5);

                Log.v(LOG_TAG, "movieDataList Results: "
                        + "\n" + movieTitle
                        + "\n" + movieYear
                        + "\n" + movieRate
                        + "\n" + moviePosterPath
                        + "\n" + movieOverview
                        + "\n" + movieRuntime
                );
                TextView textViewTitle = (TextView) view.findViewById(R.id.movie_title);
                textViewTitle.setText(movieTitle);
                Log.v(LOG_TAG, "TextView" + movieTitle);

                TextView textViewRate = (TextView) view.findViewById(R.id.movie_rate);
                textViewRate.setText(movieRate);

                TextView textViewYear = (TextView) view.findViewById(R.id.movie_year);
                textViewYear.setText(movieYear);

                ImageView imageViewPoster = (ImageView) view.findViewById(R.id.movie_poster);
                Picasso
                        .with(getContext())
                        .load(moviePosterPath)
                        .fit()
                        .into(imageViewPoster);

                TextView textViewOverview = (TextView) view.findViewById(R.id.movie_overview);
                textViewOverview.setText(movieOverview);

                TextView textViewRuntime = (TextView) view.findViewById(R.id.movie_runtime);
                textViewRuntime.setText(movieRuntime);
            }
        }
    }
}