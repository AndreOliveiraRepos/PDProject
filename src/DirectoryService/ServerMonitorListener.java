/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DirectoryService;

public interface ServerMonitorListener extends java.rmi.Remote{
    
    public void printServers() throws java.rmi.RemoteException;
}
