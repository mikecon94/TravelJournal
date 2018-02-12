package com.mikepconroy.traveljournal.fragments.holidays;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mikepconroy.traveljournal.R;
import com.mikepconroy.traveljournal.model.db.Holiday;

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
        holder.mImageView.setImageResource(R.drawable.photo_not_found);
        holder.mTitle.setText(mValues.get(position).getTitle());
        holder.mStartDate.setText(mValues.get(position).getStartDate());
        holder.mEndDate.setText(mValues.get(position).getEndDate());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    Holiday item = holder.mItem;
                    mListener.onListFragmentInteraction(item);
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
