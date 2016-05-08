package com.eicke.github.playground.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.eicke.github.R;
import com.eicke.github.playground.GitHubPlaygroundApplication;
import com.eicke.github.playground.data.GitHubService;
import com.eicke.github.playground.data.entities.Owner;
import com.eicke.github.playground.data.entities.UserSearchResponse;
import com.eicke.github.playground.ui.DetailActivity;
import com.eicke.github.playground.ui.MainActivity;
import com.eicke.github.playground.ui.adapters.UserRecyclerViewAdapter;
import com.eicke.github.playground.utils.NetworkUtils;

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

public class UserFragment extends BaseFragment implements UserRecyclerViewAdapter.OnClickListener {

    private static final String KEY_LAST_SEARCH_ITEMS = "KEY_LAST_SEARCH_ITEMS";
    public static final String KEY_OWNER = "KEY_OWNER";

    @BindView(R.id.usersList)
    RecyclerView mRecyclerView;
    @BindView(R.id.inputUsername)
    AppCompatEditText userNameEditText;
    @BindView(R.id.searchUsersFab)
    FloatingActionButton searchingFab;
    @BindView(R.id.main_content)
    ViewGroup mMainContent;

    @Inject
    GitHubService mGitHubService;

    private UserRecyclerViewAdapter mRecyclerViewAdapter;
    private List<Owner> mLastFetchedItems;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_list, container, false);
        ButterKnife.bind(this, view);
        GitHubPlaygroundApplication.getApplication().getNetComponent().inject(this);

        configureRecyclerView();

        if (savedInstanceState != null) {
            /**
             * preserving the search word and leveraging okHttp's cache mechanism would have been an option as well
             */
            final Parcelable parceledList = savedInstanceState.getParcelable(KEY_LAST_SEARCH_ITEMS);
            if (parceledList != null) {
                mLastFetchedItems = Parcels.unwrap(parceledList);
                mRecyclerViewAdapter.setNewUserList(mLastFetchedItems);
            }
        }
        return view;
    }

    private void configureRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerViewAdapter = new UserRecyclerViewAdapter(getActivity(), new ArrayList<Owner>(), this);
        mRecyclerView.setAdapter(mRecyclerViewAdapter);
    }

    @OnClick(R.id.searchUsersFab)
    public void onSearchButtonClick(final View v) {
        final String searchWord = userNameEditText.getText().toString();

        if (NetworkUtils.isInternetConnected(getActivity())) {
            if (!TextUtils.isEmpty(searchWord)) {
                hideKeyboard();
                final MainActivity mainActivity = (MainActivity) getActivity();
                mainActivity.showNetworkLoading(true);
                v.setEnabled(false);
                mGitHubService.searchUsers(searchWord).enqueue(new Callback<UserSearchResponse>() {
                    @Override
                    public void onResponse(Call<UserSearchResponse> call, Response<UserSearchResponse> response) {
                        mLastFetchedItems = response.body().items;
                        mRecyclerViewAdapter.setNewUserList(mLastFetchedItems);

                        if (mLastFetchedItems.size() == 0)
                            Snackbar.make(mMainContent, "No results found!", Snackbar.LENGTH_LONG).show();

                        v.setEnabled(true);
                        mainActivity.showNetworkLoading(false);
                    }

                    @Override
                    public void onFailure(Call<UserSearchResponse> call, Throwable t) {
                        v.setEnabled(true);
                        mainActivity.showNetworkLoading(false);
                        Log.e(MainActivity.TAG, "fetchUserdata: ", t);
                        Snackbar.make(mMainContent, R.string.failureTryAgain, Snackbar.LENGTH_LONG).show();
                    }
                });
            }
        } else {
            Snackbar.make(mMainContent, R.string.noInternet, Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(KEY_LAST_SEARCH_ITEMS, Parcels.wrap(mLastFetchedItems));
    }

    @Override
    public void onRecyclerViewItemClick(Owner owner) {
        Intent intent = new Intent(getActivity(), DetailActivity.class);
        intent.putExtra(KEY_OWNER, Parcels.wrap(owner));
        startActivity(intent);
    }

    public void hideKeyboard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
