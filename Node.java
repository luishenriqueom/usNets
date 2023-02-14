
import java.io.*;
import java.net.*;
import java.util.*;

public class Node {
    
    private static Scanner sc;

    public static String myIP;

    //Vizinhos de um nó
    //obs: depois usar o array dinamicamente
    public static ArrayList<String> neighbors;

    public static ArrayList<Funcionario> funcionarios;

    public static void main(String[] args){
        getMyIP();
        System.out.println("Meu ip:"+myIP);
        int opc=0;
        neighbors = new ArrayList<String>();
        funcionarios = new ArrayList<Funcionario>();
        System.out.println("Server ouvindo na porta 12345");
        sc = new Scanner(System.in);

        new Server().start();  

        do{
            System.out.println("OPCOES:");
            System.out.println("1 - Entrar na rede;\n2 - Sair da rede;\n3 - Procurar rede;\n4 - Inserir Funcionario;\n5 - Ver meus funcionarios\n6 - Ver conexões\n7 - Sair da aplicação.");
            opc = sc.nextInt();
            sc.nextLine();

            switch(opc){
                case 1:{//entrar na rede
                    System.out.println("Digite o endereço ip de um nó para se conectar:");
                    String ip = sc.nextLine();
                    System.out.println("Digite a porta do nó para se conectar:");
                    int port = sc.nextInt();
                    Object[] req = {ServerProcessClient.ENTERING_NETWORK, null};
                    new Client(ip, port, req).start();
                    break;
                }
                case 2:{//sair da rede
                    if(neighbors.get(0)==neighbors.get(1)){
                        Object[] req = {ServerProcessClient.OUT_NETWORK, neighbors.get(0)};
                        new Client(neighbors.get(0), 12345, req).start();
                        neighbors.clear();
                        System.out.println("Rede desfeita!");
                    }else{
                        Object[] req1 = {ServerProcessClient.OUT_NETWORK, neighbors.get(0)};
                        Object[] req2 = {ServerProcessClient.OUT_NETWORK, neighbors.get(1)};

                        new Client(neighbors.get(0), 12345, req2).start();
                        new Client(neighbors.get(1), 12345, req1).start();
                        //neighbors.clear();
                        System.out.println("Rede desfeita!");
                    }
                    break;
                }
                case 3:{//procurar na rede
                    System.out.println("Digite o CPF a ser buscado:");
                    String CPF = sc.nextLine();
                    //Object[][] req = new Object()/
                    Object[] msg = {CPF, myIP};
                    Object[] req = {ServerProcessClient.SEARCH, msg};
                    new Client(neighbors.get(1), 12345, req).start();
                    System.out.println("Buscando...");
                    break;
                }
                case 4:{//inserir funcionário
                    insertFunc();
                    break;
                }
                case 5:{//ver funcionarios
                    System.out.println("Funcionarios cadastrados:");
                    Iterator<Funcionario> ite = funcionarios.iterator();
                    while(ite.hasNext()){
                        System.out.println(ite.next());
                        System.out.println("-----------------");
                    }
                    break;
                }
                case 6:{
                    System.out.println(neighbors);
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
        funcionarios.add(new Funcionario(CPF, nome, idade));
        System.out.println("Funcionario adicionado!");
    }

    private static void getMyIP(){
        try{
            Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
            for (NetworkInterface netint : Collections.list(nets)){
                if(netint.getName().equals("wlan0")){
                    Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();
                    for (InetAddress inetAddress : Collections.list(inetAddresses)) {
                        if(inetAddress.toString().split("\\.").length>1){
                            myIP = inetAddress.toString().split("/")[1];
                        }
                    }
                }
            }
        }catch(SocketException e){
            e.printStackTrace();
        }
    }

}
