/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package FileServer;

import common.FileSystem;
import common.ICommands;
import java.net.Socket;

/**
 *
 * @author André Oliveira
 */
public class ServerCommands implements ICommands {
    private FileSystem serverFileSystem;
    private String output;
    private ServerTCPHandler tcpHandler;
    
    public ServerCommands(FileSystem fs,ServerTCPHandler s){
        this.serverFileSystem = fs;
        this.tcpHandler = s;
        this.output = "";
    }
    
    @Override
    public String Connect(String[] args) {
        output = this.serverFileSystem.getWorkingDir().replace("C:/","remote"+serverFileSystem.getName()+"/");
        return output;
    }

    @Override
    public String Login() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String Logout() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String Register() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String Download(String path) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String Upload(String path) {
        return "";
    }

    @Override
    public String Move(String origin, String destiny) {
        output = serverFileSystem.moveFile(origin, destiny);
        return output;
    }

    @Override
    public String Copy(String origin, String destiny) {
        output = serverFileSystem.copyFile(origin, destiny);
        return output;
    }

    @Override
    public String Delete(String path) {
        output = serverFileSystem.deleteFile(path);
        return output;
    }

    @Override
    public String EditDirName(String path, String newName) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String EditFileName(String path, String newName) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String Process(String line) {
        
        String[] cmd = line.split("\\s");
        System.out.println("CONTA:"+cmd.length);
        if(cmd.length < 1){
            String rep;
            if(cmd[1].contains("remote"+serverFileSystem.getName())){
                rep = cmd[1].replace("remote"+serverFileSystem.getName()+"/","C:/");
                cmd[1] = rep;
                System.out.println("CMD1 primeiro"+cmd[1]);
            }
        }else if(cmd.length> 2){
            String rep;
            if(cmd[1].contains("remote"+serverFileSystem.getName())){
                rep = cmd[1].replace("remote"+serverFileSystem.getName()+"/","C:/");
                cmd[1] = rep;
                System.out.println("CMD1 segundo if"+cmd[1]);
                if(cmd[2].contains("remote"+serverFileSystem.getName())){
                    
                    rep=cmd[2].replace("remote"+serverFileSystem.getName()+"/","C:/");
                    cmd[2] = rep;
                    System.out.println("CMD2"+cmd[2]);
                }
            }
            
        }
        switch(cmd[0].toUpperCase()){
            case CONNECT:
                return this.Connect(cmd);
            case COPY:
                return this.Copy(cmd[1],cmd[2]);
            case REGISTER:
                return "Unknown command type help";
            case LOGIN:
                return "Unknown command type help";
            case LOGOUT:
                return "Unknown command type help";
            case MOVE:
                return this.Move(cmd[1],cmd[2]);
            case CHANGEDIR:
                return "Unknown command type help";
            case BACKDIR:
                return "Unknown command type help";
            case GETCONTENTDIR:
                return this.ListFiles(cmd[1]);
            case GETFILECONTENT:
                return this.CatFile(cmd[1]);
            case MKDIR:
                return this.MakeDirectory(cmd[1]);
            case RMFILE:
                return this.Delete(cmd[1]);
            default:
                return "Unknown command type help";
        }
    }

    @Override
    public String ListFiles(String path) {
        
        return serverFileSystem.listDirectoryContent(path);
    }

    @Override
    public String MakeDirectory(String path) {
        output = serverFileSystem.makeDirectory(path);
        return output;
    }

    @Override
    public String CatFile(String path) {
        output = serverFileSystem.fileCat(path);
        return output;
    }

   
}
