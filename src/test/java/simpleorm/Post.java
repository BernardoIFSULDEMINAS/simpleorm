package simpleorm;

import java.util.Objects;

//Entidade de exemplo
@SQLTable("post")
public class Post {
    @SQLField(value = "id", isId = true)
    private Integer id;
    public Integer getId() {return this.id;}
    public void setId(Integer tantofaz3) {this.id = tantofaz3;}
    public void setCodigo(Integer id) {this.id = id;}
    @SQLField("text")
    private String texto;
    public String getTexto() {return this.texto;}
    public void setTexto(String tantofaz2) {this.texto = tantofaz2;}
	@SQLField("user")
    private Usuario author;
    public Usuario getAuthor() {return this.author;}
    public void setAuthor(Usuario tantofaz2) {this.author = tantofaz2;}
    @Override public boolean equals(Object other)  {
        if(this == other) return true;
        if(other == null) return false;
        if(getClass() != other.getClass()) return false;
        Post o = (Post)other;
        if(!Objects.equals(this.id, o.id)) return false;
        if(!Objects.equals(this.texto, o.texto)) return false;
        if(!Objects.equals(this.author, o.author)) return false;
        return true;
    }
    @Override public int hashCode() {
        int hash = 11;
        hash = 23 * hash + Objects.hash(this.id, this.author, this.texto);
        return hash;
    }
    @Override public String toString() {
        return "id:" + this.id + ",texto:" + this.texto + ",author:(" + this.author + ")";
    }
}
