package com.example.liyang.androidtouristapp;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Frederic on 11/28/2017.
 */


public class Solver {
    public static ArrayList<Pojo> brute_force(ArrayList<Pojo> chosenPojos, String mode){
        ArrayList<Pojo> starting_path = new ArrayList<>();
        starting_path.add(chosenPojos.get(0));
        ArrayList<ArrayList<Pojo>> starting_paths = new ArrayList<>();
        starting_paths.add(starting_path);
        ArrayList<Integer> starting_cost = new ArrayList<>();
        starting_cost.add(0);
        return recursive_brute(starting_paths,starting_cost,chosenPojos,mode);
    }
    private static ArrayList<Pojo> recursive_brute(ArrayList<ArrayList<Pojo>> paths,
                                                   ArrayList<Integer> path_cost,
                                                   ArrayList<Pojo> chosenPojos,
                                                   String mode){

        ArrayList<ArrayList<Pojo>> new_paths = new ArrayList<>();
        ArrayList<Integer> new_costs = new ArrayList<>();
        for (int index = 0;index<paths.size();index++){
            ArrayList<Pojo> path = paths.get(index);
            Integer cost = path_cost.get(index);
            for (Pojo chosenPojo:chosenPojos){
                if (!path.contains(chosenPojo)){
                    ArrayList<Pojo> new_path = (ArrayList<Pojo>) path.clone();
                    Integer new_cost = cost;
                    if (mode.equals("bus"))
                        new_cost += path.get(path.size()-1).bus.get(chosenPojo.getId());
                    else if (mode.equals("walk"))
                        new_cost += path.get(path.size()-1).walk.get(chosenPojo.getId());
                    else
                        new_cost += path.get(path.size()-1).taxi.get(chosenPojo.getId());
                    new_path.add(chosenPojo);
                    new_paths.add(new_path);
                    new_costs.add(new_cost);
                }
            }
        }

        // End of recursion, close cycle and return
        if (new_paths.get(0).size()>=chosenPojos.size()){
            Integer min_final_cost = 30000;
            ArrayList<Pojo> best_path = null;
            for (int index = 0;index<paths.size();index++) {
                ArrayList<Pojo> curr_path = new_paths.get(index);
                Pojo lastPojo = curr_path.get(curr_path.size()-1);
                Pojo firstPojo = curr_path.get(0);
                Integer final_cost = new_costs.get(index);
                final_cost+=(mode.equals("bus"))?lastPojo.bus.get(firstPojo.getId()):(mode.equals("walk"))?lastPojo.walk.get(firstPojo.getId()):lastPojo.taxi.get(firstPojo.getId());
                curr_path.add(firstPojo);
                if (final_cost<min_final_cost) {
                    best_path = curr_path;
                    min_final_cost = final_cost;
                }
            }
            return best_path;
        } else {
            return recursive_brute(new_paths,new_costs,chosenPojos,mode);
        }
    }

    public static ArrayList<Pojo> greedy_solver(ArrayList<Pojo> chosenPojos, String mode) {
        ArrayList<Pojo> final_path = new ArrayList<>();
        final_path.add(chosenPojos.get(0));
        for (int counter=0;counter<chosenPojos.size();counter++){
            Pojo start = final_path.get(0);
            Pojo end = final_path.get(final_path.size()-1);
            int min_cost = 30000;
            Pojo next = null;
            boolean append_to_start = false;
            for (Pojo adj:chosenPojos){
                if (!final_path.contains(adj)){
                    if (mode.equals("bus")){
                        int cost = start.bus.get(adj.getId());
                        if (cost <min_cost){
                            min_cost = cost;
                            next = adj;
                            append_to_start = true;
                        }
                        if (end!=start){
                            cost = end.bus.get(adj.getId());
                            if (cost <min_cost){
                                min_cost = cost;
                                next = adj;
                                append_to_start = false;
                            }
                        }
                    } else if (mode.equals("walk")){
                        int cost = start.walk.get(adj.getId());
                        if (cost <min_cost){
                            min_cost = cost;
                            next = adj;
                            append_to_start = true;
                        }
                        if (end!=start){
                            cost = end.walk.get(adj.getId());
                            if (cost <min_cost){
                                min_cost = cost;
                                next = adj;
                                append_to_start = false;
                            }
                        }
                    } else {
                        int cost = start.taxi.get(adj.getId());
                        if (cost <min_cost){
                            min_cost = cost;
                            next = adj;
                            append_to_start = true;
                        }
                        if (end!=start){
                            cost = end.taxi.get(adj.getId());
                            if (cost <min_cost){
                                min_cost = cost;
                                next = adj;
                                append_to_start = false;
                            }
                        }
                    }
                }
            }
            if (next!=null){
                if (append_to_start)
                    final_path.add(0,next);
                else
                    final_path.add(next);
            } else {
                // End of loop
                final_path.add(final_path.get(0));
                break;
            }
        }
        return final_path;
    }

    public static void main(String[] args) {
        // Test case
        /*
        ArrayList<Pojo> testCase1 = new ArrayList<>();
        HashMap<String,Integer> walkMap = new HashMap<>();
        walkMap.put("B",2);
        walkMap.put("C",3);
        walkMap.put("D",4);
        walkMap.put("E",400);
        testCase1.add(new Pojo("A","A","","","","","",walkMap,new HashMap<String, Integer>(),new HashMap<String, Integer>(),new HashMap<String, Integer>(),new HashMap<String, Integer>()));
        walkMap = new HashMap<>();
        walkMap.put("A",2);
        walkMap.put("C",4);
        walkMap.put("D",3);
        testCase1.add(new Pojo("B","B","","","","","",walkMap,new HashMap<String, Integer>(),new HashMap<String, Integer>(),new HashMap<String, Integer>(),new HashMap<String, Integer>()));
        walkMap = new HashMap<>();
        walkMap.put("A",3);
        walkMap.put("B",4);
        walkMap.put("D",20);
        testCase1.add(new Pojo("C","C","","","","","",walkMap,new HashMap<String, Integer>(),new HashMap<String, Integer>(),new HashMap<String, Integer>(),new HashMap<String, Integer>()));
        walkMap = new HashMap<>();
        walkMap.put("A",4);
        walkMap.put("B",30);
        walkMap.put("C",20);
        testCase1.add(new Pojo("D","D","","","","","",walkMap,new HashMap<String, Integer>(),new HashMap<String, Integer>(),new HashMap<String, Integer>(),new HashMap<String, Integer>()));
        ArrayList<Pojo> result = greedy_solver(testCase1,"walk");
        for (Pojo p:result){
            System.out.print(p.getId());
        }*/

    }
}

