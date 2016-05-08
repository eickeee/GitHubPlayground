package com.eicke.github.playground.ui.fragments;


import android.support.v4.app.Fragment;

import com.eicke.github.playground.GitHubPlaygroundApplication;
import com.squareup.leakcanary.RefWatcher;

public abstract class BaseFragment extends Fragment {


    @Override
    public void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = GitHubPlaygroundApplication.getRefWatcher(getActivity());
        refWatcher.watch(this);
    }
}

