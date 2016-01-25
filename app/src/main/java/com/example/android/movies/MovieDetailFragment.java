package com.example.android.movies;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailFragment extends Fragment {

    private String DEFAULTURL = "http://image.tmdb.org/t/p/w500/";

    public MovieDetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);


        Bundle extras = getActivity().getIntent().getExtras();
        MovieObject movieObject;
        if(extras == null) {
            movieObject= null;
            return null;
        } else {
            movieObject= (MovieObject) extras.getSerializable(MovieObject.class.getSimpleName());
        }

        TextView titleTextView = (TextView) rootView.findViewById(R.id.title_textview);
        titleTextView.setText(movieObject.originalTitle);
        TextView overviewTextView = (TextView) rootView.findViewById(R.id.overview_textview);
        overviewTextView.setText(movieObject.overview);
        TextView releaseTextView = (TextView) rootView.findViewById(R.id.releasedate_textview);
        releaseTextView.setText(movieObject.releaseDate);
        TextView ratingTextView = (TextView) rootView.findViewById(R.id.rating_textview);
        ratingTextView.setText(movieObject.voteAverage);

        ImageView imageView = (ImageView) rootView.findViewById(R.id.poster_imageview);
        Picasso.with(getContext())
                .load(DEFAULTURL + movieObject.poster)
                .into(imageView);


        return rootView;
    }
}
