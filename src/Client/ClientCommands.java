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
    public static final String GETFILECONTENT = "CAT";
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
                        if(cmd[1].contains("remote") && cmd.length > 3 
                                && cmd[3].contains("remote")){
                            //System.out.println("COMANDO: " + "cp "+ fs.getRemoteWorkingDir()+"/"+cmd[2]+" " + cmd[3]+"/"+cmd[2]);
                            s = processRequest(new Msg(msg.getName(),"cp "+ fs.getRemoteWorkingDir()+"/"+cmd[2]+" " + cmd[3]+"/"+cmd[2]),fs);
                        }else if(cmd.length > 2 && cmd[2].contains("remote")){
                            //local para remoto
                            System.out.println("COMANDO: " + "cp "+ fs.getWorkingDirPath()+"/"+cmd[1]+" " + cmd[2]+"/"+cmd[1]);
                            //s=processRequest(new Msg(msg.getName(),"cp "+cmd[1]+ " " + cmd[2]+"/"+cmd[1]),fs);
                            
                            tcpHandler.writeData("cp "+cmd[1]+ " " + cmd[2]+"/"+cmd[1]);
                            System.out.println(tcpHandler.sendFile(fs.getWorkingDirPath()+"/"+cmd[1]));
                            //tcpHandler.sendFile(fs.getWorkingDirPath()+"/"+cmd[1]);
                            //System.out.println((String)tcpHandler.readData());
                            /*if(s.equalsIgnoreCase("READY"))
                                s+=tcpHandler.sendFile(fs.getWorkingDirPath()+"/"+cmd[1]);
                            else
                                System.out.println("NOPE!");*/
                            //s=tcpHandler.sendFile(fs.getWorkingDirPath()+"/"+cmd[1]);
                        }else if(cmd.length > 1 && cmd[1].contains("remote")){
                            //remoto para local
                            System.out.println("COMANDO" + "cp "+ cmd[1]+" local");
                        }else{    
                           
                            s= fs.copyFile(cmd[1],cmd[2]);
                        }
                        return s;
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
                           
                            
                            fs.setRemoteWorkingDir(processRequest(new Msg(msg.getName(),"cd " + fs.getRemoteWorkingDir()+"/" + cmd[2]),fs));
                            //System.out.println("AQUI resultado cd:" + fs.getRemoteWorkingDir());
                            s="AQUI: " + fs.getRemoteWorkingDir();
                            
                        }else{
                            s=fs.changeWorkingDirectory(cmd[1]);
                        }
                        return s; 
                    case BACKDIR:
                        if(cmd.length> 1 && cmd[1].contains("remote")){
                            fs.setRemoteWorkingDir(processRequest(new Msg(msg.getName(),"cd.. " + fs.getRemoteWorkingDir() ),fs));
                            s= fs.getRemoteWorkingDir();
                        }else{
                            s= fs.changeWorkingDirectory(cmd[0]); 
                        }
                        return s;
                    case GETCONTENTDIR:
                        System.out.println("REMOTE:"+fs.getRemoteWorkingDir());
                        s+= processRequest(new Msg(msg.getName(),"ls "+ fs.getRemoteWorkingDir()),fs);
                        s+="Listing local\n";
                        s+= fs.getDirContent(fs.getWorkingDirPath());
                        return s;        
                    case GETFILECONTENT:
                        return fs.getFileContent(cmd[1]);
                        
                    case MKDIR:
                        
                        if(cmd.length> 2 && cmd[1].contains("remote")){
                            s+= processRequest(new Msg(msg.getName(),"mkdir "+ fs.getRemoteWorkingDir() +" " + cmd[2]),fs);
                                  
                        }else{
                            
                            s+= fs.makeDir(cmd[1]);
                        }
                        return s;
                    case RMFILE:
                        if(cmd.length> 2 && cmd[1].contains("remote")){
                            s+= processRequest(new Msg(msg.getName(),"rm "+ fs.getRemoteWorkingDir() +" " + cmd[2]),fs);
                                  
                        }else{
                            s+= fs.removeFile(cmd[1]);
                        }
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
                
                tcpHandler.connectToServer(InetAddress.getByName(args[1]), Integer.parseInt(args[2]));
                
                //tcpHandler.writeData("HOME "+msg.getName());
                String m = "HOME "+msg.getName();
                
                tcpHandler.writeData("HOME "+msg.getName());
                //String req =(String) tcpHandler.readData();
                fs.setRemoteWorkingDir((String) tcpHandler.readData());
                return "Working on "+fs.getRemoteWorkingDir();
                //System.out.println(fs.getRemoteWorkingDir());
            } else System.out.println("Erro de sintaxe: connect <ip> <porto>");
            
       
        }else{
            tcpHandler.writeData(msg.getMsg());
            return (String) tcpHandler.readData();
            
            
           // return tcpHandler.sendRequest(msg.getMsg());
           
        }
        return "";
    }
    
    
}
