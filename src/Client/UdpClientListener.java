package Client;

import common.Msg;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

public class UdpClientListener extends Thread{
    public static final int MAX_SIZE = 1000;
    public static final String LIST = "LIST";
    DatagramSocket socket;
    
    /**
     * Inicializa o DatagramSocket caso este não tenha sido passado pelo construtor
     */
    public UdpClientListener(){
        try {
            socket = new DatagramSocket(); //Não pode ser 6001, deve de ser () ... automático.
        } catch(SocketException e){
            System.out.println("[Receiver] Ocorreu um erro ao nível do socket UDP:\n\t"+e);
        }finally{
            System.out.println("Udp server port:\n\t"+socket.getLocalPort());
        }
    }
    
    public UdpClientListener(DatagramSocket s){
            socket = s;
    }
    
    @Override
    public void run(){
        DatagramPacket packet = null;
        
        ObjectInputStream in;
        Object obj;
        
        ByteArrayOutputStream baos = null; //buff
        ObjectOutputStream oos = null;     //out
        
        try{
            
            while(true){
            
                packet = new DatagramPacket(new byte[MAX_SIZE], MAX_SIZE);
                socket.receive(packet);

                in = new ObjectInputStream(new ByteArrayInputStream(packet.getData(), 0, packet.getLength()));
                obj = in.readObject();
                
                System.out.println("packet received...");
                
                if (obj instanceof Msg){
                    Msg msg = (Msg)obj;
                    System.out.println("Message: \n\t Nickname: "+msg.getNickname()
                                        + "\n\t Text: "+msg.getMsg());
                    
                    if(msg.getMsg().equalsIgnoreCase(LIST)){
                        //Envia de volta ao cliente uma listagem dos servidores activos
                        baos = new ByteArrayOutputStream();
                            oos =  new ObjectOutputStream(baos);
                            
                            String r = "\n\t Objecto com listagem de servers activos/n";
                            oos.writeObject(r);
                            
                            oos.flush();
                            oos.close();
                
                            //dgram = new DatagramPacket(Buf, Buf.length, group, port);
                            
                            packet.setData(baos.toByteArray());
                            packet.setLength(MAX_SIZE);
                            
                            socket.send(packet);
                    }
                } else if (obj instanceof String){
                    System.out.println((String)obj);
                }
                obj = null; 
            } // Ciclo while           
        }catch(UnknownHostException e){
             System.out.println("Destino desconhecido:\n\t"+e);
        }catch(SocketTimeoutException e){
            System.out.println("Não foi recebida qualquer resposta:\n\t"+e);
        }catch(SocketException e){
            System.out.println("Ocorreu um erro ao nível do socket UDP:\n\t"+e);
        }catch(IOException e){
            System.out.println("Ocorreu um erro no acesso ao socket:\n\t"+e);
        }catch(ClassNotFoundException e){
             System.out.println("O objecto recebido não é do tipo esperado:\n\t"+e);
        }finally{
            if(socket != null){
                socket.close();
            }
        }
    }
    
    public int getLocalPort(){
        return socket.getLocalPort();
    }
}
