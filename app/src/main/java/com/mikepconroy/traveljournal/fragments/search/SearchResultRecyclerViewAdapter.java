package com.mikepconroy.traveljournal.fragments.search;

import android.net.Uri;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mikepconroy.traveljournal.R;

import java.util.List;

public class SearchResultRecyclerViewAdapter extends RecyclerView.Adapter<SearchResultRecyclerViewAdapter.ViewHolder> {

    private final List<SearchResultItem> mValues;
    private final SearchFragment.SearchListInteractionListener mListener;

    public SearchResultRecyclerViewAdapter(List<SearchResultItem> items, SearchFragment.SearchListInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_search_result_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);

        holder.typeView.setText(mValues.get(position).getType());
        holder.titleView.setText(mValues.get(position).getTitle());

        if(mValues.get(position).getImageLocation() != null && !mValues.get(position).getImageLocation().equals("")){
            holder.imageView.setImageURI(Uri.parse(mValues.get(position).getImageLocation()));
        }

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onSearchListInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView typeView;
        public final TextView titleView;
        public final ImageView imageView;
        public SearchResultItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            typeView = view.findViewById(R.id.item_type);
            titleView = view.findViewById(R.id.item_title);
            imageView = view.findViewById(R.id.item_image);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + typeView.getText() + " '" + titleView.getText() + "'";
        }
    }
}
