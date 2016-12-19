/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

import common.FileSystem;

/**
 *
 * @author André Oliveira
 */
public class FileSystemClient extends FileSystem {
    private String remoteHomeDir;

    public FileSystemClient(String name) {
        super(name);
    }
    
    public String getRemoteWorkingDir(){
        return this.remoteHomeDir;
    }
     public void setRemoteWorkingDir(String path){
        this.remoteHomeDir = path;
    }
}
