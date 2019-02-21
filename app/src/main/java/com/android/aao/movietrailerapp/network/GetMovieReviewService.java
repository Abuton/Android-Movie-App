package com.android.aao.movietrailerapp.network;

/**
 * Created by A.A.O on 2/16/2019.
 */


import com.android.aao.movietrailerapp.model.MovieReviewPageResult;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface GetMovieReviewService {
    @GET("movie/{id}/reviews")
    Call<MovieReviewPageResult> getReviews(@Path("id") int movieId, @Query("api_key") String userkey);
}
