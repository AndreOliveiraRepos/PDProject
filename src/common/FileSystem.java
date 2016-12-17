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
    private String output;
    
    
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
            File s = new File(this.workingDirectory +"/" + fileName);
            File d = new File(destinyPath +"/" +fileName);
            try {
               source = new FileInputStream(s).getChannel();
               destination = new FileOutputStream(d).getChannel();
               destination.transferFrom(source, 0, source.size());
               source.close();
               destination.close();
                System.out.println("Done!");
            } catch (FileNotFoundException ex) {
                Logger.getLogger(FileSystem.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(FileSystem.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    }
    
    public String moveFile(String fileName,String destinyPath){
        output = "";
        FileChannel source;
        FileChannel destination;
        
        File s = new File(this.workingDirectory +"/" + fileName);
        File d = new File(destinyPath +"/" +fileName);
        
        if(s.exists()){
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
            s.delete();
            output = fileName +" moved to " + destinyPath;
        }else{
            output+= this.workingDirectory +"/" + fileName + " not found!";
        }
        return output;
    }
    
    public String changeWorkingDirectory(String path){
        output +="";
        this.workingDirectory += path;
        output += "Working Directory is now " + path;
        return output;
    }
    public String getWorkingDirContent() {
        output="";
       
        File folder = new File(this.workingDirectory);
        File[] listOfFiles = folder.listFiles();
        if(listOfFiles != null){
            output+="Listing from:" + this.workingDirectory + "\n";
            for (int i = 0; i < listOfFiles.length; i++) {
                if (listOfFiles[i].isFile()) {
                  output+="[F]:" + listOfFiles[i].getName() + "\n";
                } else if (listOfFiles[i].isDirectory()) {
                  output+="[D]:" + listOfFiles[i].getName() + "\n";
                }
            }
        }else{
            output+="empty:";
        }
        
        return output;
        
        
    }
    public String getWorkingDirPath(){
        return this.workingDirectory;
    }
    //lel
    public void getFileContent(){}
    
    public String removeFile(String fileName){
        output = "";
        
        File f = new File(this.workingDirectory + "/" +fileName);
        if(f.exists()){
            f.delete();
            output += "Deleted " + this.workingDirectory + "/" +fileName + " sucessfuly!";
        }else{
            output += "File not found!";
        }
        return output;
        
    }
    public String makeDir(String path){
        
        output ="";
        File newDir = new File(this.workingDirectory + "/" + path);

        if(!newDir.exists()){
            newDir.mkdir();
            output += this.workingDirectory + "/" + path + "created!";
            
        }
        else{
            output +="Directory already exists!";
           
        }
        return output;
    }
    
    
}
