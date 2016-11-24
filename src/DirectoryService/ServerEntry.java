/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DirectoryService;

import common.Heartbeat;
import java.util.ArrayList;

public class ServerEntry extends common.Entry{
    private ArrayList connectedClients;
    public ServerEntry(Heartbeat hb){
        // Cria uma entrada de registo selando-a com o tempo actual.
        super(hb);
    }
}
