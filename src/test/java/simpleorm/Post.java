// SPDX-FileCopyrightText: 2023 Bernardo Gomes Negri<bernardo.negri@alunos.ifsuldeminas.edu.br>
//
// SPDX-License-Identifier: Apache-2.0

package simpleorm;

import java.util.Objects;

//Entidade de exemplo

import java.util.Objects;

@SQLTable("post")
public class Post {
    @SQLField(value = "id", isId = true)
    private Integer id;
    public Integer getId() {return this.id;}
    public void setId(Integer id) {this.id = id;}
    @SQLField("text")
    private String texto;
    public String getTexto() {return this.texto;}
    public void setTexto(String tantofaz2) {this.texto = tantofaz2;}
	@SQLField("user")
    private Usuario author;
    public Usuario getAuthor() {return this.author;}
    public void setAuthor(Usuario tantofaz2) {this.author = tantofaz2;}
    @Override public String toString() {
        return "id:" + this.id + ",texto:" + this.texto + ",author:(" + this.author + ")";
    }
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + Objects.hashCode(this.id);
        hash = 67 * hash + Objects.hashCode(this.texto);
        hash = 67 * hash + Objects.hashCode(this.author);
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
        final Post other = (Post) obj;
        if (!Objects.equals(this.texto, other.texto)) {
            return false;
        }
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        if (!Objects.equals(this.author, other.author)) {
            return false;
        }
        return true;
    }
    
}
