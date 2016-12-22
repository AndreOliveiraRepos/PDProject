/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;

/**
 *
 * @author André Oliveira
 */
public class ClientUI {
    private static final String EXIT = "EXIT";
   
    private static Client client;
    
    //private static ClientICommands commands;
        
    public static void main(String[] args) {
        
        //ClientCommands observer = new ClientCommands();
        ClientUI observer = new ClientUI();
        
        if(args.length != 2){
            System.out.println("Sintaxe: java Client dirAdress dirUdpPort");
            return;
        }
        
        try 
        {
            InetAddress directoryServerAddr = InetAddress.getByName(args[0]);
            Integer directoryServerPort = Integer.parseInt(args[1]);
            
            client = new Client(observer, directoryServerAddr, directoryServerPort);
            client.goOnline();
            String msg;
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            while(true){
                System.out.print("> ");
                msg = in.readLine();
                if(msg.equalsIgnoreCase(EXIT)) break;
                System.out.println(client.getClientCommand().Process(msg));
                //System.out.println(commands.processCommand(msg));
                //System.out.println(commands.processRequest(msg));
            }
        } catch (IOException ex) {
            System.out.println("Erro ao ler comando! " + ex);
        }
    }
    
    public void printError(String e){
        System.out.println("Erro: " + e);
    }
    
    public void printContent(String s){
        System.out.println(s);
    }
}
