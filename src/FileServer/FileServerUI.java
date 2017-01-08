/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package FileServer;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class FileServerUI {
    
    public static void main(String[] args) {
        
          
        if(args.length != 3){
            System.out.println("Sintaxe: java FileServer serverName dirServerAddress dirServerUdpPort");
            return;
        }
        
        //Liga ao serviço de directoria e vê se não existe mais nenhum servidor com o mesmo nome    
        
        try {    
            String serverName = args[0];
            InetAddress dirAddr = InetAddress.getByName(args[1]);
            Integer dirPort = Integer.parseInt(args[2]);
            
            FileServer fserver = new FileServer(serverName, dirAddr, dirPort);
            
            if (!fserver.isDuplicatedName(serverName,dirAddr,dirPort)){
                fserver.goOnline();
            } else {
                System.out.println("Erro: Já existe um servidor ligado com o mesmo nome!");
            }
            //Inicializar heartbeat/Packets UDP
            
              
        } catch (UnknownHostException ex) {
            System.out.println("Destino desconhecido:\n\t"+ex);
        } 
    }
    
    
}
