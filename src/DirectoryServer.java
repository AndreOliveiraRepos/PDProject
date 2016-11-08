
import java.util.ArrayList;


/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

public class DirectoryServer {
    
    private static UDPReceiver udpReceiver;
    
    private static ArrayList connectedServers;
    private static ArrayList connectedClients;
    
    public static void main(String[] args) {
        udpReceiver = new UDPReceiver();
        udpReceiver.start();
    }
}
