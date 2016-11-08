
import java.io.Serializable;

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
