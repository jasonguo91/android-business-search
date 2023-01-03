package com.example.yelpsearch.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import static java.net.URLEncoder.encode;

public class AutoCompleteAdapter extends ArrayAdapter<String> implements Filterable {
    private ArrayList<String> data;
    private JsonObjectRequest request;
    private RequestQueue requestQueue;

    public AutoCompleteAdapter(@NonNull Context context, @LayoutRes int resource) {
        super(context, resource);
        data = new ArrayList<>();
    }

    public void setData (ArrayList<String> list) {
        data.clear();
        data.addAll(list);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Nullable
    @Override
    public String getItem(int position) {
        return data.get(position);
    }

    public String getObject (int position) {
        return data.get(position);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        Filter dataFilter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence search) {
                FilterResults results = new FilterResults();
                if (search != null) {
                    String query = null;
                    try {
                        query = encode(search.toString(), StandardCharsets.UTF_8.name());
                    } catch (UnsupportedEncodingException e) {
                        throw new RuntimeException(e);
                    }

                    String autocomplete_url = "https://angular-yelp-backend.wl.r.appspot.com/autocomplete/" + query;

                    request = new JsonObjectRequest(autocomplete_url, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            try {
                                JSONArray autocomplete_results = response.getJSONArray("terms");

                                ArrayList<String> autocompleteSuggestions = new ArrayList<>();
                                for (int i = 0; i < autocomplete_results.length(); i++) {
                                    String term = autocomplete_results.getJSONObject(i).getString("text");
                                    autocompleteSuggestions.add(term);
                                    //Log.d("Autocomplete term: ", term);
                                }

                                JSONArray autocomplete_results_categories = response.getJSONArray("categories");
                                for (int i = 0; i < autocomplete_results_categories.length(); i++) {
                                    String category = autocomplete_results_categories.getJSONObject(i).getString("title");
                                    autocompleteSuggestions.add(category);
                                    Log.d("category", category);
                                }

                                data = autocompleteSuggestions;
                                results.values = autocompleteSuggestions;
                                results.count = autocompleteSuggestions.size();

                                Log.d("AutoAdapData", data.toString());


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
                    requestQueue = Volley.newRequestQueue(getContext());
                    requestQueue.add(request);

                    /*HttpURLConnection conn = null;
                    InputStream input = null;
                    try
                    {
                        URL url = new URL (server + search.toString());
                        conn = (HttpURLConnection) url.openConnection();
                        input = conn.getInputStream();
                        InputStreamReader reader = new InputStreamReader (input, "UTF-8");
                        BufferedReader buffer = new BufferedReader (reader, 8192);
                        StringBuilder builder = new StringBuilder();
                        String line;
                        while ((line = buffer.readLine()) != null)
                        {
                            builder.append (line);
                        }
                        JSONArray terms = new JSONArray (builder.toString());
                        ArrayList<String> suggestions = new ArrayList<>();
                        for (int ind = 0; ind < terms.length(); ind++)
                        {
                            String term = terms.getString (ind);
                            suggestions.add (term);
                        }
                        results.values = suggestions;
                        results.count = suggestions.size();
                        data = suggestions;
                    }
                    catch (Exception ex)
                    {
                        ex.printStackTrace();
                    }*/

/*                    finally
                    {
                        if (input != null)
                        {
                            try
                            {
                                input.close();
                            }
                            catch (Exception ex)
                            {
                                ex.printStackTrace();
                            }
                        }
                        if (conn != null) conn.disconnect();
                    }*/
                }
                Log.d("autocompleteAdapResults", results.toString());
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    notifyDataSetChanged();
                } else notifyDataSetInvalidated();
            }
        };
        return dataFilter;
    }
}