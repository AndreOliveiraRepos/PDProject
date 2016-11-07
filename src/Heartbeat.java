
import java.io.Serializable;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

public class Heartbeat implements Serializable{
    private final int TCPPort;
    private final String name;
    
    public Heartbeat(int p, String n){
        this.TCPPort = p;
        this.name = n;
    }
    
    public int getTCPPort(){
        return TCPPort;
    }
    
    public String getName(){
        return name;
    }
}
