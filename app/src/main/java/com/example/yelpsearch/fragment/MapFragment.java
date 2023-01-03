package com.example.yelpsearch.fragment;

import android.os.Bundle;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.yelpsearch.R;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import org.jetbrains.annotations.NotNull;


public class MapFragment extends Fragment implements OnMapReadyCallback {
    GoogleMap googleMap;
    private Bundle bundle;
    private String lat_string, lon_string;
    private Double lat, lon;
    private Marker markerBusiness;;
    private LatLng business;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        bundle = this.getArguments();
        lat_string = bundle.getString("lat");
        lon_string = bundle.getString("lon");

        lat = Double.parseDouble(lat_string);
        lon = Double.parseDouble(lon_string);
        business = new LatLng(lat, lon);


        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        SupportMapFragment supportMapFragment = (SupportMapFragment)getChildFragmentManager().findFragmentById(R.id.map);

        supportMapFragment.getMapAsync(this);

        return view;
    }

    @Override
    public void onMapReady(@NonNull @NotNull GoogleMap googleMap) {
        this.googleMap = googleMap;
        markerBusiness = googleMap.addMarker(new MarkerOptions()
                .position(business));


        Log.d("MAPCoor", business.toString());
        moveToCurrentLocation(business);
    }
    private void moveToCurrentLocation(LatLng currentLocation)
    {
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation,15));
        // Zoom in, animating the camera.
        googleMap.animateCamera(CameraUpdateFactory.zoomIn());
        // Zoom out to zoom level 10, animating with a duration of 2 seconds.
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);


    }
}

