package common;

import java.io.Serializable;

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
