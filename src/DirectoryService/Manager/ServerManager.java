/*
 * Classe que gere os servidores activos.
 */
package DirectoryService.Manager;

import FileServer.ServerHeartbeat;
import java.net.InetAddress;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ServerManager extends Thread
{
    public static final int ACCEPTED_INTERVAL = 34; //seconds //34
    
    private HashMap<String,ServerEntry> onlineServers;
    private boolean running;
    private boolean hasChanges;
    
    public ServerManager()
    {
        onlineServers = new HashMap<String,ServerEntry>();
        running = true;
        hasChanges = false;
    }
    
    public boolean hasChanges(){
        if (hasChanges){
            hasChanges = false;
            return true;
        }
        return false;
    }
    
    public void processHeartbeat(ServerHeartbeat hb, InetAddress hbAddr) throws RemoteException{
        Integer previousCount = onlineServers.size();
        ServerEntry se = new ServerEntry(hb, hbAddr);
        onlineServers.put(se.getName(),se);
        if (!(previousCount.equals(onlineServers.size())))
            hasChanges = true;
    }
    
    @Override
    public void run(){
        while(running){
            if (!onlineServers.isEmpty()){
                Iterator it = onlineServers.entrySet().iterator();
                System.out.println("Online Servers: ");
                while (it.hasNext()) {
                    Map.Entry<String,ServerEntry> entry = (Map.Entry)it.next();  
                    
                    System.out.print("" + entry.getKey() /*+ " = " + entry.getValue()*/);
                    double timestampSeconds = (double)entry.getValue().getTimestamp() / 1000000000.0;
                    double systemSeconds = (double)System.nanoTime() / 1000000000.0;
                    System.out.print("\t Ultimo hb: "+(systemSeconds - timestampSeconds)+ " segundos.\n");
                    
                    System.out.print("Clients: { ");
                    for(String client : entry.getValue().getConnectedClients()){
                        System.out.print(client + " ");
                    }
                    System.out.println("}\n");
                    
                    if((systemSeconds - timestampSeconds) > ACCEPTED_INTERVAL){
                        it.remove();
                        hasChanges = true;
                        //onlineServers.remove(entry.getKey());
                    }
                    //it.remove(); //iterador
                }
            }
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ex) {
                System.out.println("Erro durante o sleep da thread que gere os servidores! "+ex);
            }
        }
    }
    
    public void stopThread(){
        running = false;
    }
    
    public ArrayList<ServerEntry> getServerList(){
        //return onlineServers.values();
        ArrayList<ServerEntry> arr = new ArrayList<ServerEntry>();
        Iterator it = onlineServers.values().iterator();
        while(it.hasNext()) arr.add((ServerEntry)it.next());
        return arr;
    }
    
    public String getServerListAsString(){
        StringBuilder buff = new StringBuilder();
        Iterator it = onlineServers.values().iterator();
        buff.append("\n");
        while (it.hasNext()) {
            ServerEntry se = (ServerEntry)it.next();
            //buff.append(entry.getKey() + " - " + entry.getValue().getPort() + "\n");
            buff.append(se.getName() + "\t" + se.getAddr().getHostAddress() + "\t" + se.getPort() + "\n");
        }
        return buff.toString();
    }
    
    @Override
    public String toString(){
        return onlineServers.toString();
    }
    
    public boolean isAuthenticatedClient(String c){
        for (ServerEntry se : onlineServers.values()){
            if (se.existsClient(c)) return true;
        }
        return false;
    }
    
    public boolean existsServer(String s){
        return onlineServers.containsKey(s);
    }
    
//    public Map<String,Integer> getServerMap(){
////        StringBuilder buffer = new StringBuilder();
////        Iterator it = onlineServers.entrySet().iterator();
////        while (it.hasNext()) {
////            Map.Entry<String,ServerEntry> entry = (Map.Entry)it.next();  
////
////            System.out.print("" + entry.getKey() /*+ " = " + entry.getValue()*/);
////            buffer.append(entry.getKey() + " - " + entry.getValue().getPort() + "\n");
////        }
////        return buffer.toString();*/
//        Map<String,Integer> serversInfo = new HashMap<String,Integer>();
//
//        Iterator it = onlineServers.entrySet().iterator();
//        while (it.hasNext()) {
//            Map.Entry<String,ServerEntry> entry = (Map.Entry)it.next();
//
//            //System.out.print("" + entry.getKey() /*+ " = " + entry.getValue()*/);
//            //buffer.append(entry.getKey() + " - " + entry.getValue().getPort() + "\n");
//            serversInfo.put(entry.getKey(), entry.getValue().getPort());
//        }
//        return serversInfo;
//    }
}
