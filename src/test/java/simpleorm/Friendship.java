// SPDX-FileCopyrightText: 2023 Bernardo Gomes Negri<bernardo.negri@alunos.ifsuldeminas.edu.br>
//
// SPDX-License-Identifier: Apache-2.0

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simpleorm;

import java.util.Objects;

/**
 *
 * @author 13828523633
 */
// CREATE TABLE friendship (user1 INT NOT NULL, user2 INT NOT NULL, PRIMARY KEY (user1, user2),
// CONSTRAINT FOREIGN KEY (user1) REFERENCES user(id), CONSTRAINT FOREIGN KEY (user2) REFERENCES user(id));
@SQLTable("friendship")
public class Friendship {
   @SQLField(isId = true, value = "user1")
   private Usuario user1;
   @SQLField(isId = true, value = "user2")
   private Usuario user2;

    public Usuario getUser1() {
        return user1;
    }

    public void setUser1(Usuario user1) {
        this.user1 = user1;
    }

    public Usuario getUser2() {
        return user2;
    }

    public void setUser2(Usuario user2) {
        this.user2 = user2;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + Objects.hashCode(this.user1);
        hash = 97 * hash + Objects.hashCode(this.user2);
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
        final Friendship other = (Friendship) obj;
        if (!Objects.equals(this.user1, other.user1)) {
            return false;
        }
        if (!Objects.equals(this.user2, other.user2)) {
            return false;
        }
        return true;
    }
    @Override
    public String toString() {
        return "{user1: (" + user1.toString() + "), user2: (" + user2.toString() + ")}";
    }
}
