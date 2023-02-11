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
            //System.out.println("RE IP:"+client.getInetAddress().getHostAddress());

            String clientIP = client.getInetAddress().getHostAddress();

            ObjectInputStream in = new ObjectInputStream(client.getInputStream());
            ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());
                        
            Object[] clientReq = (Object[]) in.readObject();
            
            int type = (int) clientReq[0];

            switch(type){
                case ENTERING_NETWORK:{
                    if(clientReq[1]==null){
                        if(neighbors.size()==0){
                            System.out.println("Formando rede com:"+clientIP);
                            neighbors.add(clientIP);
                            neighbors.add(clientIP);
                            //System.out.println(neighbors);
                            Object[] res = {ENTERING_NETWORK, null};
                            out.flush(); //limpar buffer
                            out.writeObject(res);
                        }else{
                            System.out.println(clientIP+" solicitou entrar na rede");
                            Object[] req = {ENTERING_NETWORK, clientIP};

                            new Client(neighbors.get(1), 12345, req, neighbors).start();
                            
                            Object[] res = {ENTERING_NETWORK, neighbors.get(1)};

                            neighbors.set(1, clientIP);
                            
                            out.flush();
                            out.writeObject(res);                            
                        }
                    }else{
                        int index = neighbors.indexOf(clientIP);
                        neighbors.set(index, (String) clientReq[1]);
                    }
                    System.out.println("Vizinhos:"+neighbors);
                    break;
                }
                case OUT_NETWORK:{
                    System.out.println("Pedido de saida na rede recebido de "+clientIP);
                    if(neighbors.size()==0){
                        System.out.println("Filial nao conetada a rede.");
                    }else{
                        if(neighbors.get(0).equals(clientIP) && neighbors.get(1).equals(clientIP)){
                            neighbors.clear();
                            System.out.println(clientIP+" desfez a rede!");
                        }else{
                            int index = neighbors.indexOf(clientIP);
                            neighbors.set(index, (String) clientReq[1]);
                            Object[] res = {OUT_OK, null};
                            System.out.println("Vizinhos:"+neighbors);
                            out.flush();
                            out.writeObject(res);
                        }                        
                    }
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
