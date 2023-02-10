import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class ServerProcessClient extends Thread{
    
    private Socket client;
    private ArrayList<Funcionario> funcionarios;
    private ArrayList<String> neighbors;

    private static final int ENTERING_NETWORK = 0;
    private static final int OUT_NETWORK = 1;
    private static final int SEARCH = 2;
    private static final int FOUND_SEARCH = 3;

    public ServerProcessClient(Socket client, ArrayList<Funcionario> funcionarios, ArrayList<String> neighbors){
        this.client = client;
        this.funcionarios = funcionarios;
        this.neighbors = neighbors;
    }

    public void run(){
        try{
            //System.out.println("Cliente conectado. IP:"+client.getInetAddress().getHostAddress());
            ObjectInputStream in = new ObjectInputStream(client.getInputStream());
            ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());
                        
            Object[] clientReq = (Object[]) in.readObject();
            
            int type = (int) clientReq[0];

            switch(type){
                case ENTERING_NETWORK:{
                    if(clientReq[1]==null){
                        if(neighbors.size()==0){
                            neighbors.add(client.getInetAddress().getHostAddress());
                            neighbors.add(client.getInetAddress().getHostAddress());
                            Object[] res = {ENTERING_NETWORK, null};
                            out.flush(); //limpar buffer
                            out.writeObject(res);
                            out.close();
                        }else{
                            Object[] req = {ENTERING_NETWORK, client.getInetAddress().getHostAddress()};
                            new Client(neighbors.get(1), 12345, req).start();
                        }
                    }else{
                        
                    }
                    break;
                }
                case OUT_NETWORK:{
                    break;
                }
                case SEARCH:{
                    break;
                }
                case FOUND_SEARCH:{
                    break;
                }
            }
            

            /* out.flush();
            out.writeObject("Hello World");
            out.close();
            client.close(); */
        }catch (Exception e){
            System.out.println("Error:: Thread ProcessClient - "+e.getMessage());
        }
    }

}
