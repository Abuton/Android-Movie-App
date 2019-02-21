package com.android.aao.movietrailerapp.network;

import com.android.aao.movietrailerapp.model.MoviePageResult;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by A.A.O on 2/16/2019.
 */

@SuppressWarnings("ALL")
public interface GetMovieDataService {

    @GET("movie/popular")
    Call<MoviePageResult> getPopularMovies(@Query("page") int page, @Query("api_key") String userKey);

    @GET("movie/top_rated")
    Call<MoviePageResult> getTopRatedMovies(@Query("page") int page, @Query("api_key") String userKey);
}
