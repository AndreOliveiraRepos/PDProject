package Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientTcpHandler {
    public static final int TIMEOUT = 5000; //5 segs
    
    protected InetAddress serverAddress;
    protected int serverPort;
    protected Socket socketToServer;
    
    public ClientTcpHandler(/*InetAddress servAddr, Integer servPort*/){
        /*serverAddress = servAddr;
        serverPort = servPort;
        
        try {
            socketToServer = new Socket(servAddr, servPort);
            socketToServer.setSoTimeout(TIMEOUT);
            socketToServer.
            
        } catch(IOException e){
            System.out.println("Ocorreu um erro no acesso ao socket" + ":\n\t"+e);
        }*/
        socketToServer = null;
    }
    public boolean connectToServer(InetAddress servAddr, Integer servPort){
        try {
            if(socketToServer != null){
                socketToServer.close();
            }
            socketToServer = new Socket(servAddr, servPort);
            return true;
        } catch (IOException e) {
            System.out.println("Ocorreu um erro no acesso ao socket TCP" + ":\n\t"+e);
            return false;
        }
    }
    
    public String sendRequest(String request) throws IOException{
        PrintWriter pout;
        
        try {
            if(socketToServer == null) return null;
            pout = new PrintWriter(socketToServer.getOutputStream(), true);
            pout.println(request);
            pout.flush();
        } catch (IOException ex) {
            System.out.println("Erro: Não foi possível enviar os dados ao servidor via TCP!");
        }
        return getResponse();
    }
    
    public String getResponse() throws IOException{
        // Receber a resposta do servidor (TCP)
            
            //DEBUG - ver se o tcp está funcional
            /* O servidor reve de devolver respostas baseadas nos comandos a ele enviados,
            deve também ser possível receber ficheiros nbytes = in.read(fileChunk);
            ver os exemplos do marinho
            */
            //String resposta;
            BufferedReader in_ = new BufferedReader(
                    new InputStreamReader(socketToServer.getInputStream())
            );
            
            //resposta = in_.readLine();  
            return in_.readLine();
            //System.out.println("\n\t"+resposta);
            
            // fim Debug
    }
    
    public void closeSocket(){
        if(socketToServer != null){
            try {
                socketToServer.close();
            } catch (IOException ex) {}
        }
    }
}
