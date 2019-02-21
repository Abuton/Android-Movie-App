package com.android.aao.movietrailerapp.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.aao.movietrailerapp.BuildConfig;
import com.android.aao.movietrailerapp.R;
import com.android.aao.movietrailerapp.model.Movie;
import com.android.aao.movietrailerapp.model.MoviePageResult;
import com.android.aao.movietrailerapp.network.GetMovieDataService;
import com.android.aao.movietrailerapp.network.RetrofitInstance;
import com.android.aao.movietrailerapp.ui.adapter.MovieAdapter;
import com.android.aao.movietrailerapp.ui.data.FavoriteContract;
import com.android.aao.movietrailerapp.ui.utils.EndlessRecyclerViewScrollListener;
import com.android.aao.movietrailerapp.ui.utils.MovieClickListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity {
    public static final String API_KEY = BuildConfig.API_KEY;
    private static final int FIRST_PAGE = 1;
    private int totalPages;
    private int currentSortMode = 1;
    private Call<MoviePageResult> call;
    private List<Movie> movieResults;
    private MovieAdapter movieAdapter;

    @BindView(R.id.rv_movies) RecyclerView recyclerView;
    @BindView(R.id.tv_no_internet_error) ConstraintLayout mNoInternetMessage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        if(!isNetworkAvailable()){
            recyclerView.setVisibility(View.GONE);
            mNoInternetMessage.setVisibility(View.VISIBLE);
        }

        GridLayoutManager manager = new GridLayoutManager(this, 2);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return 1;
            }
        });
        recyclerView.setLayoutManager(manager);
        EndlessRecyclerViewScrollListener scrollListener = new EndlessRecyclerViewScrollListener(manager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                if ((page + 1) <= totalPages && currentSortMode != 3) {
                    loadPage(page + 1);
                }
            }
        };

        recyclerView.addOnScrollListener(scrollListener);

        loadPage(FIRST_PAGE);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.sort_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //SortID 1 -> Popularity
        //SortID 2 -> Top rated
        switch (item.getItemId()) {
            case R.id.sort_by_popularity:
                currentSortMode = 1;
                break;
            case R.id.sort_by_top:
                currentSortMode = 2;
                break;
            case R.id.sort_by_favorites:
                currentSortMode = 3;
                break;
        }
        if(currentSortMode != 3){
            loadPage(FIRST_PAGE);
        } else {
            ArrayList<Movie> favoriteMovies = getFavoriteMovies();

            movieAdapter = new MovieAdapter(favoriteMovies, new MovieClickListener() {
                @Override
                public void onMovieClick(Movie movie) {
                    Intent intent = new Intent(MainActivity.this, MovieActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("movie", movie);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });
            recyclerView.setAdapter(movieAdapter);
        }

        return super.onOptionsItemSelected(item);

    }

    private void loadPage(final int page) {
        GetMovieDataService movieDataService = RetrofitInstance.getRetrofitInstance().create(GetMovieDataService.class);

        switch(currentSortMode){
            case 1:
                call = movieDataService.getPopularMovies(page, API_KEY);
                break;
            case 2:
                call = movieDataService.getTopRatedMovies(page, API_KEY);
                break;
        }

        call.enqueue(new Callback<MoviePageResult>() {
            @Override
            public void onResponse(@NonNull Call<MoviePageResult> call, @NonNull Response<MoviePageResult> response) {

                if(page == 1) {
                    assert response.body() != null;
                    movieResults = response.body().getMovieResult();
                    assert response.body() != null;
                    totalPages = response.body().getTotalPages();

                    movieAdapter = new MovieAdapter(movieResults, new MovieClickListener() {
                        @Override
                        public void onMovieClick(Movie movie) {
                            Intent intent = new Intent(MainActivity.this, MovieActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("movie", movie);
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }
                    });
                    recyclerView.setAdapter(movieAdapter);
                } else {
                    assert response.body() != null;
                    List<Movie> movies = response.body().getMovieResult();
                    for(Movie movie : movies){
                        movieResults.add(movie);
                        movieAdapter.notifyItemInserted(movieResults.size() - 1);
                    }
                }

            }

            @Override
            public void onFailure(@NonNull Call<MoviePageResult> call, @NonNull Throwable t) {
                Toast.makeText(MainActivity.this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }
    public static int getMeasuredPosterHeight(int width) {
        return (int) (width * 1.5f);
    }

    public static String movieImagePathBuilder(String imagePath) {
        return "https://image.tmdb.org/t/p/" +
                "w500" +
                imagePath;
    }

    private ArrayList<Movie> getFavoriteMovies(){
        ArrayList<Movie> movieList = new ArrayList<>();
        Cursor cursor = getContentResolver()
                .query(FavoriteContract.FavoriteEntry.CONTENT_URI,null,null,null,null);

        assert cursor != null;
        if (cursor.moveToFirst()){
            do{
                Movie movie = new Movie();


                // List of things to be returned by TMDB
                int id = cursor.getInt(cursor.getColumnIndex("movie_id"));
                String movieTitle = cursor.getString(cursor.getColumnIndex("movie_title"));
                String movieOverview = cursor.getString(cursor.getColumnIndex("movie_overview"));
                double movieVoteAverage = cursor.getDouble(cursor.getColumnIndex("movie_vote_average"));
                String movieReleaseDate = cursor.getString(cursor.getColumnIndex("movie_release_date"));
                String moviePosterPath = cursor.getString(cursor.getColumnIndex("movie_poster_path"));
                String movieOriginalTitle = cursor.getString(cursor.getColumnIndex("movie_original_title"));


                movie.setId(id);
                movie.setTitle(movieTitle);
                movie.setOverview(movieOverview);
                movie.setVoteAverage(movieVoteAverage);
                movie.setReleaseDate(movieReleaseDate);
                movie.setPosterPath(moviePosterPath);
                movie.setOriginalTitle(movieOriginalTitle);


                movieList.add(movie);

            }while(cursor.moveToNext());
        }
        cursor.close();

        return movieList;
    }

    // Checking for network
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    // Btn Refresh Clicked
    @OnClick(R.id.tv_no_internet_error_refresh)
    public void refreshActivity(){
        finish();
        startActivity(getIntent());
    }

}