public class Client extends Thread{
    
    private String HOST;
    private int PORT;
    private Object[] MESSAGE;

    public Client(String HOST, int PORT, Object[] MESSAGE){
        this.HOST = HOST;
        this.PORT = PORT;
        this.MESSAGE = MESSAGE;
    }

    public void run(){
        
    }

}
