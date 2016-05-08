package com.eicke.github.playground.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.eicke.github.R;
import com.eicke.github.playground.GitHubPlaygroundApplication;
import com.eicke.github.playground.data.GitHubService;
import com.eicke.github.playground.data.entities.Owner;
import com.eicke.github.playground.data.entities.Repository;
import com.eicke.github.playground.ui.adapters.RepositoryRecyclerViewAdapter;
import com.eicke.github.playground.ui.fragments.UserFragment;
import com.eicke.github.playground.utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailActivity extends AppCompatActivity {

    private Owner mOwner;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout mCollapsingToolbar;
    @BindView(R.id.backdrop)
    ImageView mBackdropImageView;
    @BindView(R.id.usersRecyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.main_content)
    ViewGroup mMainContent;

    @Inject
    Picasso mPicasso;
    @Inject
    GitHubService mGitHubService;

    private RepositoryRecyclerViewAdapter mRecyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);
        ((GitHubPlaygroundApplication) getApplication()).getNetComponent().inject(this);

        Intent intent = getIntent();
        final Parcelable parceledOwner = intent.getParcelableExtra(UserFragment.KEY_OWNER);
        mOwner = Parcels.unwrap(parceledOwner);

        configureToolbar();
        configureRecyclerView();
        configureBackdrop();
        configureSwipeRefreshLayout();
    }

    private void configureToolbar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(mOwner.login);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void configureRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerViewAdapter = new RepositoryRecyclerViewAdapter(this, new ArrayList<Repository>());
        mRecyclerView.setAdapter(mRecyclerViewAdapter);
    }

    private void configureBackdrop() {
        mPicasso.load(mOwner.avatar_url)
                .fit()
                .centerCrop()
                .into(mBackdropImageView);
    }

    private void configureSwipeRefreshLayout() {
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchUserRepositories();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Orientation changes will utilize okHttp's cache
        fetchUserRepositories();
    }

    private void fetchUserRepositories() {
        if (NetworkUtils.isInternetConnected(this)) {
            //normal setRefreshing() call didn't work
            mSwipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    mSwipeRefreshLayout.setRefreshing(true);
                }
            });


            mGitHubService.listRepositories(mOwner.login).enqueue(new Callback<List<Repository>>() {
                @Override
                public void onResponse(Call<List<Repository>> call, Response<List<Repository>> response) {
                    mRecyclerViewAdapter.setNewRepositoryList(response.body());
                    mSwipeRefreshLayout.setRefreshing(false);
                }

                @Override
                public void onFailure(Call<List<Repository>> call, Throwable t) {
                    Log.d(MainActivity.TAG, "fetchUserRepositories " + t.getMessage());
                    mSwipeRefreshLayout.setRefreshing(false);
                    Snackbar.make(mMainContent, R.string.failureTryAgain, Snackbar.LENGTH_LONG).show();
                }
            });
        } else {
            Snackbar.make(mMainContent, R.string.noInternet, Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.searchRepositoriesFab)
    public void onSearchUsersClick(View v) {
        Snackbar.make(mMainContent, "Not yet implemented!", Snackbar.LENGTH_SHORT).show();
        //TODO implement search, might use RxJava + RxAndroid
    }
}
