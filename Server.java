import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server extends Thread{
    
    private final int PORT = 12345;

    public void run(){
        try{
            ServerSocket server = new ServerSocket(PORT);

            while(true){ //criar novos clientes
                Socket client = server.accept();
                new ServerProcessClient(client).start();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

}
