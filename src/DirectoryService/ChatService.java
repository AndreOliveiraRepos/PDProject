/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DirectoryService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

/**
 *
 * @author luism
 */
public class ChatService
{    
    private DatagramSocket socket;
    
    public ChatService()
    {
        try {
            socket = new DatagramSocket();
            socket.setSoTimeout(1500);
        } catch (SocketException ex) {
            System.out.println("Chat Service - Erro a nivel do socket!" + ex);
        }
    }
    
    public void sendMessage(String msg, InetAddress addr, int p)
    {
        ByteArrayOutputStream baos;
        ObjectOutputStream oOut;
        
        try {
            if (socket == null) {
                System.out.println("Socket Error");
                return;
            }
            
            baos = new ByteArrayOutputStream();
            oOut = new ObjectOutputStream(baos);
            oOut.writeObject(msg);
            oOut.flush();
            
            DatagramPacket packet = new DatagramPacket(baos.toByteArray(), baos.size(), addr, p);
            socket.send(packet);
        } catch (IOException ex) {
            System.out.println("Chat Service - Erro ao enviar mensagem" + ex);
        }
    }
}
