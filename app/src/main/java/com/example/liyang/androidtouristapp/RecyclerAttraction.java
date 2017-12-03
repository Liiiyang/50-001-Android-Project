package com.example.liyang.androidtouristapp;

import android.content.Intent;
import android.os.Bundle;
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
    public static ArrayList<Pojo> nodesChosen= new ArrayList<>();
    Pojo hotel;

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
                //TODO Kenny:change to brian codes
                // adding nodes to graphs that are checked.
                // passing array list of checked pojo to next page
                nodesChosen.add(hotel);
                for (Pojo x : mRecyclerViewItems) {
                    if (x.isSelected()) {
                        nodesChosen.add(x);
                    }
                }
                Intent attractionSelectionPage = new Intent(this, AttractionSelection.class);
                //TODO Kenny:need to PojoArraylist nodesChosen over to next intent
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("Birds", nodesChosen);
                attractionSelectionPage.putExtras(bundle);

                startActivity(attractionSelectionPage);
                //Need to use this function for next page ArrayList<Pojo> result = greedy_solver(testCase1,"walk");
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
                String attractionlongitude = attractionObject.getString("longitude");
                String attractionlatitude = attractionObject.getString("latitude");
                String attractionaddress = attractionObject.getString("address");
                String attractionadultfee = attractionObject.getString("adultfee");
                String attractionchildfee = attractionObject.getString("childfee");
                String attractionImageName = attractionObject.getString("photo");
                String attractionSnippet = attractionObject.getString("snippet");

                //extract json bus,walk and taxi edge and weight + bus price and taxi price
                HashMap<String,Integer> walkEdgeList=new HashMap<>();
                HashMap<String,Integer> busEdgeList=new HashMap<>();
                HashMap<String,Integer> taxiEdgeList=new HashMap<>();
                HashMap<String,Integer> taxiPrice=new HashMap<>();
                HashMap<String,Integer> busPrice=new HashMap<>();

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
                JSONArray tempTaxiPrice = attractionObject.getJSONArray("taxiprice");
                int lengthTP = tempTaxiPrice.length();
                if (lengthTP > 0) {
                    for (int j = 0; j < lengthTP; j=j+2) {
                        taxiPrice.put(tempTaxiPrice.getString(j),tempTaxiPrice.getInt(j+1));
                    }
                }
                JSONArray tempBusPrice = attractionObject.getJSONArray("busprice");
                int lengthBP = tempBusPrice.length();
                if (lengthBP > 0) {
                    for (int j = 0; j < lengthBP; j=j+2) {
                        busPrice.put(tempBusPrice.getString(j),tempBusPrice.getInt(j+1));
                    }
                }
                //Toast.makeText(getApplicationContext(), busPrice.get("GBB").toString(), Toast.LENGTH_SHORT).show(); //debug
                Pojo pojo = new Pojo(attractionid,attractionName,attractionlatitude, attractionlongitude,attractionaddress,attractionadultfee,
                        attractionchildfee,attractionImageName,attractionSnippet,walkEdgeList,busEdgeList,taxiEdgeList,busPrice,taxiPrice);
                //TODO Kenny Added to prevent hotel from being added
                if(!pojo.getId().equals("HOTEL")){
                mRecyclerViewItems.add(pojo);}
                else{hotel=pojo;// store in global variable so we can access when button pressed (onOption Item select)
                }
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

