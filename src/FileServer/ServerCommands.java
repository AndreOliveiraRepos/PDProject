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
    private Socket socketToClient;
    
    public ServerCommands(FileSystem fs,Socket s){
        this.serverFileSystem = fs;
        this.socketToClient = s;
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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String Move(String origin, String destiny) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String Copy(String origin, String destiny) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String Delete(String path) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
    public String Cat(String path) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String Process(String line) {
        String[] cmd = line.split("\\s");
        if(cmd.length > 1){
            if(cmd[1].contains("remote"+serverFileSystem.getName())){
                cmd[1].replace("remote"+serverFileSystem.getName()+"/","C:/");
            }else if(cmd.length > 2 && cmd[2].contains("remote"+serverFileSystem.getName())){
                cmd[2].replace("remote"+serverFileSystem.getName()+"/","C:/");
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
                return "Unknown command type help";
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
        //System.out.println("aqui:"+path);
        return serverFileSystem.listDirectoryContent(path);
    }

    @Override
    public String MakeDirectory(String path) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String CatFile(String path) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

   
}
