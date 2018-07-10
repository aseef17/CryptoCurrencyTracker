package com.example.user.cryptocurrencytracker.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.user.cryptocurrencytracker.R;

public class CoinViewHolder extends RecyclerView.ViewHolder {

    public ImageView coin_image;
    public TextView coin_symbol,coin_name,coin_price,one_hour_change,twenty_hours_change,seven_days_change;

    public CoinViewHolder(View itemView) {
        super(itemView);

        coin_image = itemView.findViewById(R.id.coin_icon);
        coin_name = itemView.findViewById(R.id.coin_name);
        coin_symbol = itemView.findViewById(R.id.coin_symbol);
        coin_price = itemView.findViewById(R.id.priceUSDText);
        one_hour_change = itemView.findViewById(R.id.percentChange1hText);
        twenty_hours_change = itemView.findViewById(R.id.percentChange24hText);
        seven_days_change = itemView.findViewById(R.id.percentChange7dText);
    }

}
