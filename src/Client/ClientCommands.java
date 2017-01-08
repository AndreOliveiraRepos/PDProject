/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

import common.FileSystem;
import common.Heartbeat;
import common.ICommands;
import common.Msg;
import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 *
 * @author André Oliveira
 */
public class ClientCommands implements ICommands{
    
    private FileSystem clientFileSystem;
    //debug only
    private String remoteWorkingDir;
    private String output;
    private ClientTcpHandler tcpHandler;
    private String lastCommand;
    private Client client;
    private String clientName;
    
    
    public ClientCommands(FileSystem fs, ClientTcpHandler t, Client c){
        this.clientFileSystem = fs;
        //this.clientName = c;
        this.client = c;
        this.tcpHandler = t;
        
        this.output = "";
        //remoteWorkingDir = "remoteserver1/temp";
        this.lastCommand = "";
    }

    @Override
    public String Download(String path) {
        
        output = tcpHandler.receiveFile(path) + "\n";
        output += "[Server]"+(String)tcpHandler.readData();
        return output;
    }

    @Override
    public String Upload(String path) {
                 
        output = tcpHandler.sendFile(path) + "\n";
        output += "[Server]"+(String)tcpHandler.readData();
        return output;
    }

    @Override
    public String Move(String origin,String destiny) {
        output = clientFileSystem.moveFile(origin, destiny);
        return output;
    }

    @Override
    public String Copy(String origin,String destiny) {

        output = clientFileSystem.copyFile(origin, destiny);
        return output;
    }

    @Override
    public String Delete(String path) {
        
        
        output = clientFileSystem.deleteFile(path);
        return output;
    }

    @Override
    public String EditDirName(String path,String newName) {
        
        output = clientFileSystem.editDirectoryName(path, newName);
        
        
        return output;
    }

    @Override
    public String EditFileName(String path,String newName) {
        
        output = clientFileSystem.editFileName(path, newName);
        
        
        return output;
        
    }

