package com.example.liyang.androidtouristapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
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
 * Created by Li Yang on 21/11/2017.
 */

public class AttractionSelection extends AppCompatActivity {
    ArrayList<Pojo> total=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attraction_selection);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.setTitle("Select Your Budget");
        final TextView text = (TextView) findViewById(R.id.calculate);
        final Button calroute = (Button) findViewById(R.id.go_button);
        final Button route=(Button) findViewById(R.id.next_button);
        //TODO Kenny added widgets required
        final EditText budgetWidget=(EditText) findViewById(R.id.entercost);
        final RadioGroup algo=(RadioGroup)findViewById(R.id.radioGroup);
        final RadioButton bruteForce=(RadioButton) findViewById(R.id.radioButton);
        final RadioButton fastApprox=(RadioButton) findViewById(R.id.radioButton2);
        //TODO kenny add get intent codes ArrayList<Pojo> nodesChosen from recycler view page  ***
        final ArrayList<Pojo> nodesChosen1 = this.getIntent().getExtras().getParcelableArrayList("Birds");
        final ArrayList<Pojo> nodesChosen2 =new ArrayList<>(); //place holder for nodes chosen
        //ArrayList<Pojo> fastestNodesRoute1= Solver.brute_force(nodesChosen1,"walk");
        //debug to send nodesChosen1 from previous activity success
        addNodesChosen1();//add total pojos objects from json again
        String test="This are the Location Chosen\n";
        String test1="\n";
        ArrayList<String> nodesChosen1id=new ArrayList<>();
        for (Pojo p:nodesChosen1){
            nodesChosen1id.add(p.getId());
        }
        //rebuilt nodesChosen
        for(Pojo p:total){
            for(String x:nodesChosen1id){
                if(x.equals(p.getId())){
                    nodesChosen2.add(p);
                }
            }
        }
        test="\n This is Nodes chosen\n";
        test1="\n\nBus Timings";
        for (Pojo p:nodesChosen2){
            test+=p.getName()+" : ";
            nodesChosen1id.add(p.getId());
            for(Pojo p1:nodesChosen2) {
                try {
                    test1+="\n"+p.getName()+"->"+p1.getName()+" : "+p.getBus().get(p1.getName()).toString();
                }
                catch(Exception e){

                }
            }
        }
        Toast.makeText(getApplicationContext(), test+test1, Toast.LENGTH_LONG).show();
        Toast.makeText(getApplicationContext(), test+test1, Toast.LENGTH_LONG).show();
        //Example Case nodesChosen
        final ArrayList<Pojo> nodesChosen =new ArrayList<>(); //place holder for nodes chosen
        HashMap<String,Integer> walkMap = new HashMap<>();
        walkMap.put("JPK",2);
        walkMap.put("GBB",3);
        walkMap.put("FCP",4);
        walkMap.put("MBS",400);
        nodesChosen.add(new Pojo("HOTEL","HOTEL","","","","","","","",walkMap,walkMap,walkMap,walkMap,walkMap));
        walkMap = new HashMap<>();
        walkMap.put("HOTEL",2);
        walkMap.put("GBB",4);
        walkMap.put("FCP",3);
        walkMap.put("MBS",3);
        nodesChosen.add(new Pojo("JPK","JPK","","","","","","","",walkMap,walkMap,walkMap,walkMap,walkMap));;
        walkMap = new HashMap<>();
        walkMap.put("HOTEL",2);
        walkMap.put("GBB",4);
        walkMap.put("JPK",3);
        walkMap.put("MBS",3);
        nodesChosen.add(new Pojo("FCP","FCP","","","","","","","",walkMap,walkMap,walkMap,walkMap,walkMap));
        walkMap = new HashMap<>();
        walkMap.put("HOTEL",2);
        walkMap.put("FCP",4);
        walkMap.put("JPK",3);
        walkMap.put("MBS",3);
        nodesChosen.add(new Pojo("GBB","GBB","","","","","","","",walkMap,walkMap,walkMap,walkMap,walkMap));


        //calroute.setTag(1);
        calroute.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                //TODO Kenny:Cal route, determine which route chosen and see if within budget
                ArrayList<Pojo> fastestNodesRoute=null;//placeholder for now
                String routeMessage=""; //Routes
                // get fast or brute force returns an arrayList<Pojo> feed in (nodesChosen, mode)
                if(algo.getCheckedRadioButtonId()==-1){
                    //no radio buttons are checked
                    //toast pls check radio button
                    Toast.makeText(getApplicationContext(), "Pls select an algorithm", Toast.LENGTH_SHORT).show();
                }
                else if(bruteForce.isChecked()){
                    //bruteforce
                    fastestNodesRoute= Solver.brute_force(nodesChosen2,"walk"); //change to nodesChosen to see example case
                    //debug to show route
                    for (Pojo p:fastestNodesRoute){
                        routeMessage+=p.getName()+"->";
                    }
                }
                else{
                    //fastApprox
                    fastestNodesRoute= Solver.greedy_solver(nodesChosen2,"walk"); //change to nodeChosen to see example case
                    // debug to show route
                    for (Pojo p:fastestNodesRoute){
                        routeMessage+=p.getName()+"->";
                    }
                }

                // with Pojo array calcualte taxi $$/ bus $$ / walk $$

                Pojo prevPojo=null;
                int cost=0;
                int time=0;
                String mode="taxi";
                for(Pojo currPojo:fastestNodesRoute){
                    if(!(prevPojo==null)){
                        cost+=prevPojo.taxiPrice.get(currPojo.getId());
                        time+=prevPojo.taxi.get(currPojo.getId());
                    }
                    prevPojo=currPojo;
                }
                // check if within taxi budget else chose bus else walk
                Double budget=Double.parseDouble(budgetWidget.getText().toString());
                if(budget-cost<0){
                    //cost is exceed budget
                    //check time and cost for bus route
                    prevPojo=null;
                    time = 0;
                    cost = 0;
                    mode="bus";
                    for (Pojo currPojo : fastestNodesRoute) {
                        if (!(prevPojo==null)) {
                            cost += prevPojo.busPrice.get(currPojo.getId());
                            time += prevPojo.bus.get(currPojo.getId());
                        }
                        prevPojo = currPojo;
                    }// calculate cost for bus
                }
                if(budget-cost<0) {
                    //cost is exceed budget
                    //check time and cost for walk route
                    time = 0;
                    cost = 0;
                    mode = "walk";
                    prevPojo=null;
                    for (Pojo currPojo : fastestNodesRoute) {
                        if (!(prevPojo==null)) {
                            time += prevPojo.walk.get(currPojo.getId());
                        }
                        prevPojo = currPojo;
                    }
                }
                //TODO LI YANG: send info to showRoute activity
                // go to next intent pass extras location nodes, cost, time, mode is taxi
                String message="";
                message=routeMessage+"\n"+"Cost: $"+cost+"| Time: "+ time+"sec |Transport by: "+mode
                        + "| Attraction Selected: " + RecyclerViewAdapter.count;
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                showroute((View)calroute);

                }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        //handle presses on the action bar items
        switch (item.getItemId()) {

            case android.R.id.home:
                onBackPressed();
                return true;
                }
        return super.onOptionsItemSelected(item);
    }

    public void showroute(View view){
        Intent ShowRoute = new Intent(this, com.example.liyang.androidtouristapp.ShowRoute.class);
        startActivity(ShowRoute);
    }

    private void addNodesChosen1() {
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
                Pojo pojo = new Pojo(attractionid,attractionName,attractionlatitude, attractionlongitude,attractionaddress,attractionadultfee,
                        attractionchildfee,attractionImageName,attractionSnippet,walkEdgeList,busEdgeList,taxiEdgeList,busPrice,taxiPrice);
                //TODO Kenny Added to prevent hotel from being added
                    total.add(pojo);
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
