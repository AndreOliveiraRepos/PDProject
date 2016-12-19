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
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileSystem implements Serializable{
    private String localHomeDir;
    private String workingDirectory;
    private String ownerName;
    //private String currentServer;
    private String output;
    
    
    public FileSystem(String name){
        this.ownerName = name;
        //this.localHomeDir = "C:\\temp\\"+clientName;
        this.localHomeDir = "C:/temp";
        this.workingDirectory = this.localHomeDir;
        
        
    }
    
    public void Register(){}
    public void Login(){}
    public void Logout(){}
    
    //operators
    public String copyFile(String fileName,String destinyPath){
        output = "";
        FileChannel source;
        FileChannel destination;
        
        File s = new File(this.workingDirectory +"/" + fileName);
        File d = new File(destinyPath +"/" +fileName);
        try {
           source = new FileInputStream(s).getChannel();
           destination = new FileOutputStream(d).getChannel();
           destination.transferFrom(source, 0, source.size());
           source.close();
           destination.close();
           output+= "File copied to " +destinyPath;
        } catch (FileNotFoundException ex) {
            output += "File not found!";
            return output;
        } catch (IOException ex) {
            output += "Data Error!";
            return output;
        }
        return output;
        
    }
    
    public String moveFile(String fileName,String destinyPath){
        output = "";
        FileChannel source;
        FileChannel destination;
        
        File s = new File(this.workingDirectory +"/" + fileName);
        File d = new File(destinyPath +"/" +fileName);
        try {
            source = new FileInputStream(s).getChannel();
            destination = new FileOutputStream(d).getChannel();
            destination.transferFrom(source, 0, source.size());
            source.close();
            destination.close();
            s.delete();
            output = fileName +" moved to " + destinyPath;

        } catch (FileNotFoundException ex) {
            output += "File not found!";
            return output;
        } catch (IOException ex) {
            output += "Data Error!";
            return output;
        }
        return output;
    }
    
    public String changeWorkingDirectory(String path){
        output ="";
        
        if(path.equalsIgnoreCase("CD..")){
            //this.workingDirectory.
            String[] npath = this.workingDirectory.split("/");
            if(npath.length <= 1){
                output+= "Working Directory is now " + this.workingDirectory + "/";
                return output;
            }
            else{
                
                
                this.workingDirectory = "";
                for(int i = 0;i < npath.length-1;i++){
                    
                    this.workingDirectory+= npath[i] +"/";
                }
                output+= "Working Directory is now " + this.workingDirectory;
                return output;
            }
        }
        else{
            //System.out.println("AQUI:" + path);
            this.workingDirectory += "/" + path;
            output += "Working Directory is now " + path + "/";
            return output;
        }
    }
    public String getDirContent(String fullPath) {
        output="";
       
        File folder = new File(fullPath);
        File[] listOfFiles = folder.listFiles();
        
        if(listOfFiles != null){
            output+=fullPath + "\n";
            for (int i = 0; i < listOfFiles.length; i++) {
                if (listOfFiles[i].isFile()) {
                  output+="[F]:" + listOfFiles[i].getName() + "\n";
                } else if (listOfFiles[i].isDirectory()) {
                  output+="[D]:" + listOfFiles[i].getName() + "\n";
                }
            }
            //output += "[]";
        }else{
            
            output+=fullPath + "\n"+"empty:";
        }
        
        return output;
        
        
    }
    public String getWorkingDirPath(){
        return this.workingDirectory;
    }
    //lel
    public String getFileContent(String fileName){
        output = "";
        
        try {
            BufferedReader br = new BufferedReader(new FileReader(this.workingDirectory+"/"+fileName));
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
            output = sb.toString();
            br.close();
        } catch (FileNotFoundException ex) {
            output+="File not found";
        } catch (IOException ex) {
            output+= "Error reading file!";
        }
        /*File f = new File(this.workingDirectory + "/" + fileName);
        try{
            output+= Files.readAllLines(f.toPath());
        } catch (IOException ex) {
            output+= "Error reading file!";
        }*/
        return output;
    }
    
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
            output += this.workingDirectory + "/" + path + " created!";
            
        }
        else{
            output +="Directory already exists!";
           
        }
        return output;
    }
    
   
    
    
}
