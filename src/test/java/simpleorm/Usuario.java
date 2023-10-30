package simpleorm;

//Entidade de exemplo

import java.util.Objects;

@SQLTable("user")
public class Usuario {
    @SQLField(value = "id", isId = true)
    private Integer codigo;
    public Integer getCodigo() {return this.codigo;}
    public void setCodigo(Integer tantofaz) {this.codigo = tantofaz;}
    @SQLField("name")
    private String nome;
    public String getNome() {return this.nome;}
    public void setNome(String tantofaz2) {this.nome = tantofaz2;}

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + Objects.hashCode(this.codigo);
        hash = 97 * hash + Objects.hashCode(this.nome);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Usuario other = (Usuario) obj;
        if (!Objects.equals(this.nome, other.nome)) {
            return false;
        }
        if (!Objects.equals(this.codigo, other.codigo)) {
            return false;
        }
        return true;
    }
    
}
