package simpleorm;

//Entidade de exemplo
@SQLTable("post")
public class Post {
    @SQLField(value = "id", isId = true)
    private Integer id;
    public Integer getId() {return this.id;}
    public void setCodigo(Integer id) {this.id = id;}
    @SQLField("text")
    private String texto;
    public String getTexto() {return this.texto;}
    public void setTexto(String tantofaz2) {this.texto = tantofaz2;}
	@SQLField("user")
    private Usuario author;
    public Usuario getAuthor() {return this.author;}
    public void setAuthor(Usuario tantofaz2) {this.author = tantofaz2;}
}
