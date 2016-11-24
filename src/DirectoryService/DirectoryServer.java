package DirectoryService;

public class DirectoryServer {
    
    private static ServerUdpListener udpReceiver;
        
    public static void main(String[] args) {
        (udpReceiver = new ServerUdpListener()).start();
    }
}