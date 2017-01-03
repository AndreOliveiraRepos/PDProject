/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DirectoryService.Manager;

import common.Heartbeat;
import java.net.InetAddress;

public class ClientEntry extends common.Entry{
        
    public ClientEntry(Heartbeat hb, InetAddress hbAddr) {
        super(hb, hbAddr);
    }
    
    @Override
    public String toString(){
        return name;
    }
}
