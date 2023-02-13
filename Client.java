import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class Client extends Thread{
    
    private Socket client;

    private String HOST;
    private int PORT;
    private Object[] MESSAGE;
    private ArrayList<String> neighbors;

    public Client(String HOST, int PORT, Object[] MESSAGE, ArrayList<String> neighbors){
        this.HOST = HOST;
        this.PORT = PORT;
        this.MESSAGE = MESSAGE;
        this.neighbors = neighbors;
    }

    public void run(){
        try{
            client = new Socket(HOST, PORT);

            ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(client.getInputStream());

            switch((int)MESSAGE[0]){
                case ServerProcessClient.SEARCH:{
                    //Object[] search = (Object[]) MESSAGE[1];
                    //search[1] = client.getLocalAddress().getHostAddress();
                    ((Object[])MESSAGE[1])[1] = client.getLocalAddress().getHostAddress();
                    break;
                }
            }

            out.flush();
            out.writeObject(MESSAGE);

            Object[] resp = (Object[]) in.readObject();
            int type = (int) resp[0];

            switch(type){
                case ServerProcessClient.ENTERING_NETWORK:{
                    if(resp[1]==null){
                        if(neighbors.size()==0){
                            neighbors.add(HOST);
                            neighbors.add(HOST);
                        }
                    }else{
                        neighbors.add(HOST);
                        neighbors.add((String) resp[1]);
                    }
                    System.out.println("Vizinhos:"+neighbors);
                    break;
                }
                case ServerProcessClient.OUT_OK:{
                    neighbors.remove(HOST); 
                    System.out.println(HOST+" removido!");                   
                    break;
                }
            }
            out.close();

        }catch(Exception e){
            System.out.println(client.getInetAddress().getHostAddress()+"::"+e.getMessage());
        }
    }

}
