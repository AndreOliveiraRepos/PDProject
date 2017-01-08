/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package FileServer;

import common.FileSystem;
import common.ICommands;
import java.io.File;

public class ServerCommands implements ICommands {
    private FileSystem serverFileSystem;
    private String output;
    private ServerTCPHandler tcpHandler;
    private FileServer fileServer;
    
    public ServerCommands(FileSystem fs,ServerTCPHandler s, FileServer f){
        this.serverFileSystem = fs;
        this.tcpHandler = s;
        this.output = "";
        this.fileServer = f;
    }
    
    @Override
    public String Connect(String[] args) {
        output = "Welcome";
        return output;
    }

    @Override
    public String Login(String user, String pass) {
        if(fileServer.validateUser(user, pass)){
            
            output = this.serverFileSystem.getWorkingDir().replace("C:/temp/"+this.fileServer.getName(),"remote"+serverFileSystem.getName()+"/"+user);
            System.out.println("CAMINHO ENVIADO:" + this.serverFileSystem.getWorkingDir().replace("C:/temp/"+this.fileServer.getName(),"remote"+serverFileSystem.getName()+"/"));
            //tcpHandler.writeData("User created!");
            return output;
        }
        else{
            //tcpHandler.writeData("User not created!");
            return "Wrong credentials";
        }
    }

    @Override
    public String Logout(String user) {
        if(fileServer.loggoutUser(user)){
            //tcpHandler.writeData("User logged out!");
            return "User logout";
        }
        else{
            //tcpHandler.writeData("Cant logout!");
            return "Error";
        }
    }

    @Override
    public String Register(String user,String pass) {
        if(fileServer.registerNewUser(user, pass)){
            //tcpHandler.writeData("User created!");
            this.MakeDirectory("C:/temp/"+this.serverFileSystem.getName()+"/"+user);
            return "New user registered";
        }
        else{
            //tcpHandler.writeData("User not created!");
            return "Error creating user";
        }
    }

    @Override
    public String Download(String path) {
        
        output = tcpHandler.receiveFile(path);
        //tcpHandler.writeData("[Server] "+output);
        return output;
    }

    @Override
    public String Upload(String path) {
        output = tcpHandler.sendFile(path);
        //tcpHandler.writeData("[Server] "+output);
        return output;
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
        output = serverFileSystem.editDirectoryName(path, newName);
        return output;
    }

    @Override
    public String EditFileName(String path, String newName) {
        output = serverFileSystem.editFileName(path, newName);
        return output;
        
    }

