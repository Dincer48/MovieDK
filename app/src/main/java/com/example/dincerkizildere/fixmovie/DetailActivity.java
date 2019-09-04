package com.example.dincerkizildere.fixmovie;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Movie;
import android.net.Uri;
import android.os.Bundle;

import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;



import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.dincerkizildere.fixmovie.data.APIUser;
import com.example.dincerkizildere.fixmovie.data.OnGetTrailersCallback;
import com.example.dincerkizildere.fixmovie.database.FVColumn;
import com.example.dincerkizildere.fixmovie.database.FVHelper;
import com.example.dincerkizildere.fixmovie.detail.ItemResult;
import com.example.dincerkizildere.fixmovie.detail.Language;
import com.example.dincerkizildere.fixmovie.detail.ModelDetail;
import com.example.dincerkizildere.fixmovie.function.Trailer;
import com.example.dincerkizildere.fixmovie.service.DateTime;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import com.google.gson.Gson;



import java.text.NumberFormat;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.dincerkizildere.fixmovie.database.DatabaseContact.CONTENT_URI;

public class DetailActivity extends AppCompatActivity {

    public static final String MOVIE_ITEM = "movie_item";

    private static String YOUTUBE_VIDEO_URL = "http://www.youtube.com/watch?v=%s";
    private static String YOUTUBE_THUMBNAIL_URL = "http://img.youtube.com/vi/%s/0.jpg";

    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsing_toolbar;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.tv_title)
    TextView tv_title;

    @BindView(R.id.img_backdrop)
    ImageView img_backdrop;

    @BindView(R.id.img_poster)
    ImageView img_poster;

    @BindView(R.id.tv_release_date)
    TextView tv_release_date;

    @BindView(R.id.tv_vote)
    TextView tv_vote;

    @BindViews({
            R.id.img_star1,
            R.id.img_star2,
            R.id.img_star3,
            R.id.img_star4,
            R.id.img_star5
    })
    List<ImageView> img_vote;

    @BindView(R.id.tv_genres)
    TextView tv_genres;

    @BindView(R.id.tv_overview)
    TextView tv_overview;

    @BindView(R.id.img_poster_belongs)
    ImageView img_poster_belongs;

    @BindView(R.id.tv_title_belongs)
    TextView tv_title_belongs;

    @BindView(R.id.tv_budget)
    TextView tv_budget;

    @BindView(R.id.tv_revenue)
    TextView tv_revenue;

    @BindView(R.id.tv_companies)
    TextView tv_companies;

    @BindView(R.id.tv_countries)
    TextView tv_countries;

    @BindView(R.id.iv_fav)
    ImageView iv_fav;

    @BindView(R.id.movieTrailers)
    LinearLayout movieTrailers;

    @BindView(R.id.trailersLabel)
    TextView trailersLabel;








    private Call<ModelDetail> apiCall;
    private APIUser apiUser = new APIUser();
    private Gson gson = new Gson();

    private ItemResult item;
    private FVHelper FVHelper;
    private Boolean isFavorite = false;
    private ItemResult movie;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);



        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);




        collapsing_toolbar.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));
        String json = getIntent().getStringExtra(MOVIE_ITEM);
        item = gson.fromJson(json, ItemResult.class);
        loadData();

        iv_fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isFavorite) FavoriteRemove();
                else FavoriteSave();

                isFavorite = !isFavorite;
                favoriteSet();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (apiCall != null) apiCall.cancel();
        if (FVHelper != null) FVHelper.close();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void favoriteSet() {
        if (isFavorite) iv_fav.setImageResource(R.drawable.ic_favorite);
        else iv_fav.setImageResource(R.drawable.ic_favorite_border);
    }

    private void loadData() {
        loadDataSQLite();
        loadDataInServer(String.valueOf(item.getId()));

        getSupportActionBar().setTitle(item.getTitle());
        tv_title.setText(item.getTitle());

        Glide.with(this)
                .load(BuildConfig.BASE_URL_IMG + "w342" + item.getBackdropPath())
                .into(img_backdrop);

        Glide.with(this)
                .load(BuildConfig.BASE_URL_IMG + "w154" + item.getPosterPath())
                .into(img_poster);

        tv_release_date.setText(DateTime.getLongDate(item.getReleaseDate()));
        tv_vote.setText(String.valueOf(item.getVoteAverage()));
        tv_overview.setText(item.getOverview());





        double userRating = item.getVoteAverage() / 2;
        int integerPart = (int) userRating;

        // Fill stars
        for (int i = 0; i < integerPart; i++) {
            img_vote.get(i).setImageResource(R.drawable.ic_star_black_24dp);
        }

        // Fill half star
        if (Math.round(userRating) > integerPart) {
            img_vote.get(integerPart).setImageResource(R.drawable.ic_star_half_black_24dp);
        }

    }



        private void loadDataSQLite() {
        FVHelper = new FVHelper(this);
        FVHelper.open();

        Cursor cursor = getContentResolver().query(
                Uri.parse(CONTENT_URI + "/" + item.getId()),
                null,
                null,
                null,
                null
        );

        if (cursor != null) {
            if (cursor.moveToFirst()) isFavorite = true;
            cursor.close();
        }
        favoriteSet();
    }

    private void loadDataInServer(String movie_item) {
        apiCall = apiUser.getService().getDetailMovie(movie_item, Language.getCountry());
        apiCall.enqueue(new Callback<ModelDetail>() {
            @Override
            public void onResponse(Call<ModelDetail> call, Response<ModelDetail> response) {
                if (response.isSuccessful()) {
                    ModelDetail item = response.body();


                    int size = 0;

                    String genres = "";
                    size = item.getGenres().size();
                    for (int i = 0; i < size; i++) {
                        genres += "√ " + item.getGenres().get(i).getName() + (i + 1 < size ? "\n" : "");
                    }
                    tv_genres.setText(genres);

                    if (item.getCollection() != null) {
                        Glide.with(DetailActivity.this)
                                .load(BuildConfig.BASE_URL_IMG + "w92" + item.getCollection().getPosterPath())
                                .into(img_poster_belongs);

                        tv_title_belongs.setText(item.getCollection().getName());
                    }

                    tv_budget.setText("$ " + NumberFormat.getIntegerInstance().format(item.getBudget()));
                    tv_revenue.setText("$ " + NumberFormat.getIntegerInstance().format(item.getRevenue()));

                    String companies = "";
                    size = item.getProductionCompanies().size();
                    for (int i = 0; i < size; i++) {
                        companies += "√ " + item.getProductionCompanies().get(i).getName() + (i + 1 < size ? "\n" : "");
                    }
                    tv_companies.setText(companies);


                    String countries = "";
                    size = item.getProductionCountries().size();
                    for (int i = 0; i < size; i++) {
                        countries += "√ " + item.getProductionCountries().get(i).getName() + (i + 1 < size ? "\n" : "");
                    }
                    tv_countries.setText(countries);


                } else loadFailed();
            }



            @Override
            public void onFailure(Call<ModelDetail> call, Throwable t) {
                loadFailed();
            }
        });
    }


    private void loadFailed() {
        Toast.makeText(this, R.string.err_load_failed, Toast.LENGTH_SHORT).show();
    }

    private void FavoriteSave() {
        //Log.d("TAG", "FavoriteSave: " + item.getId());
        ContentValues cv = new ContentValues();
        cv.put(FVColumn.COLUMN_ID, item.getId());
        cv.put(FVColumn.COLUMN_TITLE, item.getTitle());
        cv.put(FVColumn.COLUMN_BACKDROP, item.getBackdropPath());
        cv.put(FVColumn.COLUMN_POSTER, item.getPosterPath());
        cv.put(FVColumn.COLUMN_RELEASE_DATE, item.getReleaseDate());
        cv.put(FVColumn.COLUMN_VOTE, item.getVoteAverage());
        cv.put(FVColumn.COLUMN_OVERVIEW, item.getOverview());

        getContentResolver().insert(CONTENT_URI, cv);
        Toast.makeText(this, R.string.cv_save, Toast.LENGTH_SHORT).show();
    }
    private void getTrailers() {
        apiUser.getTrailers(movie.getId(), new OnGetTrailersCallback() {
            @Override
            public void onSuccess(List<Trailer> trailers) {
                trailersLabel.setVisibility(View.VISIBLE);
                movieTrailers.removeAllViews();
                for (final Trailer trailer : trailers) {
                    View parent = getLayoutInflater().inflate(R.layout.thumbnail_trailer, movieTrailers, false);
                    ImageView thumbnail = parent.findViewById(R.id.thumbnail);
                    thumbnail.requestLayout();
                    thumbnail.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showTrailer(String.format(YOUTUBE_VIDEO_URL, trailer.getKey()));
                        }
                    });
                    Glide.with(DetailActivity.this)
                            .load(String.format(YOUTUBE_THUMBNAIL_URL, trailer.getKey()))
                            .apply(RequestOptions.placeholderOf(R.color.colorPrimary).centerCrop())
                            .into(thumbnail);
                    movieTrailers.addView(parent);
                }
            }

            @Override
            public void onError() {
                // Do nothing
                trailersLabel.setVisibility(View.GONE);
            }
        });
    }

    private void showTrailer(String video_url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(video_url));
        startActivity(intent);
    }


    private void FavoriteRemove() {
        getContentResolver().delete(
                Uri.parse(CONTENT_URI + "/" + item.getId()),
                null,
                null
        );
        Toast.makeText(this, R.string.cv_remove, Toast.LENGTH_SHORT).show();
    }

}