package com.example.yelpsearch.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewTreeViewModelKt;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.yelpsearch.DetailActivity;
import com.example.yelpsearch.MainActivity;
import com.example.yelpsearch.model.Business;
import com.example.yelpsearch.R;
import com.example.yelpsearch.model.Reservation;
import com.example.yelpsearch.model.Review;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RecyclerViewAdapter extends RecyclerView.Adapter {

    private final Context mContext;
    private final List list;
    RequestOptions option;

    public RecyclerViewAdapter(Context mContext, List list) {
        this.mContext = mContext;
        this.list = list;

        //Request option for Glide
        option = new RequestOptions().centerCrop().placeholder(R.drawable.loading_shape).error(R.drawable.loading_shape);
    }

    @Override
    public int getItemViewType(int position) {
        if(Reservation.class.isInstance(list.get(position))) {
            return 0;
        } else if(Review.class.isInstance(list.get(position))) {
            return 1;
        } else {
            return 2;
        }
    }

    public void clear() {
        int size = list.size();
        list.clear();
        notifyItemRangeRemoved(0, size);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (this.getItemViewType(viewType) == 0) {
            View view;
            LayoutInflater inflater = LayoutInflater.from(mContext);
            view = inflater.inflate(R.layout.reservation_row_item, parent, false);
            ReservationHolder reservationHolder = new ReservationHolder(view);

            return reservationHolder;

        } else if (this.getItemViewType(viewType) == 1) {
            View view;
            LayoutInflater inflater = LayoutInflater.from(mContext);
            view = inflater.inflate(R.layout.review_row_item, parent, false);
            ReviewHolder reviewHolder = new ReviewHolder(view);

            return reviewHolder;
        } else {
            View view;
            LayoutInflater inflater = LayoutInflater.from(mContext);
            view = inflater.inflate(R.layout.business_row_item, parent, false);
            BusinessHolder businessHolder = new BusinessHolder(view);

            businessHolder.business_detail_row.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(mContext, DetailActivity.class);
                    intent.putExtra("business_id", businessHolder.business_id.getText());
                    intent.putExtra("business_name", businessHolder.business_name.getText());

                    //intent.putExtra("business_name", list.get(businessHolder.getAdapterPosition()).getName());
                    mContext.startActivity(intent);
                }
            });

            return businessHolder;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (this.getItemViewType(position) == 0) {
            Reservation reservation = (Reservation)list.get(position);
            ((ReservationHolder) holder).reservation_index.setText(String.valueOf(reservation.getIndex()));
            ((ReservationHolder) holder).res_name_row.setText(reservation.getBusiness_name());
            ((ReservationHolder) holder).res_date_row.setText(reservation.getDate());
            ((ReservationHolder) holder).res_time_row.setText(reservation.getTime());
            ((ReservationHolder) holder).res_email_row.setText(reservation.getEmail());

        } else if (this.getItemViewType(position) == 1) {
            Review review = (Review)list.get(position);
            ReviewHolder reviewHolder = (ReviewHolder) holder;

            ((ReviewHolder) holder).user_name.setText(review.getUser_name());
            ((ReviewHolder) holder).rating.setText(review.getRating());
            ((ReviewHolder) holder).review_text.setText(review.getText());
            ((ReviewHolder) holder).date_created.setText(review.getTime_created());

        } else {
            Business business = (Business) list.get(position);
            BusinessHolder businessHolder = (BusinessHolder) holder;

            businessHolder.index.setText(business.getTable_index());
            businessHolder.business_name.setText(business.getName());
            businessHolder.business_distance.setText(business.getDistance_miles());
            businessHolder.business_rating.setText(business.getRating());
            businessHolder.business_id.setText(business.getBusiness_id());

            //Load image from internet and set it into ImageView using Glide
            Glide.with(mContext).load(((Business) list.get(position)).getImage_url()).apply(option).into(((BusinessHolder) holder).business_img);
        }
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public void restoreItem(String item, int position) {
        list.add(position,item);
        notifyItemInserted(position);
    }

    public List getData() {
        return list;
    }

    public void removeItem(int position) {
        list.remove(position);
        notifyItemRemoved(position);
    }

    public static class BusinessHolder extends RecyclerView.ViewHolder {

        TextView index;
        ImageView business_img;
        TextView business_name;
        TextView business_rating;
        TextView business_distance;
        TextView business_id;

        TableRow business_detail_row;


        public BusinessHolder(@NonNull View itemView) {
            super(itemView);

            business_detail_row = itemView.findViewById(R.id.business_detail_row);
            business_id = itemView.findViewById(R.id.business_id);
            index = itemView.findViewById(R.id.business_index);
            business_name = itemView.findViewById(R.id.business_name);
            business_rating = itemView.findViewById(R.id.business_rating);
            business_distance = itemView.findViewById(R.id.business_distance);
            business_img = itemView.findViewById(R.id.business_image);

        }
    }

    public static class ReviewHolder extends RecyclerView.ViewHolder{
        TextView user_name;
        TextView rating;
        TextView review_text;
        TextView date_created;


        public ReviewHolder(@NonNull View itemView) {
            super(itemView);

            user_name = itemView.findViewById(R.id.review_name);
            rating = itemView.findViewById(R.id.review_rating);
            review_text = itemView.findViewById(R.id.review_review);
            date_created = itemView.findViewById(R.id.review_date);

        }
    }

    public static class ReservationHolder extends RecyclerView.ViewHolder{
        TextView reservation_index;
        TextView res_name_row;
        TextView res_date_row;
        TextView res_time_row;
        TextView res_email_row;
        public ReservationHolder(@NonNull View itemView) {
            super(itemView);

            reservation_index = itemView.findViewById(R.id.reservation_index);
            res_name_row = itemView.findViewById(R.id.res_name_row);
            res_date_row = itemView.findViewById(R.id.res_date_row);
            res_time_row = itemView.findViewById(R.id.res_time_row);
            res_email_row = itemView.findViewById(R.id.res_email_row);

        }
    }
}
