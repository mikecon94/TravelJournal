package com.mikepconroy.traveljournal.fragments.photos;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.mikepconroy.traveljournal.R;
import com.mikepconroy.traveljournal.fragments.photos.PhotoListFragment.OnPhotoListInteractionListener;
import com.mikepconroy.traveljournal.model.db.Photo;

import android.net.Uri;
import java.net.URISyntaxException;
import java.util.List;

public class PhotoRecyclerViewAdapter extends RecyclerView.Adapter<PhotoRecyclerViewAdapter.ViewHolder> {

    private final List<Photo> mValues;
    private final OnPhotoListInteractionListener mListener;

    public PhotoRecyclerViewAdapter(List<Photo> items, OnPhotoListInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_photo_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mImage.setImageURI(Uri.parse(holder.mItem.getImageUri()));

       // holder.mImage.setImageResource(R.drawable.photo_not_found);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onPhotoListItemInteraction(holder.mItem);
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
        public final ImageView mImage;
        public Photo mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;

            mImage = view.findViewById(R.id.image_list_item);
        }

        @Override
        public String toString() {
            return super.toString();
        }
    }
}
