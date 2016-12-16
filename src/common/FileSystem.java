/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileSystem implements Serializable{
    private String remoteHomeDir;
    private String localHomeDir;
    private String workingDirectory;
    private String clientName;
    private String currentServer;
    
    
    public FileSystem(String name){
        this.clientName = name;
        //this.localHomeDir = "C:\\temp\\"+clientName;
        this.localHomeDir = "C:/temp/";
        this.workingDirectory = this.localHomeDir;
        
        
    }
    //monta vistas
    public void buildTrees(){
        //test
        
    }
    
    //gets
    //sets
    //fs comands
    
    public void Register(){}
    public void Login(){}
    public void Logout(){}
    
    //operators
    public void copyFile(String fileName,String destinyPath){
        FileChannel source;
        FileChannel destination;
        if(this.workingDirectory.contains("remote")){
            //request to server, get response;
        }
        else{
            File s = new File(this.workingDirectory + fileName);
            File d = new File(destinyPath + fileName);
            try {
               source = new FileInputStream(s).getChannel();
               destination = new FileOutputStream(d).getChannel();
               destination.transferFrom(source, 0, source.size());
               source.close();
               destination.close();
            } catch (FileNotFoundException ex) {
                Logger.getLogger(FileSystem.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(FileSystem.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    }
    public void moveFile(String fileName,String destinyPath){}
    public void changeWorkingDirectory(String path){
        this.workingDirectory = path;
    }
    public void getWorkingDirContent(){
        if(this.workingDirectory.contains("remote")){
            //request to server, get response;
        }
        else{
            File folder = new File(this.workingDirectory);
            File[] listOfFiles = folder.listFiles();

            for (int i = 0; i < listOfFiles.length; i++) {
              if (listOfFiles[i].isFile()) {
                System.out.println("[F]:" + listOfFiles[i].getName());
              } else if (listOfFiles[i].isDirectory()) {
                System.out.println("[D]:" + listOfFiles[i].getName());
              }
            }
        }
        
        
        
    }
    public String getWorkingDirPath(){
        return this.workingDirectory;
    }
    //lel
    public void getFileContent(){}
    
    public void removeFile(String fileName){
        if(this.workingDirectory.contains("remote")){
            //request to server, get response;
        }else{
            File f = new File(fileName);
            f.delete();
            System.out.println("Done!");
        }
    }
    public void makeDir(String path){
        if(this.workingDirectory.contains("remote")){
            //request to server, get response;
        }else{
            File newDir = new File(path);
            if(!newDir.exists()){
                newDir.mkdir();
                System.out.println("Done!");
            }
        }
    }
    
    
    //console debug only
    public static void main(String[] args) throws IOException {
        FileSystem fs = new FileSystem("red");
        String msg;
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        while(true){
            System.out.print("@" + fs.workingDirectory +"> ");
            msg = in.readLine();
            switch(msg){
                case "ls":
                    fs.getWorkingDirContent();
                    break;
            }
        }
        
    }
    
    
    
    
    
}
