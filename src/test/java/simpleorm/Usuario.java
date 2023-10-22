package simpleorm;

//Entidade de exemplo
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
}
