package common;

import java.io.Serializable;
import java.net.InetAddress;

public class Heartbeat implements Serializable
{
    private final InetAddress address;
    private final int port;
    private String name;
    
    public Heartbeat(InetAddress addr, int p, String n){
        this.address = addr;
        this.port = p;
        this.name = n;
    }
    public InetAddress getAddr(){
        return address;
    }
    
    public int getPort(){
        return port;
    }
    
    public String getName(){
        return name;
    }
    
    public void setName(String n){
        name = n;
    }
}
