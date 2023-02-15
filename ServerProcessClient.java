import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.Iterator;


public class ServerProcessClient extends Thread implements Serializable {
    
    private Socket client;

    public static final int ENTERING_NETWORK = 0;
    public static final int OUT_NETWORK = 1;
    public static final int SEARCH = 2;
    public static final int FOUND_SEARCH = 3;
    public static final int OUT_OK = 4;

    public ServerProcessClient(Socket client){
        this.client = client;
    }

    public void run(){
        try{
            //System.out.println("RE IP:"+client.getInetAddress().getHostAddress());

            String clientIP = client.getInetAddress().getHostAddress();

            ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(client.getInputStream());
                        
            Object[] clientReq = (Object[]) in.readObject();
            
            int type = (int) clientReq[0];
            
            switch(type){
                case ENTERING_NETWORK:{
                    Filial clientFilial = (Filial) clientReq[1];
                    if((int)clientReq[2]==0){ //NÓ DE FORA QUER ENTRAR
                        if(Node.neighbors.size()==0){ //REDE AINDA NÃO FORMADA
                            System.out.println("Formando rede com:"+clientFilial.nome+" ("+clientIP+")");
                            Node.neighbors.add(clientFilial);
                            Node.neighbors.add(clientFilial);
                            Object[] res = {ENTERING_NETWORK, Node.filial, 0};
                            out.flush(); //limpar buffer
                            out.writeObject(res);
                        }else{ // REDE FORMADA
                            System.out.println(clientFilial.nome+" ("+clientIP+") solicitou entrar na rede");
                            Object[] req = {ENTERING_NETWORK, clientFilial, 1};

                            new Client(Node.neighbors.get(1).IP, req).start();
                            
                            Object[] filiaisResp = {Node.filial, Node.neighbors.get(1)};
                            Object[] res = {ENTERING_NETWORK, filiaisResp, 1};
                            Node.neighbors.set(1, clientFilial);
                            
                            out.flush();
                            out.writeObject(res);                            
                        }
                    }else{ //AJUSTE PARA UM NÓ ENTRAR NA REDE
                        int index = 0;
                        Iterator<Filial> ite = Node.neighbors.iterator();
                        while(ite.hasNext()){
                            if(ite.next().IP.equals(clientIP)){
                                break;
                            }
                            index++;
                        }
                        Node.neighbors.set(index, clientFilial);
                    }
                    out.flush();
                    System.out.println("Vizinhos:"+Node.neighbors);
                    break;
                }
                case OUT_NETWORK:{
                    Iterator<Filial> ite = Node.neighbors.iterator();
                    int index=0;
                    Filial fil;
                    while(ite.hasNext()){
                        fil = ite.next();
                        if(fil.IP.equals(clientIP)){
                            System.out.println("Pedido de saida na rede recebido de "+fil+" ("+clientIP+")");
                            break;
                        }
                        index++;
                    }
                    Filial clientFilial = (Filial) clientReq[1];
                    if(Node.neighbors.size()==0){
                        System.out.println("Sua filial nao esta conectada a rede.");
                    }else{                    
                        if((int) clientReq[2]==0){
                            Node.neighbors.clear();
                            System.out.println("Rede desfeita!");
                        }else{
                            Node.neighbors.set(index, clientFilial);
                            System.out.println("Vizinhos:"+Node.neighbors);
                            out.flush();
                        }
                    }
                    break;
                }
                case SEARCH:{
                    Object[] msg = (Object[]) clientReq[1];
                    System.out.println("Pedido de busca recebido. CPF:"+msg[0]);
                    if(msg[1].equals(Node.filial.IP)){
                        System.out.println("Em uma busca feita anteriormente, nao foi possivel localizar o CPF "+msg[0]);
                    }else{
                        Iterator<Funcionario> ite = Node.filial.funcionarios.iterator();
                        boolean found = false;
                        String searchCPF = (String) msg[0];
                        while(ite.hasNext()){
                            Funcionario func = ite.next();
                            System.out.println(func);
                            if(func.getCPF().equals(searchCPF)){
                                System.out.println("CPF encontrado, repassando para filial solicitante...");
                                found=true;
                                String ip = (String) msg[1];
                                Object[] req = {FOUND_SEARCH, func.toString()};
                                new Client(ip, req).start();;
                            }
                        }
                        if(!found){
                            new Client(Node.neighbors.get(1).IP, clientReq).start();
                        }
                    }
                    out.flush();
                    break;
                }
                case FOUND_SEARCH:{
                    System.out.println("Um CPF buscado anteriormente foi localizado. Dados:\n"+clientReq[1]);
                    out.flush();
                    break;
                }
            }
            
            out.close();
        }catch (Exception e){
            System.out.println("Error:: Thread ServerProcessClient - "+e.getMessage());
        }
    }

}
