package com.android.aao.movietrailerapp.network;

import com.android.aao.movietrailerapp.model.MovieTrailerPageResult;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by A.A.O on 2/16/2019.
 */

public interface GetMovieTrailerService {

    @GET("movie/{id}/videos")
    Call<MovieTrailerPageResult> getTrailers(@Path("id") int movieId, @Query("api_key") String userKey);
}
