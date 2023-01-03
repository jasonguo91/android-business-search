package com.example.yelpsearch;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.yelpsearch.adapters.RecyclerViewAdapter;
import com.example.yelpsearch.model.Business;
import com.example.yelpsearch.model.Reservation;
import com.example.yelpsearch.model.Review;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Reservations extends AppCompatActivity {
    //init shared pref variables
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor myEdit;

    //init recyclerview variables
    private RecyclerView recyclerView;
    private RecyclerViewAdapter recyclerViewAdapter;
    private CoordinatorLayout coordinatorLayout;

    //init reservation list
    private ArrayList<Reservation> reservationArraylist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = getApplicationContext().getSharedPreferences("Reservations", 0);

        if (sharedPreferences.getAll().size() < 1) {
            setContentView(R.layout.activity_reservation_empty);

            Toolbar resToolbar = (Toolbar) findViewById(R.id.res_toolbar);
            resToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.back_arrow));
            setSupportActionBar(resToolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);

            resToolbar.setTitle("Yelp");
            resToolbar.setTitleTextColor(Color.WHITE);

            resToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });

        } else {
            setContentView(R.layout.activity_reservations);

            Toolbar resToolbar = (Toolbar) findViewById(R.id.res_toolbar);
            resToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.back_arrow));
            setSupportActionBar(resToolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);

            resToolbar.setTitle("Yelp");
            resToolbar.setTitleTextColor(Color.WHITE);

            resToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });
        }

/*        Toolbar resToolbar = (Toolbar) findViewById(R.id.res_toolbar);
        resToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.back_arrow));
        setSupportActionBar(resToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        resToolbar.setTitle("Yelp");
        resToolbar.setTitleTextColor(Color.WHITE);

        resToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });*/

        Gson gson = new Gson();
        reservationArraylist = new ArrayList<>();

        if(sharedPreferences.getAll() != null) {
            Map<String,?> keys = sharedPreferences.getAll();

            Log.d("sharedPrefALL", keys.toString());
            for(Map.Entry<String,?> entry : keys.entrySet()){
                Log.d("MapValue", entry.getValue().toString());
                String jsonRes = entry.getValue().toString();

                Reservation reservation = gson.fromJson(jsonRes, Reservation.class);

                reservationArraylist.add(reservation);
            }
        }

        Collections.sort(reservationArraylist);
        Log.d("Sorted list of reservation by index", reservationArraylist.toString());

        coordinatorLayout = findViewById(R.id.coordinatorLayout);
        recyclerView = findViewById(R.id.recycler_view_res);

        if (reservationArraylist.size() > 0) {
            setupRecyclerView(reservationArraylist);
        }
        enableSwipeToDeleteAndUndo();
    }

    private void setupRecyclerView(List<Reservation> lstReservation) {
        recyclerViewAdapter = new RecyclerViewAdapter(this, lstReservation);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setAdapter(recyclerViewAdapter);
    }

    private void enableSwipeToDeleteAndUndo() {
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(this) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {


                final int position = viewHolder.getAdapterPosition();
                final Reservation item = (Reservation) recyclerViewAdapter.getData().get(position);
                Log.d("res item", "onSwiped: " + item.getBusiness_name());

                sharedPreferences = getApplicationContext().getSharedPreferences("Reservations", 0);
                sharedPreferences.edit().remove(item.getBusiness_name()).commit();

                recyclerViewAdapter.removeItem(position);

                if (sharedPreferences.getAll().size() < 1 || sharedPreferences.getAll().size() == 0) {
                    setContentView(R.layout.activity_reservation_empty);

                    Toolbar resToolbar = (Toolbar) findViewById(R.id.res_toolbar);
                    resToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.back_arrow));
                    setSupportActionBar(resToolbar);
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    getSupportActionBar().setDisplayShowTitleEnabled(false);

                    resToolbar.setTitle("Yelp");
                    resToolbar.setTitleTextColor(Color.WHITE);

                    resToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            onBackPressed();
                        }
                    });
                }

                Snackbar snackbar = Snackbar
                        .make(coordinatorLayout, "Removing Existing Reservation", Snackbar.LENGTH_LONG);

                snackbar.setActionTextColor(Color.YELLOW);
                snackbar.show();

            }
        };

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(recyclerView);
    }
}