package com.homevents.vendor.adapter;

import android.content.Context;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.internal.LinkedTreeMap;
import com.homevents.vendor.R;
import com.homevents.vendor.adapter.holder.ProgressViewHolder;
import com.homevents.vendor.bean.BidStatus;
import com.homevents.vendor.bean.BiddingDetail;
import com.homevents.vendor.bean.Image;
import com.homevents.vendor.bean.Item;
import com.homevents.vendor.bean.PreviousBid;
import com.homevents.vendor.bean.RateCardItem;
import com.homevents.vendor.bean.Vendor;
import com.homevents.vendor.bean.VendorBidding;
import com.homevents.vendor.bean.VendorOrder;
import com.homevents.vendor.databinding.ListItemBiddingBinding;
import com.homevents.vendor.databinding.ListItemBiddingImgBinding;
import com.homevents.vendor.utils.HEApp;
import com.homevents.vendor.utils.HEUtility;
import com.homevents.vendor.utils.KeyName;
import com.homevents.vendor.utils.SharedPreference;
import com.homevents.vendor.utils.UIUtility;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.homevents.vendor.utils.HEApp.getContext;

public class BiddingListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = KeyName.LOG_TAG + "DocListAdp";
    private static final int VIEW_ITEM = 1;
    private static final int VIEW_PROGRESS = 2;

    private final List<VendorBidding> biddings;
    private BiddingListListener listener;
    private Vendor vendor;

    public BiddingListAdapter(List<VendorBidding> biddings) {
        this.biddings = biddings;
    }

    public void setVendor(Vendor vendor) {
        this.vendor = vendor;
    }

    public void setListener(BiddingListListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_ITEM) {
            LayoutInflater inflater = (LayoutInflater)
                    parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            ListItemBiddingBinding binding = DataBindingUtil.inflate(Objects.requireNonNull(inflater),
                    R.layout.list_item_bidding, parent, false);
            return new BiddingListAdapter.ViewHolder(binding);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pbar_item, parent, false);
            return new ProgressViewHolder(view);
        }
    }

    //@SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolder) {
            VendorBidding bidding = biddings.get(position);
            ((ViewHolder) holder).bind(bidding);
        } else {
            ((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
        }
    }

    @Override
    public long getItemId(int position) {
        return biddings.get(position) != null ? VIEW_ITEM : VIEW_PROGRESS;
    }

    @Override
    public int getItemViewType(int position) {
        return biddings.get(position) != null ? VIEW_ITEM : VIEW_PROGRESS;
    }

    @Override
    public int getItemCount() {
        return biddings.size();
    }

    public void update(VendorBidding vendorBidding) {
        if (biddings != null && !biddings.isEmpty()) {
            int pos = biddings.indexOf(vendorBidding);
            // Log.d(TAG, "update: " + pos);
            if (pos != -1) {
                biddings.set(pos, vendorBidding);
                notifyItemChanged(pos);
            }
        }
    }

    public interface BiddingListListener {
        void onRecyclerItemClicked(View v, int position);

        void onImageClick(int adapterPos, int imgPos);
        void refreshImg(int pos,View v);
    }

    private class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ListItemBiddingBinding binding;
        CountDownTimer timer;


        ViewHolder(ListItemBiddingBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            this.binding.placeBidBtn.setOnClickListener(this);
            this.binding.biddingRoot.setOnClickListener(this);
            this.binding.eventImg.setOnClickListener(this);
            this.binding.serviceImgView.setOnClickListener(this);
            this.binding.cusSelectedImg.setOnClickListener(this);
            this.binding.refreshIcon.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.onRecyclerItemClicked(v, getAdapterPosition());
            listener.refreshImg(getAdapterPosition(),v);
        }


        private void initBidImg(List<Image> images) {
            for (int i = 0; i < images.size(); i++) {
                String imgUrl = images.get(i).getUrl();
                // binding.imgContainer.addView(prepareBidServiceImgView(imgUrl, i));
            }
        }

        private View prepareBidServiceImgView(String url, int position) {
            LayoutInflater inflater = (LayoutInflater)
                    binding.getRoot().getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            ListItemBiddingImgBinding binding = DataBindingUtil.inflate(Objects.requireNonNull(inflater),
                    R.layout.list_item_bidding_img, null, false);
            UIUtility.setOtherPicasso(binding.bidImg, url);
            binding.bidImg.setTag(position);
            binding.bidImg.setOnClickListener(v -> {
                int position1 = (int) v.getTag();
                listener.onImageClick(getAdapterPosition(), position1);
            });
            return binding.getRoot();
        }

      /*  private void showServiceImageView(boolean isShow) {
            binding.imgContainer.setVisibility(isShow ? View.VISIBLE : View.GONE);
        }*/

        public void bind(VendorBidding bidding) {
            binding.setBidding(bidding);

            String venue = "-";
            if (bidding.getBidding() != null && bidding.getBidding().getVendorOrder() != null &&
                    bidding.getBidding().getVendorOrder().getAddress() != null && bidding.getBidding().getVendorOrder().getAddress().getVenue() != null){
                venue = bidding.getBidding().getVendorOrder().getAddress().getVenue();
            }
            binding.venueValueTxt.setText(venue);
            if (false){
                binding.eventNoteLabelTxt.setVisibility(View.VISIBLE);
                binding.venueValueTxt.setVisibility(View.VISIBLE);
                binding.eventValueTxt.setShowingLine(2);
                binding.eventValueTxt.addShowMoreText(HEApp.getContext().getString(R.string.continue_str));
                binding.eventValueTxt.addShowLessText(HEApp.getContext().getString(R.string.less_str));
                binding.eventValueTxt.setShowMoreColor(ContextCompat.getColor(HEApp.getContext(),R.color.colorPrimary)); // or other color
                binding.eventValueTxt.setShowLessTextColor(ContextCompat.getColor(HEApp.getContext(),R.color.colorPrimary)); // or other color
            } else {
                binding.eventNoteLabelTxt.setVisibility(View.VISIBLE);
                binding.eventValueTxt.setVisibility(View.GONE);
            }

            String qty = bidding.getQuantity() != null ? String.valueOf(bidding.getQuantity()) : "0";
            binding.qtyValueTxt.setText(qty);

            String tierName = null;
            if (bidding.getCostTier() != null) {
                Object obj = bidding.getCostTier();
                LinkedTreeMap t = (LinkedTreeMap) obj;
                if (t.get("tier_name") != null) {
                    tierName = t.get("tier_name").toString();
                }
            }
            if (tierName != null) {
                binding.packageTxt.setVisibility(View.VISIBLE);
                binding.packageTxt.setText(tierName);
            } else {
                binding.packageTxt.setVisibility(View.INVISIBLE);
            }

            if (bidding.getEvent() != null) {
                binding.serviceTxt.setText(bidding.getEvent().getName());
                binding.serviceImgView.setTag(bidding.getEvent().getImgUrl());
                UIUtility.setOtherPicasso(binding.serviceImgView, bidding.getEvent().getImgUrl());
            }
            String rateCard = null;
            if (bidding.getService() != null) {
                binding.eventImg.setTag(bidding.getService().getImgUrl());
                UIUtility.setOtherPicasso(binding.eventImg,bidding.getService().getImgUrl() );
                binding.eventNameTxt.setText(bidding.getService().getName());
                for (RateCardItem rateCardItem : bidding.getService().getRateCardItems()) {
                    if (rateCardItem.getId().equals(bidding.getRateItem())) {
                        rateCard = rateCardItem.getName();
                    }
                }
            }

            if (rateCard != null) {
                binding.rateItemTxt.setVisibility(View.VISIBLE);
                binding.rateItemTxt.setText(rateCard);
            } else {
                binding.rateItemTxt.setVisibility(View.INVISIBLE);
            }

            BidStatus bidStatus = null;
            if (bidding.getBidding() != null) {
                bidStatus = BidStatus.getStatus(bidding.getBidding().getStatus());
                //Log.d(TAG, "bind: " + bidStatus);
                if (timer != null) {
                    timer.cancel();
                }

                long startMillis = System.currentTimeMillis(); //get the start time in milliseconds

                long endTimeMillis = bidStatus != null && bidStatus == BidStatus.CLOSED
                        ? System.currentTimeMillis() : bidding.getBidding().getEndDate().getTime();
                long total_millis = (endTimeMillis - startMillis); //total time in milliseconds

                timer = new CountDownTimer(total_millis, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        long days = TimeUnit.MILLISECONDS.toDays(millisUntilFinished);
                        millisUntilFinished -= TimeUnit.DAYS.toMillis(days);

                        long hours = TimeUnit.MILLISECONDS.toHours(millisUntilFinished);
                        millisUntilFinished -= TimeUnit.HOURS.toMillis(hours);

                        long minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished);
                        millisUntilFinished -= TimeUnit.MINUTES.toMillis(minutes);

                        long seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished);

                        String format;
                        if (days == 0) {
                            format = String.format(HEUtility.getCurrentLocale(),
                                    "%02d:%02d:%02d", hours, minutes, seconds);
                        } else {
                            format = String.format(HEUtility.getCurrentLocale(),
                                    "%02d days:%02d:%02d:%02d", days, hours, minutes, seconds);
                        }

                        binding.timerTxt.setText(format);
                    }

                    // @SuppressLint("SetTextI18n")
                    @Override
                    public void onFinish() {
                        binding.timerTxt.setText(getContext().getResources().
                                getString(R.string.ended));
                        showPlaceBid(false);
                    }
                }.start();

                if (bidding.getBidding().getVendorOrder() != null) {
                    VendorOrder vendorOrder = bidding.getBidding().getVendorOrder();
                    if (vendorOrder.getAddress() != null)
                        binding.locationTxt.setText(bidding.getBidding().getVendorOrder().getAddress().getFullAddress());

                    if (vendorOrder.getItems() != null && !vendorOrder.getItems().isEmpty())
                        binding.dateTxt.setText(HEUtility.serverDateToDisplay(vendorOrder.getItems().get(0).getDate()));
                }

                List<BiddingDetail> biddingDetails = bidding.getBidding().getBiddingDetails();
                if (biddingDetails != null && !biddingDetails.isEmpty()) {
                    int index = biddingDetails.indexOf(new BiddingDetail(bidding.getBiddingDetailId()));
                    if (index != -1) {
                        BiddingDetail biddingDetail = biddingDetails.get(index);
                        binding.statusTxt.setVisibility(View.GONE);
                        if (biddingDetail != null) {
                            if (biddingDetail.getBase_bid() != null) {
                                binding.lowestAmountTxt.setText(HEUtility.getFormatCurrencyStr(biddingDetail.getBase_bid()));
                            }
                            if (biddingDetail.getWinning_bid() != null && biddingDetail.getWin_bid_vendor() != null) {
                                binding.lowestBidAmountTxt.setText(HEUtility.getFormatCurrencyStr(biddingDetail.getWinning_bid()));
                                if (bidStatus != null && bidStatus == BidStatus.CLOSED
                                        && biddingDetail.getWin_bid_vendor().equals(SharedPreference.getVendor().getId())){
                                    binding.lowestBidTxt.setText("Won Bid:");
                                    binding.statusTxt.setVisibility(View.VISIBLE);
                                }
                                binding.lowestBidTxt.setVisibility(View.VISIBLE);
                                binding.lowestBidAmountTxt.setVisibility(View.VISIBLE);
                                binding.refreshIcon.setVisibility(View.VISIBLE);
                            } else {
                                //binding.minimumBidTxt.setText("Base Bid:");
                                binding.statusTxt.setVisibility(View.GONE);
                                binding.lowestBidTxt.setVisibility(View.GONE);
                                binding.lowestBidAmountTxt.setVisibility(View.GONE);
                                binding.refreshIcon.setVisibility(View.GONE);
                            }

                            if (bidding.getBidding() != null
                                    && bidding.getBidding().getVendorOrder() != null
                                    && bidding.getBidding().getVendorOrder().getItems() != null) {
                                Item temp = bidding.getBidding().getVendorOrder().getItems().get(0);
                                if (temp.getComments() != null) {
                                    binding.eventValueTxt.setVisibility(View.VISIBLE);
                                    binding.eventNoteLabelTxt.setVisibility(View.VISIBLE);
                                    binding.eventValueTxt.setText(temp.getComments());
                                } else {
                                    binding.eventValueTxt.setVisibility(View.GONE);
                                    binding.eventNoteLabelTxt.setVisibility(View.GONE);
                                }

                                for (Item item : bidding.getBidding().getVendorOrder().getItems()) {
                                    Log.i(TAG, "item***: " + item);
                                    if (item.getEvent() != null && item.getService() != null
                                            && bidding.getRateItem() != null && item.getRateItem() != null) {
                                        Log.d(TAG, "cost tier: ");
                                        if (item.getEvent().getId().equals(biddingDetail.getEvent())
                                                && item.getService().getId().equals(biddingDetail.getService())
                                                && item.getRateItem().equals(bidding.getRateItem())&& item.getRate().equals(bidding.getRate()))
                                        {
                                            if (item.getImages() != null && !item.getImages().isEmpty()) {
                                                Log.i(TAG, "item totalimg: " + item.getImages());
                                                //   Log.i(TAG, "item image: " + item.getImages().get(0));
                                                binding.cusSelectedImg.setTag(item.getImages().get(0));
                                                UIUtility.setOtherPicasso(binding.cusSelectedImg, item.getImages().get(0));
                                            }
                                        }
                                    }
                                }

                            }
                        }
                    }
                }



                if (bidding.getLastBid() != null) {
                    binding.lastBidTxt.setVisibility(View.VISIBLE);
                    binding.bidAmountView.setVisibility(View.VISIBLE);
                    StringBuilder builder = new StringBuilder();
                    builder.append(HEUtility.getFormatCurrencyStr(bidding.getLastBid()));

                    if (bidding.getPreviousBids() != null && !bidding.getPreviousBids().isEmpty()) {
                        for (PreviousBid bid : bidding.getPreviousBids()) {
                            builder.append(", ");
                            builder.append(HEUtility.getFormatCurrencyStr(bid.getAmt()));
                        }
                    }

                    Log.d(TAG, "bind: " + builder.toString());
                    binding.bidAmountView.setText(builder.toString());
                } else {
                    binding.lastBidTxt.setVisibility(View.GONE);
                    binding.bidAmountView.setVisibility(View.GONE);
                }

                if (bidStatus != null && bidding.getBidding() != null) {
                    if (bidStatus == BidStatus.STARTED
                            && bidding.getBidding().getEndDate().getTime() > System.currentTimeMillis()) {
                        showPlaceBid(true);
                    } else {
                        showPlaceBid(false);
                    }
                }
            }

            // initService();
           /* if (binding.imgContainer.getChildCount() == 0 && vendor != null && vendor.getVendorPortfolio() != null)
                if (bidding.getService() != null) {
                    List<Image> images = new ArrayList<>();
                    for (VendorPortfolio portfolio : vendor.getVendorPortfolio()) {
                        if (bidding.getService().getId().equals(portfolio.getService().getId())
                                && portfolio.getImages() != null && !portfolio.getImages().isEmpty()) {
                            images.addAll(portfolio.getImages());
                        }
                    }
                    if (images.size() > 0) {
                        bidding.setImages(images);
                        initBidImg(images);
                        showServiceImageView(true);
                    } else {
                        showServiceImageView(false);
                    }
                } else {
                    showServiceImageView(false);
                }*/

        }

        private void showPlaceBid(boolean isShow) {
            binding.placeBidBtn.setVisibility(isShow ? View.VISIBLE : View.GONE);
            // binding.endInTxt.setVisibility(isShow ? View.VISIBLE : View.GONE);
        }
    }
}