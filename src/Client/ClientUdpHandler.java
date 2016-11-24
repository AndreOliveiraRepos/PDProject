package Client;

import common.Msg;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

public class ClientUdpHandler{
    public static final int MAX_SIZE = 1000;
    protected DatagramSocket socket;
    protected DatagramPacket packet;
    
    InetAddress directoryServerAddr;
    Integer directoryServerPort;
    
    public ClientUdpHandler(InetAddress dirServerAddr, Integer dirServerPort){
        try {
            socket = new DatagramSocket(); //Não pode ser 6001, deve de ser () ... automático.
        } catch(SocketException e){
            System.out.println("[Receiver] Ocorreu um erro ao nível do socket UDP:\n\t"+e);
        }finally{
            System.out.println("Udp client port:\n\t"+socket.getLocalPort());
        }
        directoryServerAddr = dirServerAddr;
        directoryServerPort = dirServerPort;
    }
    
    public int getLocalPort(){
        return socket.getLocalPort();
    }
    
    public String sendRequest(Msg msg) throws IOException, ClassNotFoundException
    {
        ByteArrayOutputStream baos;
        ObjectOutputStream oOut;
        baos = new ByteArrayOutputStream();
        oOut = new ObjectOutputStream(baos);
        oOut.writeObject(msg);
        oOut.flush();

        packet = new DatagramPacket(baos.toByteArray(), baos.size(),
                directoryServerAddr, directoryServerPort);
        socket.send(packet);
        
        return getResponse();
    }
    
    protected String getResponse() throws IOException, ClassNotFoundException{
        ObjectInputStream in;
        Object obj;
        
        packet = new DatagramPacket(new byte[MAX_SIZE], MAX_SIZE);
        socket.receive(packet);

        in = new ObjectInputStream(new ByteArrayInputStream(packet.getData(), 0, packet.getLength()));
        obj = in.readObject();

        System.out.println("packet received...");

        /*if (obj instanceof Msg){
            Msg msg = (Msg)obj;
            System.out.println("Message: \n\t Nickname: "+msg.getName()
                                + "\n\t Text: "+msg.getMsg());

        } else*/ 
        if (!(obj instanceof String)){
            System.out.println("Erro: Objecto recebido do tipo inesperado!");
        }
        return (String)obj;
    }
    
    public void closeSocket(){
        socket.close();
    }
}
