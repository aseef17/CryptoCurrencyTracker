package com.example.user.cryptocurrencytracker.Adapter;

import android.app.Activity;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.user.cryptocurrencytracker.Interface.ILoadMore;
import com.example.user.cryptocurrencytracker.Model.CoidModel;
import com.example.user.cryptocurrencytracker.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CoinAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    ILoadMore iLoadMore;
    boolean isLoading;
    Activity activity;
    List<CoidModel> items;

    int visibleThreshold = 5, lastVisibleItem, totalItemCount;

    public CoinAdapter(RecyclerView recyclerView,Activity activity, List<CoidModel> items) {
        this.activity = activity;
        this.items = items;

        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager)recyclerView.getLayoutManager();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                totalItemCount = linearLayoutManager.getItemCount();
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();

                if(!isLoading && totalItemCount <= (lastVisibleItem+visibleThreshold))
                {

                    if(iLoadMore != null)
                    {
                        iLoadMore.onLoadMore();
                    }
                    isLoading = true;

                }
            }
        });
    }

    public void setiLoadMore(ILoadMore iLoadMore) {
        this.iLoadMore = iLoadMore;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity)
                .inflate(R.layout.coin_layout,parent,false);

        return new CoinViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        CoidModel item = items.get(position);
        CoinViewHolder coinViewHolder =  (CoinViewHolder)holder;

        coinViewHolder.coin_name.setText(item.getName());
        coinViewHolder.coin_symbol.setText(item.getSymbol());
        coinViewHolder.coin_price.setText(item.getPrice_usd());
        coinViewHolder.one_hour_change.setText(item.getPercent_change_1h()+"%");
        coinViewHolder.twenty_hours_change.setText(item.getPercent_change_24h()+"%");
        coinViewHolder.seven_days_change.setText(item.getPercent_change_7d()+"%");

        Picasso.with(activity)
                .load(new StringBuilder("https://res.cloudinary.com/dxi90ksom/image/upload/")
                .append(item.getSymbol().toLowerCase()).append(".png").toString())
                .error(R.drawable.coin)
                .placeholder(R.drawable.coin)
                .into(coinViewHolder.coin_image);

        coinViewHolder.one_hour_change.setTextColor(item.getPercent_change_1h().contains("-")?
                Color.parseColor("#FF0000"):Color.parseColor("#32CD32"));

        coinViewHolder.twenty_hours_change.setTextColor(item.getPercent_change_24h().contains("-")?
                Color.parseColor("#FF0000"):Color.parseColor("#32CD32"));

        coinViewHolder.seven_days_change.setTextColor(item.getPercent_change_7d().contains("-")?
                Color.parseColor("#FF0000"):Color.parseColor("#32CD32"));

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setLoaded(){isLoading = false;}

    public void updateData(List<CoidModel> coinModels)
    {
        this.items = coinModels;
        notifyDataSetChanged();
    }

}
