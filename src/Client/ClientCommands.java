package Client;

import common.FileSystem;
import common.Msg;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientCommands {
    
    public static final String LIST = "LIST";
    public static final String MSG = "MSG";
    //commands
    public static final String COPY = "CP";
    public static final String REGISTER = "REGISTER";
    public static final String LOGIN = "LOGIN";
    public static final String LOGOUT = "LOGOUT";
    public static final String MOVE = "MV";
    public static final String CHANGEDIR = "CD";
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
    
    public String processCommands(Msg msg,FileSystem fs){
        String s = null;
        String[] cmd = msg.getMsg().split("\\s");
        
        switch(cmd[0].toUpperCase()){
                    case COPY:
                        fs.copyFile(cmd[1],cmd[2]);
                        break;
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
                        fs.moveFile(cmd[1],cmd[2]);
                        break;
                    case CHANGEDIR:
                        fs.changeWorkingDirectory(cmd[1]);
                        break;
                    case GETCONTENTDIR:
                        return fs.getWorkingDirContent();
                        
                    case GETFILECONTENT:
                        fs.getFileContent();
                        break;
                    case MKDIR:
                        
                        fs.makeDir(cmd[1]);
                        break;
                    case RMFILE:
                        fs.removeFile(cmd[1]);
                        break;
                    default:

                        break;

        }
        return s;
    }
    public String processRequest(Msg msg){
        String[] args = msg.getMsg().split("\\s");
  
        if (args[0].equalsIgnoreCase(LIST)
                || args[0].equalsIgnoreCase(MSG))
        {
            try {
                return udpHandler.sendRequest(msg);
            } catch (IOException ex) {
                Logger.getLogger(ClientCommands.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(ClientCommands.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return "";
    }
    
}
