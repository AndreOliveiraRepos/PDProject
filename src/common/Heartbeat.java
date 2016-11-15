package common;


import java.io.Serializable;
import java.util.ArrayList;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

public class Heartbeat implements Serializable{
    private final int port;
    private final String name;
    
    public Heartbeat(int p, String n){
        this.port = p;
        this.name = n;
    }
    
    public int getPort(){
        return port;
    }
    
    public String getName(){
        return name;
    }
}

class ExtendedHeartbeat extends Heartbeat implements Serializable{
    ArrayList connectedClients;
    public ExtendedHeartbeat(int p, String n, ArrayList clients){
        super(p,n);
        this.connectedClients = new ArrayList<>(clients);
    }
    
    public ArrayList getConnectedClients(){
        return connectedClients;
    }
}
