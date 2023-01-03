package com.example.yelpsearch;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.yelpsearch.adapters.RecyclerViewAdapter;
import com.example.yelpsearch.model.Business;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static java.net.URLEncoder.encode;

public class MainActivity<businessArray> extends AppCompatActivity {

    //form elements
    Button search_btn, clear_btn;
    ImageButton reservation_btn;
    EditText  distance_input, location_input;
    TextView emptyView;

    Spinner category_spinner;
    CheckBox location_checkbox;

    private ArrayList<Business> businessArray;

    private final String JSON_url = "https://angular-yelp-backend.wl.r.appspot.com/search/food/8452/34.0298/-118.3528/all";
    private JsonObjectRequest request;
    private RequestQueue requestQueue;
    private List<Business> lstBusiness;
    private RecyclerView recyclerView;
    private RecyclerViewAdapter myAdapter;

    Map<String, String> coordinates = new HashMap<String, String>();
    private static final int TRIGGER_AUTO_COMPLETE = 100;
    private static final long AUTO_COMPLETE_DELAY = 300;

    AppCompatAutoCompleteTextView autocomplete;
    private ArrayList<String> autocompleteList = new ArrayList<>();

    boolean isAllFieldsChecked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //assign values to each control on the layout
        search_btn = findViewById(R.id.search_button);
        clear_btn = findViewById(R.id.clear_button);
        reservation_btn = findViewById(R.id.reservation_icon);

        //keyword_input = findViewById(R.id.keyword_input);
        distance_input = findViewById(R.id.distance_input);
        location_input = findViewById(R.id.location_input);
        category_spinner = findViewById(R.id.category_spinner);
        location_checkbox = findViewById(R.id.location_checkbox);

        //init autocomplete and set up adapater
        autocomplete = findViewById(R.id.keyword_input);

        //empty result view
        emptyView = findViewById(R.id.empty_view);

