package com.udacity.quakereport;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Earthquake>> {

    private static final String USGS_REQUEST_URL =
            "https://earthquake.usgs.gov/fdsnws/event/1/query";

    private static final int EARTHQUAKE_LOADER_ID = 1;

    private EarthquakeAdapter mAdapter;

    private TextView mEmptyView;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView listView = findViewById(R.id.list);
        mAdapter = new EarthquakeAdapter(MainActivity.this, new ArrayList<Earthquake>());
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Earthquake earthquake = mAdapter.getItem(position);
                if (earthquake != null) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(earthquake.getUrl()));
                    startActivity(intent);
                }
            }
        });

        mEmptyView = findViewById(R.id.empty_view);
        mProgressBar = findViewById(R.id.progress_bar);

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        if (isConnected) {
            // SOS: Theoretically, after rotation, the same data is displayed (no downloading), whereas
            // if I leave the app and return, new data is loaded so we can have fresh data. In practice,
            // read comments in WhoWroteIt app...
            getSupportLoaderManager().initLoader(EARTHQUAKE_LOADER_ID, null, this);
            mProgressBar.setVisibility(View.VISIBLE);
        } else {
            mEmptyView.setText(R.string.no_connection);
            mEmptyView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @NonNull
    @Override
    public Loader<List<Earthquake>> onCreateLoader(int i, @Nullable Bundle bundle) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String minMagnitude = prefs.getString(
                getString(R.string.min_magnitude_pref_key),
                getString(R.string.min_magnitude_default_value));

        String orderBy = prefs.getString(
                getString(R.string.order_by_pref_key),
                getString(R.string.order_by_default_value));

        Uri baseUri = Uri.parse(USGS_REQUEST_URL);
        String urlString = baseUri.buildUpon()
                .appendQueryParameter("format", "geojson")
                .appendQueryParameter("limit", "10")
                .appendQueryParameter("minmag", minMagnitude)
                .appendQueryParameter("orderby", orderBy)
                .toString();
        return new EarthQuakeLoader(this, urlString);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<Earthquake>> loader, List<Earthquake> data) {
        mProgressBar.setVisibility(View.GONE);

        mAdapter.clear();

        if (data != null && !data.isEmpty()) {
            mAdapter.addAll(data);
            mEmptyView.setVisibility(View.GONE);
        } else {
            mEmptyView.setText(R.string.no_earthquakes_found);
            mEmptyView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<Earthquake>> loader) {
        mAdapter.clear();
    }

    private static class EarthQuakeLoader extends AsyncTaskLoader<List<Earthquake>> {

        private final String mUrl;

        EarthQuakeLoader(@NonNull Context context, String url) {
            super(context);
            mUrl = url;
        }

        @Override
        protected void onStartLoading() {
            forceLoad();
        }

        @Nullable
        @Override
        public List<Earthquake> loadInBackground() {
            if (TextUtils.isEmpty(mUrl)) {
                return null;
            }

            return QueryUtils.fetchEarthquakeData(mUrl);
        }
    }
}
