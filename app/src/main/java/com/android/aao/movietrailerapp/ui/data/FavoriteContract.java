package com.android.aao.movietrailerapp.ui.data;

/**
 * Created by A.A.O on 2/16/2019.
 */


import android.net.Uri;
import android.provider.BaseColumns;

public class FavoriteContract {

    public static final String CONTENT_AUTHORITY = "com.android.aao.movietrailerapp";


    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final class FavoriteEntry implements BaseColumns {
        public static final String TABLE_NAME = "favorites";
        public static String MOVIE_ID = "movie_id";
        public static String MOVIE_TITLE = "movie_title";
        public static String MOVIE_OVERVIEW = "movie_overview";
        public static String MOVIE_VOTE_COUNT = "movie_vote_count";
        public static String MOVIE_VOTE_AVERAGE = "movie_vote_average";
        public static String MOVIE_RELEASE_DATE = "movie_release_date";
        public static String MOVIE_POSTER_PATH = "movie_poster_path";

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(TABLE_NAME)
                .build();

        public static Uri buildFavoriteUriWithId(long id) {
            return CONTENT_URI.buildUpon()
                    .appendPath(Long.toString(id))
                    .build();
        }
    }
}