    @Override
    public String Process(String line) {
        
        String[] cmd = line.split("\\s");
        
        if(cmd.length == 2){
            String rep;
            if(cmd[1].contains("remote"+serverFileSystem.getName())){
                rep = cmd[1].replace("remote"+serverFileSystem.getName(),this.serverFileSystem.getWorkingDir());
                cmd[1] = rep;
               
            }
        }else if(cmd.length < 4 && cmd.length > 2){
            String rep;
            if(cmd[1].contains("remote"+serverFileSystem.getName())){
                rep = cmd[1].replace("remote"+serverFileSystem.getName(),this.serverFileSystem.getWorkingDir());
                cmd[1] = rep;
                
                if(cmd[2].contains("remote"+serverFileSystem.getName())){
                    
                    rep=cmd[2].replace("remote"+serverFileSystem.getName(),this.serverFileSystem.getWorkingDir());
                    cmd[2] = rep;
                    
                }
            }
            
        }else if(cmd.length == 4){
            
            String rep;
            if(cmd[1].contains("remote"+serverFileSystem.getName())){
                rep = cmd[1].replace("remote"+serverFileSystem.getName(),this.serverFileSystem.getWorkingDir());
                cmd[1] = rep;
                
            }
            if(cmd[2].contains("remote"+serverFileSystem.getName())){
                    
                    rep=cmd[2].replace("remote"+serverFileSystem.getName(),this.serverFileSystem.getWorkingDir());
                    cmd[2] = rep;
                    System.out.println("CMD2"+cmd[2]);
            }
        }
        switch(cmd[0].toUpperCase()){
            case CONNECT:
                return this.Connect(cmd);
            case COPY:
                if(cmd.length == 3){
                    
                    return this.Copy(cmd[1],cmd[2]);
                }else if(cmd.length == 4){
                    if(cmd[3].equalsIgnoreCase("DOWNLOAD")){
                        //File f = new File(cmd[2]);
                        System.out.println("CAMINHO: "+cmd[1]);
                        return this.Upload(cmd[1]);
                    }
                    else if(cmd[3].equalsIgnoreCase("UPLOAD")){
                        File f = new File(cmd[1]);
                        System.out.println("CAMINHO: "+cmd[2]+f.getName());
                        return this.Download(cmd[2]+"/"+f.getName());
                    }
                }
                
            case REGISTER:
                if(cmd.length > 2 && !cmd[1].isEmpty() && !cmd[2].isEmpty()){
                    return this.Register(cmd[1], cmd[2]);
                }
                return "Wrong command";
            case LOGIN:
                return this.Login(cmd[1],cmd[2]);
            case LOGOUT:
                return this.Logout(cmd[1]);
            case MOVE:
                if(cmd.length == 3){
                    
                    return this.Copy(cmd[1],cmd[2]);
                }else if(cmd.length == 4){
                    if(cmd[3].equalsIgnoreCase("DOWNLOAD")){
                        File f = new File(cmd[1]);
                        output=this.Upload(cmd[1]);
                        //f.delete();
                        return output;
                    }
                    else if(cmd[3].equalsIgnoreCase("UPLOAD")){
                        File f = new File(cmd[1]);
                        System.out.println("CAMINHO: "+cmd[2]+f.getName());
                        output = this.Download(cmd[2]+"/"+f.getName());
                        return output;
                    }
                }
            case CHANGEDIR:
                
                return this.ChangeDirectory(cmd[1] + "/" + cmd[2]);
            case BACKDIR:
                return this.BackDirectory(cmd[1]);
            case GETCONTENTDIR:
                System.out.println("debug " + cmd[1]);
                return this.ListFiles(cmd[1]);
            case GETFILECONTENT:
                return this.CatFile(cmd[1]);
            case MKDIR:
                return this.MakeDirectory(cmd[1]);
            case RMFILE:
                return this.Delete(cmd[1]);
            case RENAMEFILE:
                
                return this.EditFileName(cmd[1], cmd[2]);
            case RENAMEDIR:
                return this.EditDirName(cmd[1], cmd[2]);
            
            default:
                return "Unknown command type help";
        }
    }

    @Override
    public String ListFiles(String path) {
        System.out.println("CAMINHO LS Remoto: " + path);
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

    @Override
    public String ChangeDirectory(String path) {
        System.out.println("CAMINHO:" + path);
        File f = new File(path);
        if (f.exists() && f.isDirectory())
            return "ok";
        else
            return "no";
    }

    @Override
    public String BackDirectory(String currentPath) {
        String resposta = "";
        String[] npath = currentPath.split("/");
        if(npath.length-1 < 2){
                                
            currentPath = currentPath.replace("C:/","remote"+serverFileSystem.getName()+"/");
            return currentPath;
        }else{


            resposta = "";
            for(int i = 0;i < npath.length-1;i++){

                resposta+= npath[i] + "/";
            }
            resposta = resposta.replace("C:/","remote"+serverFileSystem.getName()+"/");
            return resposta;
            //resposta = convertedPath;
        }
        
    }

    @Override
    public String RenameDirectory(String path, String newName) {
        output = this.serverFileSystem.editDirectoryName(path, newName);
        return output;
    }

    @Override
    public String RenameFile(String path, String newName) {
        output = this.serverFileSystem.editFileName(path, newName);
        return output;
    }

    
    
}
