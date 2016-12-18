package DirectoryService;

import FileServer.ServerHeartbeat;
import common.Heartbeat;
import common.Msg;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class DirectoryServer {
    
    private static ServerManager serverManager;
    private static ClientManager clientManager;
        
    public static void main(String[] args) 
    {   
        UdpRequestHandler udpRequestHandler = new UdpRequestHandler();
        udpRequestHandler.start();
        
        serverManager = new ServerManager();
        serverManager.setDaemon(true);
        serverManager.start();
        
        clientManager = new ClientManager();
        clientManager.setDaemon(true);
        clientManager.start();
    }
    
    private static class UdpRequestHandler extends Thread {
        
        private static final String LIST = "LIST";
        private static final String USERS = "USERS";
        private static final String MSG = "MSG";
        
        ServerUdpListener udpListener;
        ChatService chatService;
                
        UdpRequestHandler()
        {
            udpListener = new ServerUdpListener();
            chatService = new ChatService();
        }
        
        @Override
        public void run(){
            try {
                while(udpListener.isListening()){
                    Object obj;
                    obj = udpListener.handleRequests();
                    processRequest(obj);
                }
            } catch(IOException e){
                System.out.println("Ocorreu um erro no acesso ao socket:\n\t"+e);
            }catch(ClassNotFoundException e){
                System.out.println("O objecto recebido não é do tipo esperado:\n\t"+e);
            }finally{
                if(udpListener.getSocket() != null){
                    udpListener.getSocket().close();
                }
            }
        }
        
        private void processRequest(Object obj) throws IOException
        {
            if (obj instanceof ServerHeartbeat)
                serverManager.processHeartbeat((ServerHeartbeat)obj);  
            else if (obj instanceof Heartbeat)
                clientManager.processHeartbeat((Heartbeat)obj);
            else if (obj instanceof Msg)
                processCommand((Msg)obj);
            else if (obj instanceof String)
                System.out.println((String)obj);
            else
                System.out.println("Erro: Objecto recebido do tipo inesperado!");
        }
        
        private void processCommand(Msg msg) throws IOException
        {
            if(msg.getMsg().equalsIgnoreCase(LIST))
                udpListener.sendResponse(serverManager.getServerMap());
            else if (msg.getMsg().equalsIgnoreCase(USERS)){
                Iterator it = clientManager.getOnlineClients().keySet().iterator();
                StringBuilder clientsAsString = new StringBuilder();
                while (it.hasNext()){
                    String c = (String)it.next();
                    if (serverManager.isAuthenticatedClient(c)){
                        clientsAsString.append(c + "\n");
                    }
                }
                udpListener.sendResponse(clientsAsString.toString());
                
            }
            else {
                String[] args = msg.getMsg().split("\\s");

                if (args[0].equalsIgnoreCase(MSG))
                {
                    Iterator it = clientManager.getOnlineClients().values().iterator();
                    while (it.hasNext()){
                        ClientEntry client = (ClientEntry) it.next();
                        if (serverManager.isAuthenticatedClient(client.getName())){
                            chatService.sendMessage(
                                client.getName() + ": " + args[1],
                                client.getClientAddr(), client.getPort());
                        }
                    }
                    udpListener.sendResponse("\n");
                } else udpListener.sendResponse("Unknown Command");
            }
        }
    }
}