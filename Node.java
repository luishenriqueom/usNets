
import java.io.*;
import java.util.Scanner;
import java.util.ArrayList;

public class Node {
    
    //Vizinhos de um nó
    private ArrayList<String> neighbors;
    public static void main(String[] args){
        int opc;
        System.out.println("Server ouvindo na porta 12345");

        new Server(neighbors).start();  

        while(opc!=5){
            System.out.println("OPCOES:");
            System.out.println("1 - Entrar na rede;\n2 - Sair da rede;\n3 - Procurar rede;\n4 - Retornar resultado;\n 5 - Finalizar operações.");
            
        }
        
        

    }

}
