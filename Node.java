
import java.io.*;
import java.util.Scanner;
import java.util.ArrayList;

public class Node {
    
    //Vizinhos de um nó
    //obs: depois usar o array dinamicamente
    private static ArrayList<String> neighbors;
    public static void main(String[] args){
        int opc=0;
        neighbors = new ArrayList<String>();
        System.out.println("Server ouvindo na porta 12345");
        Scanner sc = new Scanner(System.in);

        new Server(neighbors).start();  

        do{
            System.out.println("OPCOES:");
            System.out.println("1 - Entrar na rede;\n2 - Sair da rede;\n3 - Procurar rede;\n4 - Retornar resultado;\n5 - Ver conexões\n6 - Finalizar operações.");
            opc = sc.nextInt();
            sc.nextLine();

            switch(opc){
                case 1:{//entrar na rede
                    System.out.println("Digite o endereço ip de um nó para se conectar:");
                    String ip = sc.nextLine();
                    System.out.println("Digite a porta do nó para se conectar:");
                    int port = sc.nextInt();
                    Object[] req = {ServerProcessClient.ENTERING_NETWORK, null};
                    new Client(ip, port, req, neighbors).start();
                    break;
                }
                case 2:{//sair da rede
                    if(neighbors.get(0)==neighbors.get(1)){
                        Object[] req = {ServerProcessClient.OUT_NETWORK, neighbors.get(0)};
                        new Client(neighbors.get(0), 12345, req, neighbors).start();
                        neighbors.clear();
                        System.out.println("Rede desfeita!");
                    }else{
                        Object[] req1 = {ServerProcessClient.OUT_NETWORK, neighbors.get(0)};
                        Object[] req2 = {ServerProcessClient.OUT_NETWORK, neighbors.get(1)};

                        new Client(neighbors.get(0), 12345, req2, neighbors).start();
                        new Client(neighbors.get(1), 12345, req1, neighbors).start();
                        //neighbors.clear();
                        System.out.println("Rede desfeita!");
                    }
                    break;
                }
                case 3:{//procurar na rede
                    break;
                }
                case 4:{//CPF encontrado
                    break;
                }
                case 5:{
                    System.out.println(neighbors);
                    break;
                }
                case 6:{
                    System.out.println("Processo finalizado");
                    break;
                }
                default:{
                    System.out.println("Opcao invalida.");
                    break;
                }
            }

        }while(opc!=6);
        
        sc.close();

    }

}
