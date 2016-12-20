/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package common;

/**
 *
 * @author André Oliveira
 */

public interface ICommands {
    public static final String NAME = "NAME";
    public static final String LIST = "LIST";
    public static final String MSG = "MSG";
    public static final String USERS = "USERS";
    public static final String MSGTO = "MSGTO";
    //commands
    public static final String CONNECT = "CONNECT";
    public static final String COPY = "CP";
    public static final String REGISTER = "REGISTER";
    public static final String LOGIN = "LOGIN";
    public static final String LOGOUT = "LOGOUT";
    public static final String MOVE = "MV";
    public static final String CHANGEDIR = "CD";
    public static final String BACKDIR = "CD..";
    public static final String GETCONTENTDIR = "LS";
    public static final String GETFILECONTENT = "CAT";
    public static final String MKDIR = "MKDIR";
    public static final String RMFILE = "RM";
    
    
    ICommands Login();
    ICommands Logout();
    ICommands Register();
    ICommands Download();
    ICommands Upload();
    ICommands Move();
    ICommands Copy();
    ICommands Delete();
    ICommands EditDirName();
    ICommands EditFileName();
    ICommands Cat();
    
}
