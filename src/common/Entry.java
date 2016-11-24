/*
* Entrada de registo.
*/
package common;

public class Entry {
    protected String name;
    protected int port;
    protected long timestamp;
    
    public Entry(String n, int p, long t){
        name = n;
        port = p;
        timestamp = t;
    }
    
    public Entry(Heartbeat hb){
        name = hb.getName();
        port = hb.getPort();
        timestamp = System.nanoTime();
    }
    
    public String getName(){
        return name;
    }
    
    public int getPort(){
        return port;
    }
    
    public long getTimestamp(){
        return timestamp;
    }
}