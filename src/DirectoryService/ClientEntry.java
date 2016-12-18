/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DirectoryService;

import common.Heartbeat;
import java.net.InetAddress;

/**
 *
 * @author luism
 */
public class ClientEntry extends common.Entry{
    
    private InetAddress cliAddr;
    
    public ClientEntry(Heartbeat hb) {
        super(hb);
    }
    
    public InetAddress getClientAddr(){
        return cliAddr;
    }
}
