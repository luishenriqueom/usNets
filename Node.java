
import java.io.*;
import java.util.Scanner;
import java.util.ArrayList;

public class Node {
    
    //Vizinhos de um nó
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
                case 1:{
                    System.out.println("Digite o endereço ip de um nó para se conectar:");
                    String ip = sc.nextLine();
                    System.out.println("Digite a porta do nó para se conectar:");
                    int port = sc.nextInt();
                    Object[] req = {ServerProcessClient.ENTERING_NETWORK, null};
                    new Client(ip, port, req, neighbors).start();
                    break;
                }
                case 2:{
                    break;
                }
                case 3:{
                    break;
                }
                case 4:{
                    break;
                }
                case 5:{
                    System.out.println(neighbors);
                    break;
                }
                case 6:{
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
