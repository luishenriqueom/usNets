import java.io.Serializable;
import java.util.*;

public class Filial implements Serializable{
	
    private static final long serialVersionUID = 1L;

	public String nome;

    public ArrayList<Funcionario> funcionarios;

	public String IP;

	public Filial(String nome){
		this.nome = nome;
	}

	public String toString(){
		return nome;
	}

}
