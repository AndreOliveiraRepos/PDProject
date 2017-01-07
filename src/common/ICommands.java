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
    //public static final String NAME = "NAME";
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
    public static final String RENAMEDIR = "REN";
    public static final String RENAMEFILE = "REF";
    
    String Connect(String[] args);
    String Login(String user, String pass);
    String Logout(String user);
    String Register(String user, String pass);
    String Download(String path);
    String Upload(String path);
    String Move(String origin,String destiny);
    String Copy(String origin,String destiny);
    String Delete(String path);
    String EditDirName(String path,String newName);
    String EditFileName(String path,String newName);
    String Process(String line);
    String ListFiles(String path);
    String MakeDirectory(String path);
    String CatFile(String path);
    String ChangeDirectory(String folder);
    String BackDirectory(String currentPath);
    String RenameDirectory(String path,String newName);
    String RenameFile(String path, String newName);
    
    
}
