
import java.util.ArrayList;


/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

public class DirectoryServer {
    
    private static UdpListener udpReceiver;
    
    private static ArrayList connectedServers;
    
    public static void main(String[] args) {
        startUdpListener();
    }
    
    public static void startUdpListener(){
        udpReceiver = new UdpListener();
        udpReceiver.start();
    }
}
