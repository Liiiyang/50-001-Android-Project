package com.example.liyang.androidtouristapp;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by Li Yang on 22/11/2017.
 */

public class RecyclerAttraction extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private ArrayList<Pojo> mRecyclerViewItems = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recycler_setup);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        RecyclerView.Adapter adapter = new RecyclerViewAdapter(this, mRecyclerViewItems);
        mRecyclerView.setAdapter(adapter);

        addattractionFromJson();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.recycler_next_activity, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        //handle presses on the action bar items
        switch (item.getItemId()) {

            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.selectconstraints:
                // adding nodes to graphs that are checked.
                Graph walkGraph = new Graph();
                Graph busGraph = new Graph();
                Graph taxiGraph = new Graph();
                for (Pojo x : mRecyclerViewItems) {
                    if (x.isSelected()) {
                        Toast.makeText(this, x.getName(), Toast.LENGTH_SHORT).show();
                        walkGraph.addNodes(x);
                        busGraph.addNodes(x);
                        taxiGraph.addNodes(x);
                    }
                }
                //adding edges and weigth to walk,bus and taxi graphs
                for (Pojo x : walkGraph.nodes) {
                    for (Pojo y : walkGraph.nodes) {
                        for (String z : x.walk.keySet())
                            if (z.equals(y.getId())) {
                                walkGraph.addEdge(x, y, x.walk.get(z));
                            }
                    }
                }
                for (Pojo x : busGraph.nodes) {
                    for (Pojo y : busGraph.nodes) {
                        for (String z : x.bus.keySet())
                            if (z.equals(y.getId())) {
                                busGraph.addEdge(x, y, x.bus.get(z));
                            }
                    }
                }
                for (Pojo x : taxiGraph.nodes) {
                    for (Pojo y : taxiGraph.nodes) {
                        for (String z : x.taxi.keySet())
                            if (z.equals(y.getId())) {
                                taxiGraph.addEdge(x, y, x.taxi.get(z));
                            }
                    }
                }
                startActivity(new Intent(this, AttractionSelection.class));
                return true;
        }
        return super.onOptionsItemSelected(item);}

    private void addattractionFromJson() {
        try {
            String jsonDataString = readJsonDataFromFile();
            JSONArray attractionJsonArray = new JSONArray(jsonDataString);

            for (int i = 0; i < attractionJsonArray.length(); ++i) {

                JSONObject attractionObject = attractionJsonArray.getJSONObject(i);

                String attractionid = attractionObject.getString("id");
                String attractionName = attractionObject.getString("name");
                String attractionlocation = attractionObject.getString("location");
                //String attractionroute = attractionObject.getString("route");
                String attractionaddress = attractionObject.getString("address");
                String attractionadultfee = attractionObject.getString("adultfee");
                String attractionchildfee = attractionObject.getString("childfee");
                String attractionImageName = attractionObject.getString("photo");

                //extract json bus,walk and taxi edge and weight
                HashMap<String,Integer> walkEdgeList=new HashMap<>();
                HashMap<String,Integer> busEdgeList=new HashMap<>();
                HashMap<String,Integer> taxiEdgeList=new HashMap<>();

                JSONArray tempWalk = attractionObject.getJSONArray("walk");
                int lengthW = tempWalk.length();
                if (lengthW > 0) {
                    for (int j = 0; j < lengthW; j=j+2) {

                        walkEdgeList.put(tempWalk.getString(j),tempWalk.getInt(j+1));
                    }
                }
                JSONArray tempBus = attractionObject.getJSONArray("bus");
                int lengthB = tempBus.length();
                if (lengthB > 0) {
                    for (int j = 0; j < lengthB; j=j+2) {
                        busEdgeList.put(tempBus.getString(j),tempBus.getInt(j+1));
                    }
                }
                JSONArray tempTaxi = attractionObject.getJSONArray("taxi");
                int lengthT = tempTaxi.length();
                if (lengthT > 0) {
                    for (int j = 0; j < lengthT; j=j+2) {
                        taxiEdgeList.put(tempTaxi.getString(j),tempTaxi.getInt(j+1));
                    }
                }
                Pojo pojo = new Pojo(attractionid,attractionName, attractionlocation,attractionaddress,attractionadultfee,
                        attractionchildfee,attractionImageName,walkEdgeList,busEdgeList,taxiEdgeList);
                mRecyclerViewItems.add(pojo);
            }
        } catch (IOException | JSONException exception) {
            Log.e(RecyclerAttraction.class.getName(), "Unable to parse JSON file.", exception);
        }
    }

    private String readJsonDataFromFile() throws IOException {

        InputStream inputStream = null;
        StringBuilder builder = new StringBuilder();

        try {
            String jsonDataString = null;
            inputStream = getResources().openRawResource(R.raw.android_project_test1);
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(inputStream, "UTF-8"));
            while ((jsonDataString = bufferedReader.readLine()) != null) {
                builder.append(jsonDataString);
            }
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }

        return new String(builder);
    }

}

