package simpleorm;
import java.util.List;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.stream.Collectors;
import java.lang.reflect.Field;

class FieldTree {
	// Tem subFields se se referir a outra classe (for uma chave estrangeira)
	// e dbField se se referir a um tipo primitivo
	// field NÃO é de o tipo de dbField, field é onde está o dbField
	// numa classe.
	static class PathToDbField {
		ImStack<Field> fieldStack;
		DBField dbf;
		@Override public boolean equals(Object o) {
			if(o == this) {return true;}
			if(!(o instanceof PathToDbField)) {return false;}
			PathToDbField other = (PathToDbField)o;
			if(this.dbf == null) {return other.dbf == null;}
			if(this.fieldStack == null) {return other.fieldStack == null;}
			return other.dbf.equals(this.dbf) && other.fieldStack.equals(this.fieldStack);
		}
	}
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
	public FieldTree() {
		setSubFields(new ArrayList<FieldTree>());
	}
	public FieldTree(Field p, DBField dbf) {
		setDbField(dbf);
		setField(p);
	}
	public List<DBField> toList() {
		return traverseTree(this, new ImStack<Field>()).stream().map(x -> x.dbf).collect(Collectors.toList());
	}
	public List<PathToDbField> traverse() {
		return traverseTree(this, new ImStack<Field>());
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
	}/*
	private DBField next() {
		Stack treeStack;
		Stack iterStack;
		while(t.getSubFields() == null) {
			if(currentIter.hasNext()) {
				t = currentIter.next();
			} else {
				t = a.pop();
				
			}
		}
	}*/
	
	public static FieldTree fromClass(Class<?> cl) {
		SQLTable t = cl.getAnnotation(SQLTable.class);
		FieldTree tree = new FieldTree();
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
					FieldTree this_tree = new FieldTree();
					this_tree.setField(p);
					this_tree.setDbField(maybe);
					ids.add(this_tree);
				} else {
					ids.add(FieldTree.fromClass(p.getType()));
				}
			}
		}
		return tree;
	}
}