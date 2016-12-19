/*
* Entrada de registo.
*/
package common;

import java.net.InetAddress;

public class Entry {
    protected String name;
    protected InetAddress address;
    protected int port;
    protected long timestamp;
    
    public Entry(String n, InetAddress addr, int p, long t){
        name = n;
        address = addr;
        port = p;
        timestamp = t;
    }
    
    public Entry(Heartbeat hb){
        name = hb.getName();
        address = hb.getAddr();
        port = hb.getPort();
        timestamp = System.nanoTime();
    }
    
    public String getName(){
        return name;
    }
    
    public InetAddress getAddr(){
        return address;
    }
    
    public int getPort(){
        return port;
    }
    
    public long getTimestamp(){
        return timestamp;
    }
}