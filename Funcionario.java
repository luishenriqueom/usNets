import java.io.Serializable;

public class Funcionario implements Serializable{
    
    private static final long serialVersionUID = 1L;

    private String CPF;
    private String Nome;
    private int Idade;

    public Funcionario(String CPF, String Nome, int Idade){
        this.CPF = CPF;
        this.Nome = Nome;
        this.Idade = Idade;
    }

    public void setCPF(String cPF) {
        CPF = cPF;
    }

    public void setIdade(int idade) {
        Idade = idade;
    }

    public void setNome(String nome) {
        Nome = nome;
    }

    public String getCPF() {
        return CPF;
    }

    public int getIdade() {
        return Idade;
    }

    public String getNome() {
        return Nome;
    }

    public String toString(){
        return "Nome:"+Nome+"\nCPF:"+CPF+"\nIdade:"+Idade;
    }

}
