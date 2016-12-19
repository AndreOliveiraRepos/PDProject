package Client;

import common.FileSystem;
import common.Msg;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientCommands {
    
    public static final String LIST = "LIST";
    public static final String MSG = "MSG";
    public static final String USERS = "USERS";
    //commands
    public static final String COPY = "CP";
    public static final String REGISTER = "REGISTER";
    public static final String LOGIN = "LOGIN";
    public static final String LOGOUT = "LOGOUT";
    public static final String MOVE = "MV";
    public static final String CHANGEDIR = "CD";
    public static final String BACKDIR = "CD..";
    public static final String GETCONTENTDIR = "LS";
    public static final String GETFILECONTENT = "PICO";
    public static final String MKDIR = "MKDIR";
    public static final String RMFILE = "RM";
    
    private ClientTcpHandler tcpHandler;
    private ClientUdpHandler udpHandler;
    
    public ClientCommands(ClientUdpHandler udpHandler, ClientTcpHandler tcpHandler){
        this.tcpHandler = tcpHandler;
        this.udpHandler = udpHandler;
    }
    
    public String processCommands(Msg msg,FileSystemClient fs) throws IOException, ClassNotFoundException{
        String s = "";
        String[] cmd = msg.getMsg().split("\\s");
        if(fs.getWorkingDirPath().contains("remote"))
            return this.processRequest(msg,fs);
        else
        {
            switch(cmd[0].toUpperCase()){
                    case COPY:
                        return fs.copyFile(cmd[1],cmd[2]);
                    case REGISTER:
                        fs.Register();
                        break;
                    case LOGIN:
                        fs.Login();
                        break;
                    case LOGOUT:
                        fs.Logout();
                        break;
                    case MOVE:
                        return fs.moveFile(cmd[1],cmd[2]);
                        
                    case CHANGEDIR:
                        if(cmd[1].contains("remote") && cmd.length > 2){
                            
                            
                            fs.setRemoteWorkingDir(processRequest(new Msg(msg.getName(),"cd "+ cmd[2]),fs));
                            s+=fs.getRemoteWorkingDir();
                            
                        }else{
                            s+=fs.changeWorkingDirectory(cmd[1]);
                        }
                        return s; 
                    case BACKDIR:
                        if(cmd[1].contains("remote")){
                            fs.setRemoteWorkingDir(processRequest(new Msg(msg.getName(),"cd.."),fs));
                        }else{
                            s+= fs.changeWorkingDirectory(cmd[0]); 
                        }   
                    case GETCONTENTDIR:
                        
                        s+= processRequest(new Msg(msg.getName(),"ls "+ fs.getRemoteWorkingDir()),fs);
                        s+="Listing local\n";
                        s+= fs.getWorkingDirContent();
                        return s;        
                    case GETFILECONTENT:
                        return fs.getFileContent(cmd[1]);
                        
                    case MKDIR:
                        
                        return fs.makeDir(cmd[1]);
                        
                    case RMFILE:
                        
                        return fs.removeFile(cmd[1]);
                    default:
                        return "";
                        
                    

            }
        
        }
        return s;
    }
    public String processRequest(Msg msg,FileSystemClient fs) throws UnknownHostException, IOException, ClassNotFoundException{
        String[] args = msg.getMsg().split("\\s");
  
        if (args[0].equalsIgnoreCase(LIST)
            || args[0].equalsIgnoreCase(MSG)
            || args[0].equalsIgnoreCase(USERS))
        {
            try {
                return udpHandler.sendRequest(msg);
            } catch (IOException ex) {
                System.out.println("Erro ao enviar pedido UDP! " + ex);
            } catch (ClassNotFoundException ex) {
                System.out.println(ex);
            }
        }
        else if (args[0].equalsIgnoreCase("CONNECT")){
            if (args.length == 3){
                //connect 127.0.0.1 7001
                //on connect manda o pedido de login?
                tcpHandler.connectToServer(InetAddress.getByName(args[1]), Integer.parseInt(args[2]));
                /*System.out.println(
                    tcpHandler.sendRequest("HOME "+msg.getName())
                );*/
                
                fs.setRemoteWorkingDir(tcpHandler.sendRequest("HOME "+msg.getName()));
                System.out.println(fs.getRemoteWorkingDir());
            } else System.out.println("Erro de sintaxe: connect <ip> <porto>");
        }else{
            
            //processar outros comandos
            //System.out.println("AQUI "+msg.getMsg());
            
            return tcpHandler.sendRequest(msg.getMsg());
           /* System.out.println("AQUI "+msg.getMsg());
            System.out.println(
                    tcpHandler.sendRequest("HOME "+msg.getName())
                );*/
            /*System.out.println("AQUI"+msg.getMsg());
            return tcpHandler.sendRequest(msg.getMsg());*/
        }
        return "";
    }
    
}
