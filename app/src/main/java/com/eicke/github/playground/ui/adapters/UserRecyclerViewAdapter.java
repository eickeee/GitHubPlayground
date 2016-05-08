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
import com.eicke.github.playground.data.entities.Owner;
import com.eicke.github.playground.ui.transformations.CircleStrokeTransformation;
import com.squareup.picasso.Picasso;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;


public class UserRecyclerViewAdapter extends RecyclerView.Adapter<UserRecyclerViewAdapter.ViewHolder> {

    @Inject
    Picasso picasso;

    private List<Owner> mItems;
    private final CircleStrokeTransformation avatarTransformation;
    private OnClickListener mListener;

    public UserRecyclerViewAdapter(Context context, List<Owner> items, OnClickListener listener) {
        GitHubPlaygroundApplication.getApplication().getNetComponent().inject(this);

        mListener = listener;
        mItems = items;
        avatarTransformation = new CircleStrokeTransformation(context, ContextCompat.getColor(context, R.color.avatar_stroke), 1);
    }

    public void setNewUserList(List<Owner> items) {
        mItems = items;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Owner owner = mItems.get(position);

        picasso.load(owner.avatar_url)
                .placeholder(R.drawable.user_placeholder)
                .fit()
                .transform(avatarTransformation)
                .into(holder.avatar);

        holder.name.setText(owner.login);
        holder.url.setText(owner.url);
        holder.score.setText(String.valueOf(owner.score));
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.onRecyclerViewItemClick(owner);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.users_avatar)
        ImageView avatar;
        @BindView(R.id.users_name)
        TextView name;
        @BindView(R.id.users_url)
        TextView url;
        @BindView(R.id.users_score)
        TextView score;

        public final View mView;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            mView = view;
        }
    }

    public interface OnClickListener {

        void onRecyclerViewItemClick(Owner owner);
    }
}
