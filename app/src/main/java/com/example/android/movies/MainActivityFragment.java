package com.example.android.movies;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;

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
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    public ImageListAdapter imageListAdapter;
    public List<MovieObject> movieObjectList;

    public MainActivityFragment() {
    }

    @Override
    public void onStart() {
        super.onStart();

        FetchMoviesTask moviesTask = new FetchMoviesTask();
        moviesTask.execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);


        GridView gridView = (GridView) rootView.findViewById(R.id.gridView);
        imageListAdapter = new ImageListAdapter(getContext());
        gridView.setAdapter(imageListAdapter);

        return rootView;
    }

    public class FetchMoviesTask extends AsyncTask<Void, Void, List<MovieObject>> {

        private String LOG_TAG = FetchMoviesTask.class.getSimpleName();

        @Override
        protected List<MovieObject> doInBackground(Void... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String moviesJson = "";

            try {

                String SCHEME = "http";
                String AUTHORITY= "api.themoviedb.org";
                String SORTBY = "popularity.desc";
                String APIKEY = "";

                Uri uri = new Uri.Builder().scheme(SCHEME)
                        .authority(AUTHORITY)
                        .appendPath("3")
                        .appendPath("discover")
                        .appendPath("movie")
                        .appendQueryParameter("sort_by", SORTBY)
                        .appendQueryParameter("api_key", APIKEY)
                        .build();

                URL url = new URL(uri.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                moviesJson = buffer.toString();

                return getMoviesURLFromJson(moviesJson);

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } catch (JSONException je){
                Log.e(LOG_TAG, "Error ", je);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
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
        }

        private List<MovieObject> getMoviesURLFromJson(String moviesJson) throws JSONException{
            List<MovieObject> movies = new ArrayList<>();

            String OWM_RESULT = "results";
            String OWM_ID = "id";
            String OWM_POSTER = "poster_path";
            String OWM_ORIGINALTITLE = "original_title";
            String OWM_OVERVIEW = "overview";
            String OWM_VOTEAVERAGE = "vote_average";

            JSONObject forecastJson = new JSONObject(moviesJson);
            JSONArray moviesArray = forecastJson.getJSONArray(OWM_RESULT);

            for(int i = 0; i < moviesArray.length(); i++){
                JSONObject movieObject = moviesArray.getJSONObject(i);

                MovieObject movie = new MovieObject();
                movie.id = movieObject.getString(OWM_ID);
                movie.poster = movieObject.getString(OWM_POSTER);
                movie.originalTitle = movieObject.getString(OWM_ORIGINALTITLE);
                movie.overview = movieObject.getString(OWM_OVERVIEW);
                movie.voteAverage = movieObject.getString(OWM_VOTEAVERAGE);

                movies.add(movie);
            }

            return movies;
        }

        @Override
        protected void onPostExecute(List<MovieObject> movieObjects) {

            if(movieObjects != null) {
                imageListAdapter.clear();
                for(MovieObject movieObject : movieObjects){
                    imageListAdapter.add(movieObject.poster);
                }
                movieObjectList = new ArrayList<>(movieObjects);
            }
        }
    }

    public class ImageListAdapter extends ArrayAdapter {
        private Context context;
        private LayoutInflater inflater;

        final private String LOG_TAG = ImageListAdapter.class.getSimpleName();

        private List<String> imageUrls = new ArrayList<>();
        private String DEFAULTURL = "http://image.tmdb.org/t/p/w185/";

        public ImageListAdapter(Context context) {
            super(context, R.layout.grid_item_layout);

            this.context = context;

            inflater = LayoutInflater.from(context);
        }

        @Override
        public void clear() {
            imageUrls = new ArrayList<>();
            this.notifyDataSetChanged();
        }

        @Override
        public void add(Object poster) {
            String posterTxt = (String) poster;
            imageUrls.add(posterTxt);
            this.notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return imageUrls.size();
        }

        @Override
        public String getItem(int position) {
            return imageUrls.get(position);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (null == convertView) {
                convertView = inflater.inflate(R.layout.grid_item_layout, parent, false);
            }

            Picasso
                    .with(context)
                    .load(DEFAULTURL + imageUrls.get(position))
                    .fit() // will explain later
                    .into((ImageView) convertView);

            return convertView;
        }
    }

}