        autocomplete.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                getAutcompleteText(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        location_checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    Log.d("Checkbox check", "onCheckedChanged is happening");
                    location_input.setEnabled(false);
                    location_input.setVisibility(View.INVISIBLE);
                    autoLocation();
                } else {
                    location_input.setEnabled(true);
                    location_input.setVisibility(View.VISIBLE);
                }
            }
        });

        lstBusiness = new ArrayList<>();
        recyclerView = findViewById(R.id.recycler_view);

        reservation_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), Reservations.class);
                startActivity(intent);
            }
        });

       search_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                isAllFieldsChecked = CheckAllFields();

                if (isAllFieldsChecked) {

                    if (location_checkbox.isChecked()) {
                        String keyword = autocomplete.getText().toString();
                        String distance = distance_input.getText().toString();
                        String category = category_spinner.getSelectedItem().toString();

                        search(keyword, Integer.valueOf(distance), coordinates.get("lat"), coordinates.get("lon"), category);
                    } else {
                        String location = location_input.getText().toString();
                        Log.d("location string", location);
                        geocodeLocation(location);
                    }
                }
            }
        });

        clear_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                clearForm(view);
            }
        });
    }

    private boolean CheckAllFields() {
        if (autocomplete.length() == 0) {
            autocomplete.setError("This field is required");
            return false;
        }

        if (distance_input.length() == 0) {
            distance_input.setError("This field is required");
            return false;
        }

        if (location_input.length() == 0) {
            location_input.setError("This field is required");
        }

        return true;
    }

    private void getAutcompleteText(String search) {
        String query = null;
        try {
            query = encode(search, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        String autocomplete_url = "https://angular-yelp-backend.wl.r.appspot.com/autocomplete/" + query;

        request = new JsonObjectRequest(autocomplete_url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                JSONArray autocomplete_results = null;
                autocompleteList.clear();
                try {
                    autocomplete_results = response.getJSONArray("terms");

                    for (int i = 0; i < autocomplete_results.length(); i++) {
                        autocompleteList.add( autocomplete_results.getJSONObject(i).getString("text"));
                    }

                    JSONArray autocomplete_results_categories = response.getJSONArray("categories");
                    for (int i = 0; i < autocomplete_results_categories.length(); i++) {
                        String category = autocomplete_results_categories.getJSONObject(i).getString("title");
                        autocompleteList.add(category);
                    }

                    Log.d("Autocomplete array", autocompleteList.toString());
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, autocompleteList);

                    autocomplete.setAdapter(adapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        //requestQueue = VolleySingleton.getInstance(this).getRequestQueue();
        //VolleySingleton.getInstance(this).addToRequestQueue(request);
        requestQueue = Volley.newRequestQueue(MainActivity.this);
        requestQueue.add(request);
    }

    private void autoLocation() {
        String auto_location_url = "https://ipinfo.io/?token=e192fe79b9d3e7";

        request = new JsonObjectRequest(auto_location_url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                String location = "";
                String lat = "";
                String lon = "";
                JSONArray location_results = null;

                try {
                    location = response.getString("loc");
                    lat = location.substring(0, location.indexOf(','));
                    lon = location.substring(location.indexOf(',') + 1, location.length());

                    coordinates.put("lat", lat);
                    coordinates.put("lon", lon);
                    Log.d("coordinates", coordinates.toString());

                } catch (JSONException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        //requestQueue = VolleySingleton.getInstance(this).getRequestQueue();
        //VolleySingleton.getInstance(this).addToRequestQueue(request);

        requestQueue = Volley.newRequestQueue(MainActivity.this);
        requestQueue.add(request);

    }
    private void geocodeLocation(String location)  {
        String keyword = autocomplete.getText().toString();
        String distance = distance_input.getText().toString();
        String category = category_spinner.getSelectedItem().toString();

        String query = null;
        try {
            query = encode(location, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        String geocode_url = "https://angular-yelp-backend.wl.r.appspot.com/geocode/" + query;

        request = new JsonObjectRequest(geocode_url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                String lat = "";
                String lon = "";
                JSONArray location_results = null;

                try {
                    location_results = response.getJSONArray("results");
                    lat = location_results.getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getString("lat");
                    lon = location_results.getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getString("lng");

                    coordinates.put("lat", lat);
                    coordinates.put("lon", lon);
                    Log.d("coordinates", coordinates.toString());

                    String lat_string = coordinates.get("lat").toString();
                    String lon_string = coordinates.get("lon").toString();
                    search(keyword,Integer.valueOf(distance),lat_string , lon_string,category);

                } catch (JSONException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        //requestQueue = VolleySingleton.getInstance(this).getRequestQueue();
        //VolleySingleton.getInstance(this).addToRequestQueue(request);

        requestQueue = Volley.newRequestQueue(MainActivity.this);
        requestQueue.add(request);
    }

    private String getCategoryValue(String category) {
        String category_val = "all";
        switch(category){
            case "Default":
                category_val = "all";
                break;
            case "Arts & Entertainment":
                category_val = "arts";
                break;
            case "Food":
                category_val = "food";
                break;
            case "Health & Medical":
                category_val = "health";
                break;
            case "Hotels & Travel":
                category_val = "hotelstravel";
                break;
            case "Professional Services":
                category_val = "professional";
                break;
        }
        return category_val;
    }

    private void search(String keyword, Integer distance, String latitude, String longitude, String category) {

        //convert distance to meters
        Integer distance_meters = distance*1609;

        //url encode keyword
        String query = null;
        try {
            query = encode(keyword, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        //category
        String category_value = getCategoryValue(category);

        String search_url = "https://angular-yelp-backend.wl.r.appspot.com/search/" + query + "/" + distance_meters + "/" + latitude + "/" + longitude + "/" + category_value;
        Log.d("Query url", search_url);

        request = new JsonObjectRequest(search_url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                //JSONArray businesses = null;
                JSONObject business_object = null;
                Log.d("Response Object", response.toString());

                try {
                    Log.d("Total", response.getString("total"));
                    if ( Integer.parseInt(response.getString("total")) == 0) {
                        Log.d("EmptyResult", "EmptyResult");
                        recyclerView.setVisibility(View.GONE);
                        emptyView.setVisibility(View.VISIBLE);
                    } else {
                        recyclerView.setVisibility(View.VISIBLE);
                        emptyView.setVisibility(View.GONE);
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

                try {
                    JSONArray businesses = response.getJSONArray("businesses");
                    lstBusiness.clear();

                    for (int i = 0; i < businesses.length(); i++) {
                        int tableNo = i + 1;
                        String tableNoString = String.valueOf(tableNo);

                        business_object = businesses.getJSONObject(i);

                        Business business = new Business();
                        business.setTable_index(tableNoString);
                        business.setBusiness_id(business_object.getString("id"));
                        business.setName(business_object.getString("name"));
                        business.setRating(business_object.getString("rating"));
                        business.setImage_url(business_object.getString("image_url"));
                        business.setDistance_meters(business_object.getString("distance"));
                        business.convertToMiles();

                        lstBusiness.add(business);
                        Log.d("jsonObject", lstBusiness.toString());

                        Log.d("businesses length", String.valueOf(businesses.length()));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
                setupRecyclerView(lstBusiness);
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        //requestQueue = VolleySingleton.getInstance(this).getRequestQueue();
        //VolleySingleton.getInstance(this).addToRequestQueue(request);

        requestQueue = Volley.newRequestQueue(MainActivity.this);
        requestQueue.add(request);
    }

/*
    public void onRowClick(View view) {
        Intent intent = new Intent(view.getContext(), DetailActivity.class);
        intent.putExtra("business_id", R.id.business_id);
        intent.putExtra("business_name", R.id.business_name);

        startActivity(intent);
    }
*/



    private void setupRecyclerView(List<Business> lstBusiness) {
        myAdapter = new RecyclerViewAdapter(this, lstBusiness);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setAdapter(myAdapter);
    }

    public void clearForm(View view) {
        if (autocomplete != null) autocomplete.setText("");
        if (category_spinner != null) category_spinner.setSelection(0);
        if (distance_input != null) distance_input.setText("");
        if (location_input != null) location_input.setText("");

        if (myAdapter != null) myAdapter.clear();
    }

}