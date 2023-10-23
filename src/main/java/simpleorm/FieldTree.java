package simpleorm;
import java.util.Stream;
import java.util.List;
import java.util.ArrayList;
import java.lang.reflect.Field;

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
	public FieldTree() {
		setSubFields(new ArrayList<FieldTree>());
	}
	public FieldTree(Field p, DBField dbf) {
		setDBField(dbf);
		setField(p);
	}
	public List<DBField> toList() {
		return traverseTree(this);
	}
	private static List<DBField> traverseTree(FieldTree t) {
		List<DBField> empty = new ArrayList<>();
		if(t.getSubFields() == null) {
			empty.add(t.getDbField());
			return empty;
		} else {
			for(FieldTree t_o : t.getSubFields()) {
				// Já fiz uns 5 algoritmos recursivos nesse projeto...
				empty.addAll(traverseTree(t_o));
				return empty;
			}
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
		FieldTree tree = new FieldTree<>();
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
				FieldTree this_tree = new FieldAndDBField();
				this_tree.setField(p);
				if(maybe != null) {
					this_tree.setDBField(maybe);
					ids.add(maybe);
				} else {
					this_tree.setSubFields(FieldTree.fromClass(p.getType()));
					ids.add(this_tree);
				}
			}
		}
		return tree;
	}
}