package com.mikeconroy.traveljournal.fragments.holidays;

import android.net.Uri;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mikeconroy.traveljournal.R;
import com.mikeconroy.traveljournal.db.Holiday;

import java.util.List;

public class HolidayRecyclerAdapter extends RecyclerView.Adapter<HolidayRecyclerAdapter.ViewHolder> {

    private final List<Holiday> mValues;
    private final HolidayListFragment.HolidayListInteractionListener mListener;

    public HolidayRecyclerAdapter(List<Holiday> items, HolidayListFragment.HolidayListInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_holiday_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        //TODO: Update this to display appropriate image.
        holder.mItem = mValues.get(position);

        String imagePath = mValues.get(position).getProfilePhotoPath();
        if(imagePath != null && !imagePath.equals("")) {
            Uri imageUri = Uri.parse(imagePath);
            holder.mImageView.setImageURI(imageUri);
        }
        holder.mTitle.setText(mValues.get(position).getTitle());
        holder.mStartDate.setText(mValues.get(position).getStartDate());
        holder.mEndDate.setText(mValues.get(position).getEndDate());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    Holiday item = holder.mItem;

                    mListener.onHolidayListItemInteraction(item);
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
        public final ImageView mImageView;
        public final TextView mTitle;
        public final TextView mStartDate;
        public final TextView mEndDate;


        public Holiday mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mImageView = view.findViewById(R.id.holiday_image);
            mTitle = view.findViewById(R.id.holiday_title);
            mStartDate = view.findViewById(R.id.holiday_start_date);
            mEndDate = view.findViewById(R.id.holiday_end_date);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mTitle.getText() + "'";
        }
    }
}
