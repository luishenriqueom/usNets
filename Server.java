import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server extends Thread{
    
    private ArrayList<String> neighbors;

    public Server(ArrayList<String> neighbors){
        this.neighbors = neighbors;
    }

    public void run(){
        try{
            ServerSocket server = new ServerSocket(12345);
            
            ArrayList<Funcionario> funcionarios = new ArrayList<Funcionario>();

            funcionarios.add(new Funcionario("12345645", "Fabinho da ZN", 45));
            funcionarios.add(new Funcionario("234523", "Nivas da fumaca", 65));
            funcionarios.add(new Funcionario("3456347", "Luisera", 35));

            while(true){ //criar novos clientes
                Socket client = server.accept();
                //IPS.add(client.getInetAddress().getHostAddress());
                new ServerProcessClient(client, funcionarios, neighbors).start();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

}