    @Override
    public String Process(String line) {
        //validar argumentos do split
        String[] cmd = line.split("\\s");
        
        switch(cmd[0].toUpperCase()){
            case CONNECT:
                this.lastCommand = cmd[0].toUpperCase();
                return this.Connect(cmd);
            case COPY:
                
                if(cmd[1].contains("remote") && !cmd[2].contains("remote")){
                    if(this.client.getClientName().equalsIgnoreCase("guest")){
                            return "You need to login";
                    }
                    if(tcpHandler.isOnline()){
                         tcpHandler.writeData("cp " + cmd[1] + " " + cmd[2] + " DOWNLOAD");
                         File f = new File(cmd[1]);
                         System.out.println("NOME DO FILE " + f.getName());
                         output = this.Download(cmd[2]+"/"+ f.getName())+"\n";

                    }else{
                        output = "You are offline!";
                    }
                    }else if(!cmd[1].contains("remote") && cmd[2].contains("remote")){
                        if(this.client.getClientName().equalsIgnoreCase("guest")){
                            return "You need to login";
                        }
                        if(tcpHandler.isOnline()){
                             tcpHandler.writeData("cp "+ cmd[1]+" "+cmd[2]+" UPLOAD");
                             File f = new File(cmd[1]);
                             output = this.Upload(cmd[1]);
                             
                             
                        }else{
                            output = "You are offline!";
                        }
                    }else if(cmd[1].contains("remote") && cmd[2].contains("remote")){
                        if(this.client.getClientName().equalsIgnoreCase("guest")){
                            return "You need to login";
                        }
                        if(tcpHandler.isOnline()){
                             tcpHandler.writeData("cp "+ cmd[1]+" "+cmd[2]);
                             output = "[Server]"+(String)tcpHandler.readData();

                        }else{
                            output = "You are offline!";
                        }
                    }else{
                        output = clientFileSystem.moveFile(cmd[1], cmd[2]);
                    }
                    this.lastCommand = cmd[0].toUpperCase();
                    return output;
            case REGISTER:
                if(cmd.length == 3)
                    return this.Register(cmd[1],cmd[2]);
                else
                    return "Wrong command. Use register <user> <pass>";
            case LOGIN:
                if(cmd.length == 3)
                    return this.Login(cmd[1],cmd[2]);
                else
                    return "Wrong command. Use login <user> <pass>";
            case LOGOUT:
                    if(this.client.getClientName().equalsIgnoreCase("guest")){
                        return "You need to login";
                    }
                    return this.Logout(client.getClientName());
                
            case MOVE:
                if(cmd[1].contains("remote") && !cmd[2].contains("remote")){
            //download
                    if(this.client.getClientName().equalsIgnoreCase("guest")){
                        return "You need to login";
                    }
                    if(tcpHandler.isOnline()){
                         tcpHandler.writeData("cp " + cmd[1] + " " + cmd[2] + " DOWNLOAD");
                         File f = new File(cmd[1]);
                         System.out.println("NOME DO FILE " + f.getName());
                         output = this.Download(cmd[2]+"/"+ f.getName())+"\n";

                    }else{
                        output = "You are offline!";
                    }
                }else if(!cmd[1].contains("remote") && cmd[2].contains("remote")){
                    if(this.client.getClientName().equalsIgnoreCase("guest")){
                        return "You need to login";
                    }
                    if(tcpHandler.isOnline()){
                         tcpHandler.writeData("cp "+ cmd[1]+" "+cmd[2]+" UPLOAD");
                         File f = new File(cmd[1]);
                         output = this.Upload(cmd[1])+"\n";
                         f.delete();
                         
                    }else{
                        output = "You are offline!";
                    }
                }else if(cmd[1].contains("remote") && cmd[2].contains("remote")){
                    if(this.client.getClientName().equalsIgnoreCase("guest")){
                        return "You need to login";
                    }
                    if(tcpHandler.isOnline()){
                         tcpHandler.writeData("cp "+ cmd[1]+" "+cmd[2]);
                         output = "[Server]"+(String)tcpHandler.readData();

                    }else{
                        output = "You are offline!";
                    }
                }else{
                    output = clientFileSystem.moveFile(cmd[1], cmd[2]);
                }

                return output;
            case CHANGEDIR:
                if(cmd.length > 2 && cmd[1].contains("remote")){
                    if(this.client.getClientName().equalsIgnoreCase("guest")){
                        return "You need to login";
                    }
                    if(!tcpHandler.isOnline()){return "You are not connected!";}
                    System.out.println("CAMINHO REMOTO " + this.remoteWorkingDir);
                    tcpHandler.writeData("cd "+ this.remoteWorkingDir +" "+ cmd[2]);
                    String resp = (String)tcpHandler.readData();
                    if(resp.equalsIgnoreCase("OK")){
                        return this.remoteWorkingDir += "/" + cmd[2];
                        
                    }else{
                        return "No such remote dir";
                    }
                }else if(cmd.length > 1 && cmd.length < 3){
                   output = this.ChangeDirectory(cmd[1]);
                   return output;
                }else{
                    return "Wrong command: cd <folder> or cd <remote> <folder>";
                }
                
                
            case BACKDIR:
                if(cmd.length > 1 && cmd[1].contains("remote")){
                    if(this.client.getClientName().equalsIgnoreCase("guest")){
                        return "You need to login";
                    }
                    tcpHandler.writeData("cd.. "+ this.remoteWorkingDir);
                    String resp = (String)tcpHandler.readData();
                    this.remoteWorkingDir = resp;
                    return "Now working on " + this.remoteWorkingDir;
                    
                }else{
                   output = this.BackDirectory(clientFileSystem.getWorkingDir());
                   return output;
                }
            case GETCONTENTDIR:
                if(cmd.length > 1)
                    return this.ListFiles(cmd[1]);
                else
                    return this.ListFiles(clientFileSystem.getWorkingDir());
            case GETFILECONTENT:
                if(cmd[1].contains("remote")){
                    if(this.client.getClientName().equalsIgnoreCase("guest")){
                        return "You need to login";
                    }
                    tcpHandler.writeData("cat "+ cmd[1]);
                    output = "[Server]"+(String)tcpHandler.readData();
                    return output;
                }else {
                    return this.CatFile(clientFileSystem.getWorkingDir()+"/" + cmd[1]);
                }
            case MKDIR:
                if(cmd[1].contains("remote")){
                    if(this.client.getClientName().equalsIgnoreCase("guest")){
                        return "You need to login";
                    }
                    tcpHandler.writeData("mkdir "+ cmd[1]);
                    output = "[Server]"+(String)tcpHandler.readData();
                    return output;
                }else {
                    output = this.MakeDirectory(clientFileSystem.getWorkingDir()+"/"+cmd[1]);
                    return output;
                }
            case RMFILE:
                if(cmd[1].contains("remote")){
                    if(this.client.getClientName().equalsIgnoreCase("guest")){
                        return "You need to login";
                    }
                    tcpHandler.writeData("rm "+ cmd[1]);
                    output = "[Server]"+(String)tcpHandler.readData();
                    return output;
                }else {
                    output = this.Delete(cmd[1]);
                    return output;
                }
            case RENAMEDIR:
                if(cmd[1].equalsIgnoreCase("remote")&& cmd.length==4){
                    if(this.client.getClientName().equalsIgnoreCase("guest")){
                        return "You need to login";
                    }
                    tcpHandler.writeData("ren "+ this.remoteWorkingDir +"/"+cmd[2] + " " + this.remoteWorkingDir +"/"+cmd[3]);
                    output = "[Server]"+(String)tcpHandler.readData();
                    return output;
                }else if(cmd.length == 3){
                    output = this.EditDirName(clientFileSystem.getWorkingDir()+"/"+cmd[1],clientFileSystem.getWorkingDir()+"/"+cmd[2]);
                    return output;
                }
            case RENAMEFILE:
                
                if(cmd[1].equalsIgnoreCase("remote")&& cmd.length==4){
                    if(this.client.getClientName().equalsIgnoreCase("guest")){
                        return "You need to login";
                    }
                    tcpHandler.writeData("ref "+ this.remoteWorkingDir +"/"+cmd[2] + " " + this.remoteWorkingDir +"/"+cmd[3]);
                    output = "[Server]"+(String)tcpHandler.readData();
                    return output;
                }else if(cmd.length == 3){
                    output = this.EditFileName(clientFileSystem.getWorkingDir()+"/"+cmd[1],clientFileSystem.getWorkingDir()+"/"+cmd[2]);
                    return output;
                }
            /*case NAME:
                return this.changeName(cmd[1]);*/
            case LIST:
                return client.getUdpHandler().sendRequest(new Msg(client.getClientName(),line));
            case MSG:
                return client.getUdpHandler().sendRequest(new Msg(client.getClientName(),line));
            case USERS:
                return client.getUdpHandler().sendRequest(new Msg(client.getClientName(),line));
            case MSGTO:
                return client.getUdpHandler().sendRequest(new Msg(client.getClientName(),line));
            default:
                return "Unknown command, type help";
        }
    }

