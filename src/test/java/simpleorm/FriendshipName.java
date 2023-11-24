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
@SQLTable("friendshipname")
public class FriendshipName {
    /*
    CREATE TABLE `friendshipname` (
	`n` VARCHAR(50) NOT NULL
	`fsuser1` INT(11) NOT NULL,
	`fsuser2` INT(11) NOT NULL,
	PRIMARY KEY (`fsuser1`, `fsuser2`),
	CONSTRAINT `FK__friendship` FOREIGN KEY (`fsuser1`, `fsuser2`) REFERENCES `databaseexemplo`.`friendship` (`user1`, `user2`) ON UPDATE RESTRICT ON DELETE RESTRICT
);
    */
    @SQLField(value = "n")
    private String n;
    @SQLField(prefix = "fs", isId = true)
    private Friendship fs;

    public String getN() {
        return n;
    }

    public void setN(String n) {
        this.n = n;
    }

    public Friendship getFs() {
        return fs;
    }

    public void setFs(Friendship fs) {
        this.fs = fs;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + Objects.hashCode(this.n);
        hash = 89 * hash + Objects.hashCode(this.fs);
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
        final FriendshipName other = (FriendshipName) obj;
        if (!Objects.equals(this.n, other.n)) {
            return false;
        }
        if (!Objects.equals(this.fs, other.fs)) {
            return false;
        }
        return true;
    }
    
}
