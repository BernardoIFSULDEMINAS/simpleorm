/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simpleorm;

import java.lang.reflect.Field;

/**
 *
 * @author 13828523633
 */

public class PathToDbField {
		ImStack<Field> fieldStack;
		DBField dbf;
		@Override public boolean equals(Object o) {
			if(o == this) {return true;}
			if(!(o instanceof PathToDbField)) {return false;}
			PathToDbField other = (PathToDbField)o;
			if(this.dbf == null) {return other.dbf == null;}
			if(this.fieldStack == null) {return other.fieldStack == null;}
                        if((this.dbf == null) != (other.dbf == null)) return false;
                        if((this.fieldStack == null) != (other.fieldStack == null)) return false;
                	return other.dbf.equals(this.dbf) && other.fieldStack.equals(this.fieldStack);
		}

    @Override
    public String toString() {
        return "PathToDbField{" + "fieldStack=" + fieldStack + ", dbf=" + dbf + '}';
    }
	}
