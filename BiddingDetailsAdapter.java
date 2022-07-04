package com.homevents.vendor.adapter;

import android.content.Context;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.homevents.vendor.R;
import com.homevents.vendor.bean.PreviousBid;
import com.homevents.vendor.databinding.ListItemBidDetailBinding;
import com.homevents.vendor.listener.RecyclerViewClickListener;

import java.util.Date;
import java.util.List;
import java.util.Objects;

public class BiddingDetailsAdapter extends RecyclerView.Adapter<BiddingDetailsAdapter.BiddingDetailHolder> {
    private static final String TAG = "BidDtAdapter";
    private List<PreviousBid> biddingDetails;
    private Date endDate;
    private RecyclerViewClickListener recyclerViewClickListener;


    public BiddingDetailsAdapter(List<PreviousBid> biddingDetails, Date endDate, RecyclerViewClickListener recyclerViewClickListener) {
        this.biddingDetails = biddingDetails;
        this.endDate = endDate;
        this.recyclerViewClickListener=recyclerViewClickListener;

    }


    @NonNull
    @Override
    public BiddingDetailHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = (LayoutInflater) viewGroup.getContext().getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
        ListItemBidDetailBinding binding = DataBindingUtil.inflate(Objects.requireNonNull(inflater),
                R.layout.list_item_bid_detail, viewGroup, false);

        return new BiddingDetailHolder(binding);


    }

    @Override
    public void onBindViewHolder(@NonNull BiddingDetailHolder holder, int position) {
        PreviousBid previousBid = biddingDetails.get(position);
        holder.binding.setBid(previousBid);
        Log.d(TAG, "onBindViewHolder: "+previousBid.getBidStatus());
        if (previousBid.getBidStatus() != null && previousBid.getBidStatus() == 2){
            holder.binding.bidCancelBtn.setVisibility(View.GONE);
            holder.binding.statusTxt.setVisibility(View.VISIBLE);
        } else {
            holder.binding.statusTxt.setVisibility(View.GONE);
            if (endDate != null){
                long startMillis = System.currentTimeMillis(); //get the start time in milliseconds
                long endTimeMillis = endDate.getTime();
                long total_millis = (endTimeMillis - startMillis); //total time in milliseconds
                if (total_millis <= 0) {
                    holder.binding.bidCancelBtn.setVisibility(View.GONE);
                } else {
                    holder.binding.bidCancelBtn.setVisibility(View.VISIBLE);
                }
            } else {
                holder.binding.bidCancelBtn.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return biddingDetails.size();
    }

    public void update(PreviousBid previousBid){
        if (biddingDetails != null && !biddingDetails.isEmpty()){
            int index = biddingDetails.indexOf(previousBid);
            Log.d(TAG, "update bid: "+index);
            if (index != -1){
                biddingDetails.set(index,previousBid);
                notifyItemChanged(index);
            }
        }
    }

    class  BiddingDetailHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ListItemBidDetailBinding binding;
        CountDownTimer timer;

         BiddingDetailHolder(ListItemBidDetailBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            this.binding.bidCancelBtn.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
             if (recyclerViewClickListener != null){
                 recyclerViewClickListener.onClick(v,getAdapterPosition());
             }
        }
    }

}
