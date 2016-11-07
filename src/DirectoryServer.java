
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

public class DirectoryServer {
    
    private static UDPReceiver udpReceiver;
    
    public static void main(String[] args) {
        udpReceiver = new UDPReceiver();
        udpReceiver.start();
    }
}
