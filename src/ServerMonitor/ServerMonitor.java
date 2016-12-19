package ServerMonitor;

import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerMonitor {
    
    public static void main(String[] args) {
        
        try {
            if (args.length != 2) {
                System.out.println("Sintaxe: java ServerMonitor <DirectoryServer ip> <DirectoryServer Port>\n");
                return;
            }
            
            ServerMonitor view = new ServerMonitor();
            
            //Lança o serviço de directoria e pede para ser adicionado aos observadores.
            ServerMonitorClient serverMonitor = new ServerMonitorClient(args[0], Integer.parseInt(args[1]), view);
            
        } catch (RemoteException ex) {
            Logger.getLogger(ServerMonitor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void updateView(String s){
        System.out.println(s);
    }
}
