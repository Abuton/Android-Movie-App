package com.android.aao.movietrailerapp.ui.activity;

/**
 * Created by A.A.O on 2/16/2019.
 */


import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import com.android.aao.movietrailerapp.R;
import com.android.aao.movietrailerapp.model.MovieReview;
import com.android.aao.movietrailerapp.ui.adapter.ReviewAdapter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

@SuppressWarnings("unchecked")
public class ReviewActivity extends AppCompatActivity {

    @BindView(R.id.rv_movie_reviews)
    RecyclerView mReviewsRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        ButterKnife.bind(this);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        assert bundle != null;
        ArrayList<MovieReview> mMovieReviews = (ArrayList<MovieReview>) bundle.getSerializable("reviews");
        String mMovieTitle = bundle.getString("movie_title");

        setTitle(mMovieTitle + getString(R.string.review_activity_title));

        ReviewAdapter mReviewsAdapter = new ReviewAdapter(mMovieReviews);
        mReviewsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mReviewsRecyclerView.setAdapter(mReviewsAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

}
