package com.eicke.github.playground.ui.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.eicke.github.R;
import com.eicke.github.playground.GitHubPlaygroundApplication;
import com.eicke.github.playground.data.entities.Repository;
import com.eicke.github.playground.ui.transformations.CircleStrokeTransformation;
import com.squareup.picasso.Picasso;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;


public class RepositoryRecyclerViewAdapter extends RecyclerView.Adapter<RepositoryRecyclerViewAdapter.ViewHolder> {

    @Inject
    Picasso picasso;

    private List<Repository> mItems;
    private final CircleStrokeTransformation avatarTransformation;

    public RepositoryRecyclerViewAdapter(Context context, List<Repository> items) {
        GitHubPlaygroundApplication.getApplication().getNetComponent().inject(this);

        mItems = items;
        avatarTransformation = new CircleStrokeTransformation(context, ContextCompat.getColor(context, R.color.avatar_stroke), 1);
    }

    public void setNewRepositoryList(List<Repository> items) {
        mItems = items;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.repository_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Repository repository = mItems.get(position);

        picasso.load(repository.owner.avatar_url)
                .placeholder(R.drawable.avatar)
                .fit()
                .transform(avatarTransformation)
                .into(holder.avatar);

        holder.name.setText(repository.name);
        holder.description.setText(repository.description);
        holder.stars.setText(String.valueOf(repository.watchers));
        holder.forks.setText(String.valueOf(repository.forks));
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.trending_repository_avatar)
        ImageView avatar;
        @BindView(R.id.trending_repository_name)
        TextView name;
        @BindView(R.id.trending_repository_description)
        TextView description;
        @BindView(R.id.trending_repository_stars)
        TextView stars;
        @BindView(R.id.trending_repository_forks)
        TextView forks;

        public final View mView;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            mView = view;
        }
    }
}
