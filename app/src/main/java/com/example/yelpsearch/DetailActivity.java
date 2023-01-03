package com.example.yelpsearch;

import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.yelpsearch.adapters.ViewPagerAdapter;
import com.example.yelpsearch.fragment.BusinessDetailFragment;
import com.example.yelpsearch.fragment.MapFragment;
import com.example.yelpsearch.fragment.ReviewFragment;
import com.example.yelpsearch.model.Review;
import com.google.android.material.tabs.TabLayout;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static java.net.URLEncoder.encode;

public class DetailActivity extends AppCompatActivity {

    private JsonObjectRequest request;
    private RequestQueue requestQueue;
    TabLayout tabLayout;
    TextView business_name_title;
    ViewPager viewPager;


    String business_name;
    String address;
    String price;
    String phone_number;
    String status;
    String category;
    String business_id;
    String business_url;
    JSONObject coordinates;
    String lat;
    String lon;
    JSONArray photos;
    ImageView facebookLink;
    ImageButton twitterLink;

    ArrayList<Review> reviewArrayList = new ArrayList<Review>();
    List<Review> lstReview;
    ArrayList<String> photoArrayList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        business_name = getIntent().getExtras().getString("business_name");
        String business_id = getIntent().getExtras().getString("business_id");
        Log.d("business_name", business_name);

        searchReviews(business_id);

        Toolbar detailToolbar = findViewById(R.id.DetailToolbar);
        detailToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_baseline_arrow_back_24));
        setSupportActionBar(detailToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        detailToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        business_name_title = findViewById(R.id.business_name_detail);
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);

        business_name_title.setText(business_name);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

/*        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

                if (tabLayout.getTabAt(position) != null) {
                    tabLayout.getTabAt(position).select();
                }
            }
        });*/
    }



    public void setSocialMediaButtons() {
        facebookLink = findViewById(R.id.facebook_icon);
        facebookLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/share.php?u=" + business_url));
                startActivity(browserIntent);
            }
        });

        twitterLink = findViewById(R.id.twitter_icon);
        twitterLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://twitter.com/share?text=Check out " + business_name + " on Yelp.&url=" + business_url));
                startActivity(browserIntent);
            }
        });
    }

    public void setViewPager() throws JSONException {
        ViewPagerAdapter ViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        //detail bundle + fragment
        Bundle detailBundle = new Bundle();
        detailBundle.putString("business_name", business_name);
        detailBundle.putString("business_address", address);
        detailBundle.putString("business_price", price );
        detailBundle.putString("business_phone", phone_number);
        detailBundle.putString("business_status", status);
        detailBundle.putString("business_category", category);
        detailBundle.putString("business_url", business_url);
        detailBundle.putSerializable("photo_list", photoArrayList);

        BusinessDetailFragment businessDetailFragment = new BusinessDetailFragment();
        businessDetailFragment.setArguments(detailBundle);

        //map bundle + fragment
        Bundle mapBundle = new Bundle();
        mapBundle.putString("lat", lat);
        mapBundle.putString("lon", lon);

        MapFragment mapFragment = new MapFragment();
        mapFragment.setArguments(mapBundle);

        //review bundle + fragment
        Bundle reviewBundle = new Bundle();
        reviewBundle.putSerializable("review_list", reviewArrayList);

        ReviewFragment reviewFragment = new ReviewFragment();
        reviewFragment.setArguments(reviewBundle);

        ViewPagerAdapter.add(businessDetailFragment, "Business Details");
        ViewPagerAdapter.add(mapFragment, "Map Location");
        ViewPagerAdapter.add(reviewFragment, "Reviews");

        viewPager.setAdapter(ViewPagerAdapter);
        setSocialMediaButtons();
    }

    private void searchReviews (String business_id) {

        String search_review_url = "https://angular-biz-search-backend.wl.r.appspot.com/reviews/" + business_id;
        Log.d("Detail Query url", search_review_url);

        request = new JsonObjectRequest(search_review_url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                JSONObject review_object = null;
                Log.d("Review Response", response.toString());

                try {
                    JSONArray reviews = response.getJSONArray("reviews");

                    for (int i = 0; i < reviews.length(); i++) {
                        review_object = reviews.getJSONObject(i);

                        Review review = new Review();
                        review.setUser_name(review_object.getJSONObject("user").getString("name"));
                        review.setRating(review_object.getString("rating"));
                        review.setText(review_object.getString("text"));
                        review.setTime_created(review_object.getString("time_created").substring(0, 10));

                        reviewArrayList.add(review);
                    }
                    searchDetails(business_id);
                } catch (JSONException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            }
        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue = Volley.newRequestQueue(DetailActivity.this);
        requestQueue.add(request);
    }

    private void searchDetails (String business_id) {

        String search_detail_url = "https://angular-biz-search-backend.wl.r.appspot.com/details/" + business_id;
        Log.d("Detail Query url", search_detail_url);

        request = new JsonObjectRequest(search_detail_url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                Log.d("Detail Response", response.toString());

                try {

                    //Address - null check and formatting
                    JSONObject location = response.getJSONObject("location");
                    JSONArray address_array = location.getJSONArray("display_address");
                    if (address_array.length() == 0) {
                        address = "N/A";
                    } else {
                        for (int i = 0; i < address_array.length(); i++) {
                            address += address_array.get(i) + " ";
                        }
                        address = address.substring(4, address.length());
                        Log.d("Address string: ", address);
                    }

                    //category - null check and formatting
                    JSONArray category_array = response.getJSONArray("categories");
                    if (category_array.length() == 0) {
                        category = "N/A";
                    } else {
                        for (int i = 0; i < category_array.length(); i++) {
                            JSONObject category_obj = (JSONObject) category_array.get(i);
                            category += category_obj.getString("title") + " | ";
                        }

                        category = category.substring(4, category.length() - 2);
                        Log.d("Category string: ", category);
                    }

                    if(response.isNull("display_phone") || response.getString("display_phone").equals("")) {
                        phone_number = "N/A";
                    } else {
                        phone_number = response.getString("display_phone");
                    }
                    Log.d("Phone",phone_number);

                    if(response.isNull("price")) {
                        price = "N/A";
                    } else {
                        price = response.getString("price");
                    }

                    JSONObject hours = (JSONObject) response.getJSONArray("hours").get(0);
                    if (hours.getString("is_open_now").equals("false")) {
                        status = "Closed";
                    } else {
                        status = "Open";
                    }
                    Log.d("Status", status);

                    business_url = response.getString("url");

                    photos = response.getJSONArray("photos");
                    Log.d("Photos array: ", photos.toString());
                    for (int i = 0; i < photos.length(); i++) {
                        photoArrayList.add(photos.getString(i));
                        Log.d("Photo", photos.getString(i));
                    }

                    coordinates = response.getJSONObject("coordinates");
                    Log.d("coordinates object", coordinates.toString());
                    lat = coordinates.get("latitude").toString();
                    lon = coordinates.get("longitude").toString();
                    Log.d("lat coor", lat);
                    Log.d("lon coor", lon);

                    setViewPager();
                } catch (JSONException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            }
        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }
}