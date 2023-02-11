import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class ServerProcessClient extends Thread{
    
    private Socket client;
    private ArrayList<Funcionario> funcionarios;
    private ArrayList<String> neighbors;

    public static final int ENTERING_NETWORK = 0;
    public static final int OUT_NETWORK = 1;
    public static final int SEARCH = 2;
    public static final int FOUND_SEARCH = 3;
    public static final int OUT_OK = 4;

    public ServerProcessClient(Socket client, ArrayList<Funcionario> funcionarios, ArrayList<String> neighbors){
        this.client = client;
        this.funcionarios = funcionarios;
        this.neighbors = neighbors;
    }

    public void run(){
        try{
            System.out.println("Cliente conectado. IP:"+client.getInetAddress().getHostAddress());
            ObjectInputStream in = new ObjectInputStream(client.getInputStream());
            ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());
                        
            Object[] clientReq = (Object[]) in.readObject();
            
            int type = (int) clientReq[0];

            switch(type){
                case ENTERING_NETWORK:{
                    if(clientReq[1]==null){
                        if(neighbors.size()==0){
                            //System.out.println("entrei");
                            neighbors.add(client.getInetAddress().getHostAddress());
                            neighbors.add(client.getInetAddress().getHostAddress());
                            //System.out.println(neighbors);
                            Object[] res = {ENTERING_NETWORK, null};
                            out.flush(); //limpar buffer
                            out.writeObject(res);
                        }else{
                            Object[] req = {ENTERING_NETWORK, client.getInetAddress().getHostAddress()};

                            new Client(neighbors.get(1), 12345, req, neighbors).start();
                            
                            Object[] res = {ENTERING_NETWORK, neighbors.get(1)};

                            neighbors.set(1, client.getInetAddress().getHostAddress());
                            
                            out.flush();
                            out.writeObject(res);                            
                        }
                    }else{
                        int index = neighbors.indexOf(client.getInetAddress().getHostAddress());
                        neighbors.set(index, (String) clientReq[1]);
                    }
                    System.out.println("Vizinhos:"+neighbors);
                    break;
                }
                case OUT_NETWORK:{
                    int index = neighbors.indexOf(client.getInetAddress().getHostAddress());
                    neighbors.set(index, (String) clientReq[1]);
                    Object[] res = {OUT_OK, null};
                    out.flush();
                    out.writeObject(res);
                    break;
                }
                case SEARCH:{
                    break;
                }
                case FOUND_SEARCH:{
                    break;
                }
            }
            
            out.close();
        }catch (Exception e){
            System.out.println("Error:: Thread ProcessClient - "+e.getMessage());
        }
    }

}
