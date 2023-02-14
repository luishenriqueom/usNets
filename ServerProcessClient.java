import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Iterator;

public class ServerProcessClient extends Thread{
    
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

            ObjectInputStream in = new ObjectInputStream(client.getInputStream());
            ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());
                        
            Object[] clientReq = (Object[]) in.readObject();
            
            int type = (int) clientReq[0];
            String clientName = (String) clientReq[1];

            switch(type){
                case ENTERING_NETWORK:{
                    if(clientReq[1]==null){
                        if(Filial.neighbors.size()==0){
                            System.out.println("Formando rede com:"+clientName+" ("+clientIP+")");
                            Filial.neighbors.add(clientIP);
                            Filial.neighbors.add(clientIP);
                            //System.out.println(Filial.neighbors);
                            Object[] res = {ENTERING_NETWORK, Filial.nome, null};
                            out.flush(); //limpar buffer
                            out.writeObject(res);
                        }else{
                            System.out.println(clientName+" ("+clientIP+") solicitou entrar na rede");
                            Object[] req = {ENTERING_NETWORK, Filial.nome, clientIP};

                            new Client(Filial.neighbors.get(1), req).start();
                            
                            Object[] res = {ENTERING_NETWORK, Filial.nome, Node.neighbors.get(1)};

                            Filial.neighbors.set(1, clientIP);
                            
                            out.flush();
                            out.writeObject(res);                            
                        }
                    }else{
                        int index = Filial.neighbors.indexOf(clientIP);
                        Filial.neighbors.set(index, (String) clientReq[2]);
                    }
                    System.out.println("Vizinhos:"+Filial.neighbors);
                    break;
                }
                case OUT_NETWORK:{
                    System.out.println("Pedido de saida na rede recebido de "+clientName+" ("+clientIP+")");
                    if(Filial.neighbors.size()==0){
                        System.out.println("Filial nao conetada a rede.");
                    }else{
                        if(Filial.neighbors.get(0).equals(clientIP) && Filial.neighbors.get(1).equals(clientIP)){
                            Filial.neighbors.clear();
                            System.out.println(clientName+" ("+clientIP+") desfez a rede!");
                        }else{
                            int index = Filial.neighbors.indexOf(clientIP);
                            Filial.neighbors.set(index, (String) clientReq[2]);
                            Object[] res = {OUT_OK, Filial.nome, null};
                            System.out.println("Vizinhos:"+Filial.neighbors);
                            out.flush();
                            out.writeObject(res);
                        }                        
                    }
                    break;
                }
                case SEARCH:{
                    System.out.println("Pedido de busca recebido de "+clientName+" ("+clientIP+")");
                    Object[] queryCPF = (Object[]) clientReq[2];
                    if(queryCPF[1].equals(Filial.myIP)){
                        System.out.println("Em uma busca feita anteriormente, não foi possivel localizar o CPF "+queryCPF[0]);
                        break;
                    }
                    System.out.println("CPF buscado:"+queryCPF[0]+
                    "\nQuem está solicitando:"+queryCPF[1]);
                    Iterator ite = Filial.funcionarios.iterator();
                    boolean found = false;
                    while(ite.hasNext()){
                        Funcionario func = (Funcionario) ite.next();
                        //System.out.println("Meu func:"+func);
                        if(func.getCPF().equals(queryCPF[0])){
                            System.out.println("Tenho esse CPF!");
                            Object[] req = {FOUND_SEARCH, Filial.nome, func.toString()};
                            new Client((String) queryCPF[1], req).start();
                            found = true;
                            break;
                        }
                    }
                    if(!found){
                        System.out.println("Não possuo o CPF "+queryCPF[0]+"\nRepassando busca para "+Filial.neighbors.get(1));
                        new Client(Filial.neighbors.get(1), clientReq).start();
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
