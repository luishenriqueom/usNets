import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;

public class ServerProcessClient extends Thread{
    
    private Socket client;
    // private ArrayList<Funcionario> funcionarios;
    // private ArrayList<String> neighbors;

    public static final int ENTERING_NETWORK = 0;
    public static final int OUT_NETWORK = 1;
    public static final int SEARCH = 2;
    public static final int FOUND_SEARCH = 3;
    public static final int OUT_OK = 4;

    public ServerProcessClient(Socket client){
        this.client = client;
        // this.funcionarios = funcionarios;
        // this.neighbors = neighbors;
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
                        if(Node.neighbors.size()==0){
                            System.out.println("Formando rede com:"+clientIP);
                            Node.neighbors.add(clientIP);
                            Node.neighbors.add(clientIP);
                            //System.out.println(Node.neighbors);
                            Object[] res = {ENTERING_NETWORK, null};
                            out.flush(); //limpar buffer
                            out.writeObject(res);
                        }else{
                            System.out.println(clientIP+" solicitou entrar na rede");
                            Object[] req = {ENTERING_NETWORK, clientIP};

                            new Client(Node.neighbors.get(1), 12345, req).start();
                            
                            Object[] res = {ENTERING_NETWORK, Node.neighbors.get(1)};

                            Node.neighbors.set(1, clientIP);
                            
                            out.flush();
                            out.writeObject(res);                            
                        }
                    }else{
                        int index = Node.neighbors.indexOf(clientIP);
                        Node.neighbors.set(index, (String) clientReq[1]);
                    }
                    System.out.println("Vizinhos:"+Node.neighbors);
                    break;
                }
                case OUT_NETWORK:{
                    System.out.println("Pedido de saida na rede recebido de "+clientIP);
                    if(Node.neighbors.size()==0){
                        System.out.println("Filial nao conetada a rede.");
                    }else{
                        if(Node.neighbors.get(0).equals(clientIP) && Node.neighbors.get(1).equals(clientIP)){
                            Node.neighbors.clear();
                            System.out.println(clientIP+" desfez a rede!");
                        }else{
                            int index = Node.neighbors.indexOf(clientIP);
                            Node.neighbors.set(index, (String) clientReq[1]);
                            Object[] res = {OUT_OK, null};
                            System.out.println("Vizinhos:"+Node.neighbors);
                            out.flush();
                            out.writeObject(res);
                        }                        
                    }
                    break;
                }
                case SEARCH:{
                    System.out.println("Pedido de busca recebido de "+clientIP);
                    Object[] queryCPF = (Object[]) clientReq[1];
                    if(queryCPF[1].equals(Node.myIP)){
                        System.out.println("Em uma busca feita anteriormente, não foi possivel localizar o CPF "+queryCPF[0]);
                        break;
                    }
                    System.out.println("CPF buscado:"+queryCPF[0]+
                    "\nQuem está solicitando:"+queryCPF[1]);
                    Iterator ite = Node.funcionarios.iterator();
                    boolean found = false;
                    while(ite.hasNext()){
                        Funcionario func = (Funcionario) ite.next();
                        System.out.println("Meu func:"+func);
                        if(func.getCPF().equals(queryCPF[0])){
                            System.out.println("Tenho esse CPF!");
                            Object[] req = {FOUND_SEARCH, func.toString()};
                            new Client((String) queryCPF[1], 12345, req).start();
                            found = true;
                            break;
                        }
                    }
                    if(!found){
                        System.out.println("Não possuo o CPF "+queryCPF[0]+"\nRepassando busca para "+Node.neighbors.get(1));
                        new Client(Node.neighbors.get(1), 12345, clientReq).start();
                    }
                    break;
                }
                case FOUND_SEARCH:{
                    System.out.println("Um CPF buscado anteriormente foi localizado. Dados:\n"+clientReq[1]);
                    break;
                }
            }
            
            out.close();
        }catch (Exception e){
            System.out.println("Error:: Thread ProcessClient - "+e.getMessage());
        }
    }

}
