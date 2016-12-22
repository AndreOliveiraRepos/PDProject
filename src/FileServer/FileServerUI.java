/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package FileServer;

import common.HeartbeatSender;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 *
 * @author André Oliveira
 */
public class FileServerUI {
    
    public static void main(String[] args) {
        
          
        if(args.length != 3){
            System.out.println("Sintaxe: java FileServer serverName dirServerAddress dirServerUdpPort");
            return;
        }
        
        //Liga ao serviço de directoria e vê se não existe mais nenhum servidor com o mesmo nome
            
        try {    
            FileServer fserver = new FileServer(args[0],
                    InetAddress.getByName(args[1]),
                    Integer.parseInt(args[2])
            );
            
            
            fserver.goOnline();
            
            //Inicializar heartbeat/Packets UDP
            
              
        } catch (UnknownHostException ex) {
            System.out.println("Destino desconhecido:\n\t"+ex);
        } 
    }
    
    
}
