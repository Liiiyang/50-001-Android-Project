package com.example.liyang.androidtouristapp;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.TextView;

public class ShowRoute extends FragmentActivity implements OnMapReadyCallback{

    private GoogleMap myMap;
    Polyline line;
    Context context;
    Marker marker;
    Marker marker1;


    public void onCreate(Bundle bd) {
        super.onCreate(bd);
        setContentView(R.layout.activity_showroute);
        context = ShowRoute.this;
        // GoogleMap myMap
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        myMap.setMyLocationEnabled(true);

    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        myMap = googleMap;
        // TODO: Add Coordinates (startLatLng, endLatLng) and add more urlToPass, don't touch the rest
        //LatLng startLatLng = new LatLng(1.2839, 103.8609);
        //LatLng endLatLng = new LatLng(1.31749873, 103.7043305);
        //String urlTopass = makeURL(startLatLng.latitude,
                //startLatLng.longitude, endLatLng.latitude,
                //endLatLng.longitude);
        LatLng startLatLng;
        LatLng endLatLng;
        String urlTopass;
        Pojo currentx = null;
        int counter = 0;
        //Create a Route from the last attraction back to the hotel
        LatLng first = new LatLng(Double.parseDouble(RecyclerAttraction.nodesChosen.get(0).getLatitude()), Double.parseDouble(RecyclerAttraction.nodesChosen.get(0).getLongitude()));
        LatLng last = new LatLng(Double.parseDouble(RecyclerAttraction.nodesChosen.get(RecyclerAttraction.nodesChosen.size()-1).getLatitude()), Double.parseDouble(RecyclerAttraction.nodesChosen.get(RecyclerAttraction.nodesChosen.size()-1).getLongitude()));
        String newurlTopass = makeURL(first.latitude,
                first.longitude, last.latitude,
                last.longitude);
        new connectAsyncTask(newurlTopass).execute();
        //Create Route for the rest of the attractions
        for(Pojo x: RecyclerAttraction.nodesChosen){
            if(currentx==null){
                currentx = x;
                continue;
            }
            startLatLng = new LatLng(Double.parseDouble(currentx.getLatitude()), Double.parseDouble(currentx.getLongitude()));
            endLatLng = new LatLng(Double.parseDouble(x.getLatitude()), Double.parseDouble(x.getLongitude()));
            urlTopass = makeURL(startLatLng.latitude,
                    startLatLng.longitude, endLatLng.latitude,
                    endLatLng.longitude);
            new connectAsyncTask(urlTopass).execute();

            currentx = x;
            }
            //Add Markers
        for(int i=0;i<RecyclerAttraction.nodesChosen.size();i++){
            LatLng attractions = new LatLng(Double.parseDouble(RecyclerAttraction.nodesChosen.get(i).getLatitude()), Double.parseDouble(RecyclerAttraction.nodesChosen.get(i).getLongitude()));
            if(i == 0){
                marker = myMap.addMarker(new MarkerOptions().position(first).title("Start Location / End Location"));
            }
            else{
                marker = myMap.addMarker(new MarkerOptions().position(attractions).title("Location " + i + " :" + RecyclerAttraction.nodesChosen.get(i).getName()).snippet(RecyclerAttraction.nodesChosen.get(i).getSnippet()));
            }

        }
        // Add a marker in Singapore and move the camera
        LatLng singapore = new LatLng(1.2839, 103.8609);
        float zoomLevel = 10.0f;
        myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(singapore, zoomLevel));
        myMap.setInfoWindowAdapter(new PopupAdapter(getLayoutInflater()));
    }


    private class connectAsyncTask extends AsyncTask<Void, Void, String>{
        private ProgressDialog progressDialog;
        String url;
        connectAsyncTask(String urlPass){
            url = urlPass;
        }
        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            progressDialog = new ProgressDialog(ShowRoute.this);
            progressDialog.setMessage("Fetching route, Please wait...");
            progressDialog.setIndeterminate(true);
            progressDialog.show();
        }
        @Override
        protected String doInBackground(Void... params) {
            JSONParser jParser = new JSONParser();
            String json = jParser.getJSONFromUrl(url);
            return json;
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressDialog.hide();
            if(result!=null){
                drawPath(result);
            }
        }
    }

    public String makeURL (double sourcelat, double sourcelog, double destlat, double destlog ){
        String url = "https://maps.googleapis.com/maps/api/directions/json" +
                "?origin="+Double.toString(sourcelat)+","+Double.toString(sourcelog) +
                "&destination="+Double.toString(destlat)+","+Double.toString(destlog) +
                "&sensor=false" +
                "&mode=walking" +
                "&alternatives=true";

        return url;
    }


    public void drawPath(String result) {
        try {
            //Tranform the string into a json object
            final JSONObject json = new JSONObject(result);
            JSONArray routeArray = json.getJSONArray("routes");
            JSONObject routes = routeArray.getJSONObject(0);
            JSONObject overviewPolylines = routes.getJSONObject("overview_polyline");
            String encodedString = overviewPolylines.getString("points");
            List<LatLng> list = decodePoly(encodedString);
            PolylineOptions options = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true);
            for (int z = 0; z < list.size(); z++) {
                LatLng point = list.get(z);
                options.add(point);
            }
            line = myMap.addPolyline(options);
        }
        catch (JSONException e) {

        }
    }

    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng( (((double) lat / 1E5)),
                    (((double) lng / 1E5) ));
            poly.add(p);
        }

        return poly;
    }
}