import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;


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

            out.flush();
            out.writeObject(MESSAGE);

            Object[] resp = (Object[]) in.readObject();
            int type = (int) resp[0];

            //Trata resposta do servidor no lado do cliente
            switch(type){
                case ServerProcessClient.ENTERING_NETWORK:{
                    if((int) resp[2]==0){
                        Filial filialResp = (Filial) resp[1];
                        if(Node.neighbors.size()==0){
                            Node.neighbors.add(filialResp);
                            Node.neighbors.add(filialResp);
                        }
                    }else{
                        Object[] filiaisResp = (Object[]) resp[1];
                        Node.neighbors.add((Filial)filiaisResp[0]);
                        Node.neighbors.add((Filial)filiaisResp[1]);
                    }
                    System.out.println("Vizinhos:"+Node.neighbors);
                    break;
                }
            }
            out.close();

        }catch(Exception e){
            System.out.println(client.getInetAddress().getHostAddress()+"::"+e.getMessage());
        }
    }

}
