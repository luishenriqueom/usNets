
import java.io.*;
import java.net.*;
import java.util.*;

public class Main {
    
    private static Scanner sc;

    public static void main(String[] args){        
        sc = new Scanner(System.in);

        System.out.println("Digite o nome da sua filial:");
        Node.filial = new Filial(sc.nextLine().toUpperCase());
        System.out.println("FILIAL \""+Node.filial.nome+"\"");

        //getMyIP();
        Node.filial.IP = "192.168.100.156";
        System.out.println("SEU IP:"+Node.filial.IP);

        Node.neighbors = new ArrayList<Filial>();
        Node.filial.funcionarios = new ArrayList<Funcionario>();
        
        new Server().start();  
        
        int opc=0;
        do{
            System.out.println("OPCOES:");
            System.out.println("1 - Entrar na rede;\n2 - Sair da rede;\n3 - Procurar funcionario;\n4 - Inserir Funcionario na minha filial;\n5 - Ver meus funcionarios\n6 - Ver conexões\n7 - Sair da aplicação.");
            opc = sc.nextInt();
            sc.nextLine();

            switch(opc){
                case 1:{//entrar na rede
                    System.out.println("Digite o endereço ip de uma filial para se conectar:");
                    String ip = sc.nextLine();
                    Object[] req = {ServerProcessClient.ENTERING_NETWORK, Node.filial, 0};
                    new Client(ip, req).start();
                    break;
                }
                case 2:{//sair da rede
                    if(Node.neighbors.get(0).IP.equals(Node.neighbors.get(1).IP)){
                        Object[] req = {ServerProcessClient.OUT_NETWORK, Node.filial, 0};
                        new Client(Node.neighbors.get(0).IP, req).start();
                        System.out.println("Rede desfeita!");
                    }else{
                        Object[] req1 = {ServerProcessClient.OUT_NETWORK, Node.neighbors.get(0), 1};
                        Object[] req2 = {ServerProcessClient.OUT_NETWORK, Node.neighbors.get(1), 1};

                        new Client(Node.neighbors.get(0).IP, req2).start();
                        new Client(Node.neighbors.get(1).IP, req1).start();
                        System.out.println("Saiu da rede!");
                    }
                    Node.neighbors.clear();
                    break;
                }
                case 3:{//procurar funcionario
                    System.out.println("Digite o CPF a ser buscado:");
                    String CPF = sc.nextLine();

                    Iterator<Funcionario> ite = Node.filial.funcionarios.iterator();
                    boolean found=false;
                    while(ite.hasNext()){
                        Funcionario func = ite.next();
                        if(func.getCPF().equals(CPF)){
                            found=true;
                            System.out.println("CPF encontrado na propria filial. Dados\n"+func.toString());                            
                            break;
                        }
                    }
                    if(!found){
                        Iterator<Filial> neighborFiliais = Node.neighbors.iterator();
                        while(neighborFiliais.hasNext()){
                            ite = neighborFiliais.next().funcionarios.iterator();
                            while(ite.hasNext()){
                                Funcionario func = ite.next();
                                if(func.getCPF().equals(CPF)){
                                    found=true;
                                    System.out.println("CPF encontrado na filial vizinha . Dados\n"+func.toString()); 
                                    break; 
                                }
                            }
                            if(found) break;
                        }
                    }
                    if(!found){
                        Object[] msg = {CPF, Node.filial.IP};
                        Object[] req = {ServerProcessClient.SEARCH, msg};
                        new Client(Node.neighbors.get(1).IP, req).start();
                        System.out.println("Buscando...");
                    }
                    break;
                }
                case 4:{//inserir funcionário
                    insertFunc();
                    break;
                }
                case 5:{//ver funcionarios
                    System.out.println("Funcionarios cadastrados:");
                    Iterator<Funcionario> ite = Node.filial.funcionarios.iterator();
                    while(ite.hasNext()){
                        System.out.println(ite.next());
                        System.out.println("-----------------");
                    }
                    break;
                }
                case 6:{
                    System.out.println(Node.neighbors);
                    break;
                }
                case 7:{
                    System.out.println("Processo finalizado");
                    break;
                }
                default:{
                    System.out.println("Opcao invalida.");
                    break;
                }
            }

        }while(opc!=7);
        
        sc.close();

    }

    private static void insertFunc(){
        System.out.println("Digite o nome do funcionario:");
        String nome = sc.nextLine();
        System.out.println("Digite o CPF do funcionario:");
        String CPF = sc.nextLine();
        System.out.println("Digite a idade do funcionario:");
        int idade = sc.nextInt();
        sc.nextLine();
        Node.filial.funcionarios.add(new Funcionario(CPF, nome, idade));
        System.out.println("Funcionario adicionado!");
    }

    private static void getMyIP(){
        try{
            Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
            for (NetworkInterface netint : Collections.list(nets)){
                if(netint.getName().startsWith("wlan")){
                    Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();
                    for (InetAddress inetAddress : Collections.list(inetAddresses)) {
                        if(inetAddress.toString().split("\\.").length>1){
                            Node.filial.IP = inetAddress.toString().split("/")[1];
                        }
                    }
                }
            }
        }catch(SocketException e){
            e.printStackTrace();
        }

    }

}
