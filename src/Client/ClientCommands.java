package Client;

import common.FileSystem;
import common.Msg;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
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
    
    public String processCommands(Msg msg,FileSystem fs) throws IOException{
        String s = null;
        String[] cmd = msg.getMsg().split("\\s");
        if(fs.getWorkingDirPath().contains("remote"))
            return this.processRequest(msg);
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
                        return fs.changeWorkingDirectory(cmd[1]);
                    case BACKDIR:
                        return fs.changeWorkingDirectory(cmd[0]);    
                    case GETCONTENTDIR:
                        
                        return fs.getWorkingDirContent();
                        
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
        return "";
    }
    public String processRequest(Msg msg) throws UnknownHostException, IOException{
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
                System.out.println("jkj");
                tcpHandler.connectToServer(InetAddress.getByName(args[1]), Integer.parseInt(args[2]));
                System.out.println(
                    tcpHandler.sendRequest("Pedido teste")
                );
            } else System.out.println("Erro de sintaxe: connect <ip> <porto>");
        }
        return "";
    }
    
}