    @Override
    public String Connect(String[] args) {
        
        try {
            
            tcpHandler.connectToServer(InetAddress.getByName(args[1]), Integer.parseInt(args[2]));
            
            tcpHandler.writeData("Connect guest");
            output =(String)tcpHandler.readData();
            //this.remoteWorkingDir = output;
            //clientFileSystem.setWorkingDir(output);
            return "[Server]: "+ output ;
        } catch (UnknownHostException ex) {
            return "Unknown Host";
        }
    }

    @Override
    public String ListFiles(String path) {
        output="";
        if(tcpHandler.isOnline() && !this.client.getClientName().equalsIgnoreCase("guest")){
            //server.commands.upload
            //server.sendRequest
            String msg = "ls " + this.client.getRemoteDir();
            tcpHandler.writeData(msg);
            output += "[Server]:" + (String)tcpHandler.readData()+"\n";
            System.out.println("CAMINHO LS:" + path);
            output += clientFileSystem.listDirectoryContent(path);
        }else {
            output += clientFileSystem.listDirectoryContent(path);
        }
        return output;
    }

    @Override
    public String MakeDirectory(String path) {
        
        output = clientFileSystem.makeDirectory(path);
        return output;
    }

    @Override
    public String CatFile(String path) {
        System.out.println("PATH: " + path);
        output = clientFileSystem.fileCat(path);
        return output;
    }
    
    @Override
    public String ChangeDirectory(String folder){
        output = clientFileSystem.changeDir(folder);
        return output;
    }
    
    @Override
    public String BackDirectory(String currentPath){
        output = clientFileSystem.backDir(currentPath);
        return output;
    }

    @Override
    public String RenameDirectory(String path, String newName) {
        output = clientFileSystem.editDirectoryName(path, newName);
        return output;
    }

    @Override
    public String RenameFile(String path, String newName) {
        output = clientFileSystem.editFileName(path, newName);
        return output;
    }
    
    public String getClientName(){
        return clientName;
    }
    
    public void terminate(){
        this.client.terminate();
    }

    @Override
    public String Register(String user, String pass) {
        client.getTcpHandler().writeData("REGISTER " + user+" "+pass );
        output="[Server]"+(String)client.getTcpHandler().readData();
        if(output.contains("Error")){
            
            return output;
        }else{
            
            return output;
        }
    }

    @Override
    public String Login(String user, String pass) {
        client.getTcpHandler().writeData("LOGIN " + user+" "+pass );
        String path = (String)client.getTcpHandler().readData();
        output="[Server]"+path;
        if(output.contains("Wrong")){
            
            return output;
        }else{
            this.client.setRemoteDir(path);
            //System.out.println("OUT " + this.client.getRemoteDir());
            this.client.setClientName(user);
            this.changeName(user);
            output = "Logged in";
            return output;
        }
    }

    @Override
    public String Logout(String user) {
        client.getTcpHandler().writeData("LOGOUT " + user);
        output="[Server]"+(String)client.getTcpHandler().readData();
        if(output.contains("Error")){
            
            return output;
        }else{
            //tratar isto
            this.client.setClientName("guest");
            this.changeName("guest");
            output = "Logged out";
            return output;
        }
    }
    
    public String changeName(String name){
        //if (args.length == 2){
            client.setClientName(name);
            client.getHbSender().setHeartbeat(new Heartbeat(client.getUdpListener().getListeningPort(),client.getClientName()));
            return "Name set to " + client.getClientName();
        //} else client.reportError("Erro de sintaxe: nome <nome>");
    }
}
