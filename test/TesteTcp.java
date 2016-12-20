
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author luism
 */
public class TesteTcp {
    public static void main(String[] args) throws ClassNotFoundException {
        try {
            
            Socket socketToServer = new Socket(InetAddress.getByName("127.0.0.1"), 7001);
            
            //PEDIDO1
            PrintWriter pout = new PrintWriter(socketToServer.getOutputStream(), true);
            pout.println("Pedido 1");
            pout.flush();
            
            ObjectInputStream in = new ObjectInputStream(socketToServer.getInputStream());
            System.out.println((String)in.readObject());
            
            
        } catch (IOException ex) {
            System.out.println("Erro no socket TCP" + ex);
        }
    }
}
