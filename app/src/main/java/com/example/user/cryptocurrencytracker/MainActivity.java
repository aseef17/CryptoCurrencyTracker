package com.example.user.cryptocurrencytracker;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.cryptocurrencytracker.Adapter.CoinAdapter;
import com.example.user.cryptocurrencytracker.Interface.ILoadMore;
import com.example.user.cryptocurrencytracker.Model.CoidModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private Toolbar mainToolbar;

    List<CoidModel> items = new ArrayList<>();
    CoinAdapter adapter;
    RecyclerView recyclerView;

    OkHttpClient client;
    Request request;

    SwipeRefreshLayout swipeRefreshLayout;

    ProgressBar progressBar;

    Dialog dialog;

    Button Positive_button,Negative_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dialog = new Dialog(MainActivity.this, R.style.PopTheme);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.no_internet_dialog);

        Positive_button = dialog.findViewById(R.id.positiveBtn);
        Negative_button = dialog.findViewById(R.id.negativeBtn);

        dialog.setCancelable(false);

        mainToolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(mainToolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        swipeRefreshLayout = findViewById(R.id.root_layout);

        progressBar = findViewById(R.id.progressBar);

        ConnectivityManager connMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {

            swipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    loadFirst10coins(0);
                }
            });

        } else {

            try {

                Positive_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        finish();
                        overridePendingTransition( 0, 0);
                        startActivity(getIntent());
                        overridePendingTransition( 0, 0);

                    }
                });

                Negative_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        finish();

                    }
                });

                dialog.setCancelable(false);
                dialog.show();

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        /*swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                loadFirst10coins(0);
            }
        });
*/
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                items.clear();

                progressBar.setVisibility(View.VISIBLE);

                loadFirst10coins(0);
               /* setUpAdapter();*/

            }
        });

        recyclerView = findViewById(R.id.coinList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        setUpAdapter();

    }

    private void setUpAdapter() {

        adapter  =  new CoinAdapter(recyclerView,MainActivity.this,items);
        recyclerView.setAdapter(adapter);
        adapter.setiLoadMore(new ILoadMore() {
            @Override
            public void onLoadMore() {

                if(items.size() <= 1000) {

                    loadNext10Coin(items.size());

                } else {

                    Toast.makeText(MainActivity.this, "Max items is 1000", Toast.LENGTH_SHORT).show();
                    
                }

            }
        });

    }

    private void loadNext10Coin(final int index) {

        client = new OkHttpClient();
        request = new Request.Builder().url(String.format("https://api.coinmarketcap.com/v1/ticker/?start=%d&limit=10",index))
                .build();

        swipeRefreshLayout.setRefreshing(true);
        client.newCall(request)
                .enqueue(new Callback() {

                    Handler mainHandler = new Handler(getApplicationContext().getMainLooper());

                    @Override
                    public void onFailure(Call call, IOException e) {

                        mainHandler.post(new Runnable() {

                            @Override
                            public void run() {

                                try {

                                    Positive_button.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                            finish();
                                            overridePendingTransition( 0, 0);
                                            startActivity(getIntent());
                                            overridePendingTransition( 0, 0);

                                        }
                                    });

                                    Negative_button.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                            finish();

                                        }
                                    });

                                    dialog.setCancelable(false);
                                    dialog.show();

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                        });

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {

                        String body = response.body().string();
                        Gson gson =  new Gson();
                        final List<CoidModel> newItems = gson.fromJson(body, new TypeToken<List<CoidModel>>(){}.getType());

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                items.addAll(newItems);
                                adapter.setLoaded();
                                adapter.updateData(items);
                                swipeRefreshLayout.setRefreshing(false);

                            }
                        });

                    }
                });

        progressBar.setVisibility(View.INVISIBLE);

    }

    private void loadFirst10coins(int index) {

        client = new OkHttpClient();
        request = new Request.Builder().url(String.format("https://api.coinmarketcap.com/v1/ticker/?start=%d&limit=10",index))
                .build();

        swipeRefreshLayout.setRefreshing(true);
        client.newCall(request)
                .enqueue(new Callback() {

                    @Override
                    public void onFailure(Call call, IOException e) {

                        try {

                            Positive_button.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    finish();
                                    overridePendingTransition( 0, 0);
                                    startActivity(getIntent());
                                    overridePendingTransition( 0, 0);

                                }
                            });

                            Negative_button.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    finish();

                                }
                            });

                            dialog.setCancelable(false);
                            dialog.show();

                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {

                        String body = response.body().string();
                        Gson gson =  new Gson();
                        final List<CoidModel> newItems = gson.fromJson(body, new TypeToken<List<CoidModel>>(){}.getType());

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                adapter.updateData(newItems);

                            }
                        });

                    }
                });
        if(swipeRefreshLayout.isRefreshing())
            swipeRefreshLayout.setRefreshing(false);

    }

}
