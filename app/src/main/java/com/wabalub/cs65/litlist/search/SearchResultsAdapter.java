package com.wabalub.cs65.litlist.search;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.wabalub.cs65.litlist.MainActivity;
import com.wabalub.cs65.litlist.PlayerService;
import com.wabalub.cs65.litlist.R;
import com.wabalub.cs65.litlist.gson.Song;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.models.Image;
import kaaes.spotify.webapi.android.models.Track;

public class SearchResultsAdapter extends RecyclerView.Adapter<SearchResultsAdapter.ViewHolder> {

    private final List<Track> mItems = new ArrayList<>();
    private final Context mContext;
    private final ItemSelectedListener mListener;
    private static final String TAG = "SEARCH_ADAPTER";

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public final TextView title;
        public final TextView subtitle;
        public final ImageView image;
        public final Button addBtn;

        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.entity_title);
            subtitle = itemView.findViewById(R.id.entity_subtitle);
            image = itemView.findViewById(R.id.entity_image);
            addBtn = itemView.findViewById(R.id.add_button);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            notifyItemChanged(getLayoutPosition());
            mListener.onItemSelected(v, mItems.get(getAdapterPosition()));
        }
    }

    public interface ItemSelectedListener {
        void onItemSelected(View itemView, Track item);
    }

    public SearchResultsAdapter(Context context, ItemSelectedListener listener) {
        mContext = context;
        mListener = listener;
    }

    public void clearData() {
        mItems.clear();
    }

    public void addData(List<Track> items) {
        mItems.addAll(items);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_search_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Track item = mItems.get(position);

        holder.title.setText(item.name);


        holder.subtitle.setText(PlayerService.namesToString(item.artists));

        Image image = item.album.images.get(0);
        if (image != null) {
            Picasso.with(mContext).load(image.url).into(holder.image);
        }

        holder.addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(MainActivity.playlist == null) {
                    Log.e(TAG, "Playlist is null");
                }

                // TODO add to global playlist
                // add to the local playlist
                Song song = new Song(item.id);
                MainActivity.playlist.songs.add(song);

                Log.d(TAG,"Added " + item.name);
                Intent intent = new Intent(mContext, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("tab_position", 1);
                intent.putExtra(MainActivity.EXTRA_TOKEN, MainActivity.token);
                mContext.startActivity(intent);

            }
        });
    }



    @Override
    public int getItemCount() {
        return mItems.size();
    }

}
