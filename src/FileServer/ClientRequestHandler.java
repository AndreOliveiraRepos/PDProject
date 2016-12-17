/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package FileServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

/**
 *
 * @author luism
 */
public class ClientRequestHandler extends Thread{
    public static final int MAX_SIZE = 4000;
    
    Socket socketToClient;
    int myId;

    public ClientRequestHandler(Socket s, int id){
        socketToClient = s;
        myId = id;
    }
    
    @Override
    public void run(){
        BufferedReader in;
        OutputStream out;
        
        /*byte[]fileChunk = new byte[MAX_SIZE];
        int nbytes;*/
        
        String clientRequest = null;
        String resposta = null;
        
        try{
            // Streams de entrada e saída via TCP
            in = new BufferedReader(
                new InputStreamReader(
                    socketToClient.getInputStream()
                )
            );
            out = socketToClient.getOutputStream();
            
            clientRequest = in.readLine();
            System.out.println("Pedido recebido: "+clientRequest);
            
            // Enviar resposta ao cliente
            resposta = "O servidor recebeu o pedido" + clientRequest;
            out.write(resposta.getBytes(),0,resposta.length());
            out.flush();
            
        }catch(IOException e){
            System.out.println("Ocorreu a excepcao de E/S: \n\t" + e);
        }
        
        try{
             socketToClient.close();
        } catch (IOException ex) {}
    }
}
