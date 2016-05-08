package com.eicke.github.playground.dagger;

import com.eicke.github.playground.ui.DetailActivity;
import com.eicke.github.playground.ui.adapters.RepositoryRecyclerViewAdapter;
import com.eicke.github.playground.ui.adapters.UserRecyclerViewAdapter;
import com.eicke.github.playground.ui.fragments.UserFragment;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AppModule.class, NetModule.class})
public interface NetComponent {
    void inject(DetailActivity detailActivity);

    void inject(UserFragment fragment);

    void inject(RepositoryRecyclerViewAdapter adapter);

    void inject(UserRecyclerViewAdapter adapter);


}