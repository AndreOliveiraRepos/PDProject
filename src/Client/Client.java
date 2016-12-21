package Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;

public class Client {
    private static final String EXIT = "EXIT";
   
    private static ClientCommands commands;
    
    //private static ClientICommands commands;
        
    public static void main(String[] args) {
        
        Client observer = new Client();
        
        if(args.length != 2){
            System.out.println("Sintaxe: java Client dirAdress dirUdpPort");
            return;
        }
        
        try 
        {
            InetAddress directoryServerAddr = InetAddress.getByName(args[0]);
            Integer directoryServerPort = Integer.parseInt(args[1]);
            
            commands = new ClientCommands(observer, directoryServerAddr, directoryServerPort);
                      
            String msg;
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            while(true){
                System.out.print("> ");
                msg = in.readLine();
                if(msg.equalsIgnoreCase(EXIT)) break;
                System.out.println(commands.getClientCommand().Process(msg));
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
