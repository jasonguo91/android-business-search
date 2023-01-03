package com.example.yelpsearch.fragment;

import android.os.Bundle;
import android.util.Log;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.yelpsearch.R;
import com.example.yelpsearch.adapters.RecyclerViewAdapter;
import com.example.yelpsearch.model.Business;
import com.example.yelpsearch.model.Review;

import java.util.ArrayList;
import java.util.List;


public class ReviewFragment extends Fragment {
    private ArrayList<Review> reviewArrayList;
    private List<Review> lstReview;
    private Bundle bundle;
    private RecyclerView recyclerView;
    private RecyclerViewAdapter recyclerViewAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_review, container, false);

        bundle = this.getArguments();
        reviewArrayList = (ArrayList<Review>) getArguments().getSerializable("review_list");
        Log.d("ReviewFragList", reviewArrayList.toString());

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_reviews);
        setupRecyclerView(reviewArrayList);
        return view;
    }

    private void setupRecyclerView(List<Review> lstReview) {
        recyclerViewAdapter = new RecyclerViewAdapter(requireContext(), lstReview);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        recyclerView.setAdapter(recyclerViewAdapter);
    }

}