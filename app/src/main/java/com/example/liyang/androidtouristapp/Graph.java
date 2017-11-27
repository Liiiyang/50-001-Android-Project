package com.example.liyang.androidtouristapp;

import java.util.ArrayList;

/**
 * Created by Kenny on 11/25/2017.
 */

public class Graph {
    public ArrayList<Pojo> nodes;
    public ArrayList<Edge> edges;
    public Graph(){
        nodes=new ArrayList<>();
        edges=new ArrayList<>();
    }
    public void addNodes(Pojo x){
        nodes.add(x);
    }
    public void addEdge(Pojo start,Pojo end,int weight){
        Edge y=new Edge(start,end,weight);
        edges.add(y);
    }

}
class Edge{
    Pojo startNode;
    Pojo endNode;
    int weigth;
    Edge(Pojo start,Pojo end,int weigth){
        this.startNode=start;
        this.endNode=end;
        this.weigth=weigth;
    }
}