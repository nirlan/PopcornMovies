package com.example.nirlan.popcornmovies;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private final String LOG_TAG = MainActivityFragment.class.getSimpleName();
    private ImageAdapter mImageAdapter;
    private OnFragmentInteractionListener mListener;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        final View v = inflater.inflate(R.layout.fragment_main, container, false);
        GridView gridview = (GridView) v.findViewById(R.id.grid_view);
        mImageAdapter = new ImageAdapter(getActivity());
        gridview.setAdapter(mImageAdapter);

        String popularity = "popularity.desc";
        //String highestRated = "vote_average.desc";

        FetchMovieTask movieTask = new FetchMovieTask();
        movieTask.execute(popularity);



        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                if (mListener != null) {
                    String movieId = mImageAdapter.getMovieIdFromList(position);
                    mListener.onFragmentInteraction(movieId);
                }
            }
        });

        return v;
    }

//    @Override
//    public void onStart() {
//        super.onStart();
//        String popularity = "popularity.desc";
//        //String highestRated = "vote_average.desc";
//        FetchMovieTask movieTask = new FetchMovieTask();
//        movieTask.execute(popularity);
//    }

//    // TODO: Rename method, update argument and hook method into UI event
//    public void onButtonPressed(String text) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(text);
//        }
//    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(String text);
    }

    public class FetchMovieTask extends AsyncTask<String, Void, HashMap<String, String>> {

        private final String LOG_TAG = FetchMovieTask.class.getSimpleName();
        int movieNum;

        private HashMap<String, String> getMovieDataFromJson(String movieJsonStr)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted. The plan
            // is here just fetch the data that will be needed on the main screen. In MovieDetail
            // fragment will be fetched the data for each selected movie, on demand.

            final String TMDB_RESULTS = "results";
            final String TMDB_ID = "id";
            final String TMDB_POSTER = "poster_path";
            //final String TMDB_YEAR = "release_date";
            //final String TMDB_RATE = "vote_average";
            //final String TMDB_TITLE = "title";
            //final String TMDB_SYNOPSIS = "overview";
            //final String TMDB_RUNTIME = "runtime";

            JSONObject movieJson = new JSONObject(movieJsonStr);
            JSONArray movieArray = movieJson.getJSONArray(TMDB_RESULTS);

            // Calculates the number of movies in the JSONArray file.
            movieNum = movieArray.length();
            //movieNum = 10;
            Log.v(LOG_TAG, "" + movieNum);

            // Creates a HashMap of Movie ID String and String of poster path that will be the method output.
            HashMap<String, String> resultMap = new HashMap<>();

            // For each movie, extract the ID and the Poster Path from the JSON object.
            for (int i = 0; i < movieNum; i++) {
                String movieIDStr = movieArray.getJSONObject(i).getString(TMDB_ID);
                String moviePosterPathStr = movieArray.getJSONObject(i).getString(TMDB_POSTER);
                resultMap.put(movieIDStr, moviePosterPathStr);
            }

            return resultMap;
        }

        protected HashMap<String, String> doInBackground(String... params) {

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String movieJsonStr = null;
//
//          String movieID;

            try {
                // Construct the URL for the "The Movie Database" query
                // Possible parameters are available at TMDB's API page, at
                // http://docs.themoviedb.apiary.io/#

                final String MOVIE_BASE_URL =
                        "http://api.themoviedb.org/3/discover/movie?";
//
//                final String MOVIE_BASE_URL_EACH_MOVIE =
//                        "http://api.themoviedb.org/3/movie/"
//                        + movieID
//                        + "?";
//                final String MOVIE_BASE_URL_TRAILER =
//                        "http://api.themoviedb.org/3/movie/"
//                        + movieID
//                        + "/videos?";
                final String QUERY_PARAM = "sort_by";
                final String APPID_PARAM = "api_key";

                Uri builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                        .appendQueryParameter(QUERY_PARAM, params[0])
                        .appendQueryParameter(APPID_PARAM, BuildConfig.TMDB_API_KEY)
                        .build();

                URL url = new URL(builtUri.toString());

                // Create the request to TMDB, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
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
                    buffer.append(line + "\n");
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
                return getMovieDataFromJson(movieJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(HashMap<String, String> resultMap) {

            String moviePosterPathUrlBase =
                    "http://image.tmdb.org/t/p/w185";
            ArrayList<String> moviePathArray = new ArrayList<>();
            //ArrayList<String> movieIdArray = new ArrayList<>();

            if (resultMap != null) {

                if (mImageAdapter != null) {
                    mImageAdapter.clear();
                }

                for (Map.Entry<String, String> entry : resultMap.entrySet()) {
                    String movieID = entry.getKey();
                    String moviePosterPath =
                            moviePosterPathUrlBase + entry.getValue();
                    mImageAdapter.add(movieID, moviePosterPath);
                }
            }
            mImageAdapter.notifyDataSetChanged();
        }
    }
}
