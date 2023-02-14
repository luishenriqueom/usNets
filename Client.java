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
    private final int PORT = 12345;
    private Object[] MESSAGE;

    public Client(String HOST, Object[] MESSAGE){
        this.HOST = HOST;
        this.MESSAGE = MESSAGE;
    }

    public void run(){
        try{
            client = new Socket(HOST, PORT);

            ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(client.getInputStream());

            // switch((int)MESSAGE[0]){
            //     case ServerProcessClient.SEARCH:{
            //         //Object[] search = (Object[]) MESSAGE[1];
            //         //search[1] = client.getLocalAddress().getHostAddress();
            //         ((Object[])MESSAGE[1])[1] = client.getLocalAddress().getHostAddress();
            //         break;
            //     }
            // }

            out.flush();
            out.writeObject(MESSAGE);

            Object[] resp = (Object[]) in.readObject();
            int type = (int) resp[0];

            switch(type){
                case ServerProcessClient.ENTERING_NETWORK:{
                    if(resp[2]==null){
                        if(Filial.neighbors.size()==0){
                            Filial.neighbors.add(HOST);
                            Filial.neighbors.add(HOST);
                        }
                    }else{
                        Filial.neighbors.add(HOST);
                        Filial.neighbors.add((String) resp[2]);
                    }
                    System.out.println("Vizinhos:"+Filial.neighbors);
                    break;
                }
                case ServerProcessClient.OUT_OK:{
                    Filial.neighbors.remove(HOST); 
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
