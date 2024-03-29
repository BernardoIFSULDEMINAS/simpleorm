// SPDX-FileCopyrightText: 2023 Bernardo Gomes Negri<bernardo.negri@alunos.ifsuldeminas.edu.br>
//
// SPDX-License-Identifier: Apache-2.0

package simpleorm;
import java.util.List;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.stream.Collectors;
import java.lang.reflect.Field;
import simpleorm.Utils;

class FieldTree {
	// Tem subFields se se referir a outra classe (for uma chave estrangeira)
	// e dbField se se referir a um tipo primitivo
	// field NÃO é de o tipo de dbField, field é onde está o dbField
	// numa classe.
	private DBField dbField;
	private List<FieldTree> subFields;
	private Field field;
	public DBField getDbField() {
		return this.dbField;
	}
	public void setDbField(DBField df) {
		this.dbField = df;
		this.subFields = null;
	}
	public List<FieldTree> getSubFields() {
		return this.subFields;
	}
	public void setSubFields(List<FieldTree> subFields) {
		this.subFields = subFields;
		this.dbField = null;
	}
	public Field getField() {
		return this.field;
	}
	public void setField(Field f) {
		this.field = f;
	}
	public FieldTree(Field p) {
		setSubFields(new ArrayList<FieldTree>());
		setField(p);
	}
	public FieldTree(Field p, DBField dbf) {
		setDbField(dbf);
		setField(p);
	}
	public List<DBField> toList() {
		return traverse().stream().map(x -> x.dbf).collect(Collectors.toList());
	}
	public List<PathToDbField> traverse() {
		List<PathToDbField> ret = traverseTree(this, new ImStack<Field>());
                ret.forEach(x -> {x.fieldStack = x.fieldStack.reverseAndFilter(f -> f != null);});
                return ret;
	}
	private static List<PathToDbField> traverseTree(FieldTree t, ImStack<Field> s) {
		List<PathToDbField> empty = new ArrayList<>();
		s = s.push(t.getField());
		if(t.getSubFields() == null) {
			PathToDbField pdbf = new PathToDbField();
			pdbf.dbf = t.getDbField();
			pdbf.fieldStack = s;
			empty.add(pdbf);
			return empty;
		} else {
			for(FieldTree t_o : t.getSubFields()) {
				// Já fiz uns 5 algoritmos recursivos nesse projeto...
				empty.addAll(traverseTree(t_o, s));
			}
			return empty;
		}
	}
	public static FieldTree fromClass(Class<?> cl) {
		SQLTable t = cl.getAnnotation(SQLTable.class);
		FieldTree tree = new FieldTree(null);
		tree.setSubFields(new ArrayList<FieldTree>());
		List<FieldTree> ids = tree.getSubFields();
		if(t == null) {
			throw new IllegalArgumentException("Classe " + cl + " não é tabela!");
		}
		for(Field p : cl.getDeclaredFields()) {
			SQLField s_f = p.getAnnotation(SQLField.class);
			if(s_f == null) {
				continue;
			}
			if(s_f.isId()) {
				DBField maybe = DBField.fromSimpleField(p);
				if(maybe != null) {
					FieldTree this_tree = new FieldTree(p);
					this_tree.setDbField(maybe);
					ids.add(this_tree);
				} else {
					FieldTree this_tree = FieldTree.fromClass(p.getType());
                                        this_tree.setField(p);
                                        if(!s_f.prefix().equals("") || !s_f.value().equals("")) {
                                            for(DBField dbf : this_tree.toList()) {
                                                if(!s_f.value().equals("")) {
                                                    dbf.setName(s_f.value());
                                                } else {
                                                    dbf.setName(s_f.prefix() + dbf.getName());
                                                }
                                            }
                                        }
                                        Utils.debugPrint(cl + " tem " + p.getType());
                                        Utils.debugPrint(this_tree);
                                        ids.add(this_tree);
				}
			}
		}
                Utils.debugPrint("Final:"+tree);
		return tree;
	}

    @Override
    public String toString() {
        return "FieldTree{" + "dbField=" + dbField + ", subFields=" + subFields + ", field=" + field + '}';
    }
        
}