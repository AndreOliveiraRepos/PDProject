package common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.nio.channels.FileChannel;

public class FileSystem implements Serializable{
    private String localHomeDir;
    private String workingDirectory;
    private String ownerName;

    public FileSystem(String name){
        this.ownerName = name;
        this.localHomeDir = "/temp/";
        File localdir = new File(this.localHomeDir);
        //this.localHomeDir = "C:\\temp\\"+clientName;
        if(!localdir.exists()){
            localdir.mkdir();
            this.workingDirectory = this.localHomeDir;   
            
        }
        else{
            
           this.workingDirectory = this.localHomeDir;   
        }
        
        
    }
    //operators
    public String copyFile(String origin,String destiny){
        FileChannel ofc, dfc;
        File sourceFile = new File(origin);
        destiny+="/"+sourceFile.getName();
        //destiny+="\\"+sourceFile.getName();
        File destinyFile = new File(destiny);
        if(destinyFile.exists())
            return "File already exists";
        else{
            try {
                ofc = new FileInputStream(sourceFile).getChannel();
                dfc = new FileOutputStream(destinyFile).getChannel();
                dfc.transferFrom(ofc, 0, ofc.size());
                ofc.close();
                dfc.close();
                return "File copied";
            } catch (FileNotFoundException ex) {
                return "File not found!";
            } catch (IOException ex) {
                return "I/O error!";
            }
        }
    }
    
    public String moveFile(String origin,String destiny){
        FileChannel ofc, dfc;
        File sourceFile = new File(origin);
        destiny+="/"+sourceFile.getName();
        //destiny+="\\"+sourceFile.getName();
        File destinyFile = new File(destiny);
        if(destinyFile.exists())
            return "File already exists";
        else{
            try {
                ofc = new FileInputStream(sourceFile).getChannel();
                dfc = new FileOutputStream(destinyFile).getChannel();
                dfc.transferFrom(ofc, 0, ofc.size());
                ofc.close();
                dfc.close();
                sourceFile.delete();
                System.out.println("Apaguei.");
                return "File moved";
            } catch (FileNotFoundException ex) {
                return "File not found!";
            } catch (IOException ex) {
                return "I/O error!";
            }
        }
    }
    public void setWorkingDir(String wd){
        this.workingDirectory = wd;
    }
    public String getWorkingDir(){
        return this.workingDirectory;
    }
    public String deleteFile(String path){
        File fileToDelete = new File(path);
        
        if(fileToDelete.exists() && fileToDelete.isFile()){
            fileToDelete.delete();
            return "File deleted";
        }else if(fileToDelete.exists() && fileToDelete.isDirectory()){
            if(fileToDelete.listFiles().length > 0){
                return "Directory is not empty";
            }else{
                fileToDelete.delete();
                return "Directory deleted";
            }
            
        }else{
            return "File or Directory not found!";
        }
    }
    public String editFileName(String path, String newName){
        File fileToEdit = new File(path);
        File fileRenamed = new File(newName);
        if(fileToEdit.exists() && fileToEdit.isFile()){
            if(fileRenamed.exists() && fileRenamed.isFile()){
                return "Already exists a file with that name";
            }else{
                if(fileToEdit.renameTo(fileRenamed)){
                    return "File renamed to " + fileRenamed.getName();
                }
                else{
                    return "Could not rename file";
                }
                
               
            }
        }else{
            return "No such file";
        }
        
        
    }
    public String editDirectoryName(String path, String newName){
        File dirToEdit = new File(path);
        File dirRenamed = new File(newName);
        if(dirToEdit.exists() && dirToEdit.isDirectory()){
            if(dirRenamed.exists() && dirRenamed.isDirectory()){
                return "Already exists a folder with that name";
            }else{
                
                if(dirToEdit.renameTo(dirRenamed)){
                    return "Folder renamed to " + dirRenamed.getName();
                }
                else{
                    return "Could not rename folder";
                }
            }
        }else{
            return "No such folder";
        }
    }
    public String listDirectoryContent(String path){
        
        String output = "";
        System.out.println("CAMINHO LS antes folder: "+path + " fim");
        File folder = new File(path);
        System.out.println("CAMINHO LS FS: "+folder.getAbsolutePath() + " fim");
        if(folder.exists() && folder.isDirectory()){
            if(folder.listFiles().length > 0){
                output+= "Listing current directory\n";
                File[] listOfFiles = folder.listFiles();
                for (int i = 0; i < listOfFiles.length; i++) {
                    if (listOfFiles[i].isFile()) {
                      
                      output+="[F]:" + listOfFiles[i].getName() + "\n";
                    } else if (listOfFiles[i].isDirectory()) {
                      output+="[D]:" + listOfFiles[i].getName() + "\n";
                    }
                }
            }
            else{
                output = "Directory is empty";
            }
        }
        else{
            output = "Directory not found!";
            
        }
        
        
        return output;
    
    }
    public String getName(){return this.ownerName;}
    public String makeDirectory(String path){

        File newDir = new File(path);

        if(!newDir.exists()){
            newDir.mkdir();
            return path + " created!";
            
        }
        else{
            return "Directory already exists!";
           
        }
        
    }      
    public String fileCat(String path){
        File fileToRead = new File(path);
        if(fileToRead.exists() && fileToRead.isFile()){
            
            try {
                BufferedReader br = new BufferedReader(new FileReader(fileToRead));
                StringBuilder sb = new StringBuilder();
                String line = br.readLine();

                while (line != null) {
                    sb.append(line);
                    sb.append(System.lineSeparator());
                    line = br.readLine();
                }
                br.close();
                return sb.toString();
            
            } catch (FileNotFoundException ex) {
                return "File not found";
            } catch (IOException ex) {
                return  "Cannot read file!";
            }

            
        }
        else
            return "File not found";
    }
    public String changeDir(String folder){
        File folderToGo = new File(this.workingDirectory+"/"+folder);
        //File folderToGo = new File(this.workingDirectory+"\\"+folder);
        if (folderToGo.exists() && folderToGo.isDirectory()) {
           
                
            this.workingDirectory = folderToGo.getPath();
            this.workingDirectory = this.workingDirectory.replace('\\','/');
            return "Now working on " + this.workingDirectory;
            
        }else{
            return "No such directory";
        }
        
        
    }
    public String backDir(String currentDir){
        String resposta;
        resposta = "";
        String[] npath = currentDir.split("/");
        //String[] npath = currentDir.split("\\");
        System.out.println("N CAMINHO" + npath.length+"\n"+npath);
        if(npath.length < 2){
                                
            resposta = "No such dir";
            return resposta;
        }else{


            resposta = "";
            for(int i = 0;i < npath.length-1;i++){

                resposta+= npath[i] + "/";
                //resposta+= npath[i] + "\\";
            }
            this.workingDirectory = resposta;
            return resposta;
            //resposta = convertedPath;
        }
    
       
    }
}
    
    
