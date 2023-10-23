package simpleorm;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.NoSuchMethodException;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import simpleorm.SQLTable;
import simpleorm.DBConnection;
import simpleorm.DBField;
import simpleorm.SQLField;
import simpleorm.SQLType;

/**
 * Hello world!
 *
 */
public class DAO<T>
{
	private FieldTree fields = new FieldTree<>();
	private FieldTree ids = new FieldTree<>();
	private SQLTable t;
	private DBConnection db;
	private Class<T> classe;
	
	private void addField(SQLField s_f, FieldTree ft) {
		fields.getSubFields().add(ft);
		if(s_f.isId()) {
			ids.getSubFields().add(ft);
		}
	}
	
    public DAO(Class<T> cl, DBConnection db) {
		this.db = db;
		this.classe = cl;
		SQLTable t = cl.getAnnotation(SQLTable.class);
		if(t == null) {
			throw new IllegalArgumentException("Classe " + cl + " não é tabela!");
		}
		this.t = t;
		for(Field p : cl.getDeclaredFields()) {
			SQLField s_f = p.getAnnotation(SQLField.class);
			if(s_f == null) {
				continue;
			}
			DBField d_fmaybe = DBField.fromSimpleField(p);
			if(d_fmaybe != null) {
				addField(s_f, new FieldTree(p, d_fmaybe));
			} else {
				// É algo relacionado, quando precisamos armazenar algo relacionado, armazenamos suas primary keys
				FieldTree ids = FieldTree.fromClass(p.getType());
				ids.setField(p);
				for(DBField id : ids.toList()) {
					if(!s_f.value().equals("")) {
						id.setName(s_f.value());
					} else {
						id.setName(s_f.prefix() + id.getName());
					}
				}
				addField(s_f, ids);
			}
		}
	}
	
	private static Object getFromObj(T coisa, Field p) {
		String javaFieldName = p.getName();
		String methodName = "get" + javaFieldName.substring(0,1).toUpperCase() + javaFieldName.substring(1);
		try {
			Method m = classe.getMethod(methodName);
			return m.invoke(coisa);
		} catch(NoSuchMethodException e) {
			System.err.println(e.getMessage());
			throw new RuntimeException("Classe " + coisa.getClass() + " não tem método " + methodName + "!");
		} catch (IllegalAccessException e) {
			System.err.println(e.getMessage());
			throw new RuntimeException("Método " + methodName + " precisa ser público!");
		} catch (Exception e) {
			//Espero que Java esteja feliz agora
			throw new RuntimeException(e.getMessage());
		}
	}
	
	private static void setFromObj(T coisa, Field p, Object c) {
		String javaFieldName = p.getName();
		String methodName = "set" + javaFieldName.substring(0,1).toUpperCase() + javaFieldName.substring(1);
		try {
			Method m = classe.getMethod(methodName, c.getClass());
			m.invoke(coisa, c);
		} catch(NoSuchMethodException e) {
			System.err.println(e.getMessage());
			throw new RuntimeException("Classe " + coisa.getClass() + " não tem método " + methodName + "!");
		} catch (IllegalAccessException e) {
			System.err.println(e.getMessage());
			throw new RuntimeException("Método " + methodName + " precisa ser público!");
		} catch (Exception e) {
			//Espero que Java esteja feliz agora
			throw new RuntimeException(e.getMessage());
		}
	}
	
	private static void intoPst(DBField df, Object value, PreparedStatement pst, int i) {
		if(df.getRelatedTo() == null) {
			SQLType.addSimpleToPst(df.getType(), getFromObj(coisa, fdbf.javaField), ps, i_fields);
		} else {
			SQLType.addSimpleToPst(df.getType(), );
		}
	}
	
	private boolean criar(T coisa) throws SQLException {
		StringBuilder sb = new StringBuilder();
		int n_fields = 0;
		sb.append("insert into ");
		sb.append(this.t.value());
		sb.append(" (");
		for(int i = 0; i < this.fields.size(); i++) {
			FieldAndDBField fdbf = this.fields.get(i);
			if(getFromObj(coisa, fdbf.javaField) == null && this.ids.contains(fdbf)) {
				continue;
			}
			for(int j = 0; j < fdbf.associatedFields.size(); j++) {
				sb.append(fdbf.associatedFields.get(j).getName());
				n_fields++;
				if(!(j == fdbf.associatedFields.size() - 1 && i == this.fields.size() - 1)) {
					sb.append(",");
				}
			}
		}
		sb.append(") values (");
		for(int i = 0; i < n_fields; i++) {
			sb.append("?");
			if(i != n_fields - 1) {
				sb.append(",");
			}
		}
		sb.append(");");
		String sql_completo = sb.toString();
		System.out.println("SQL completo: " + sql_completo);
		PreparedStatement ps = this.db.getStatement(sql_completo);
		int i_fields = 1;
		for(FieldAndDBField fdbf : this.fields()) {
			if(getFromObj(coisa, fdbf.javaField) == null && this.ids.contains(fdbf)) {
				continue;
			}
			for(DBField df : fdbf.associatedFields) {
				if(df.getRelatedTo() == null) {
					SQLType.addSimpleToPst(df.getType(), getFromObj(coisa, fdbf.javaField), ps, i_fields);
				} else {
					
					SQLType.addSimpleToPst(df.getType(), )
				}
				i_fields++;
			}
		}
		throw new UnsupportedOperationException();
	}
	
	private boolean mudar(T coisa) throws SQLException {
		throw new UnsupportedOperationException();
	}
	
	public boolean salvar(T coisa) throws SQLException {
		for(FieldAndDBField fdbf : this.ids) {
			if(getFromObj(coisa,fdbf.javaField) == null) {
				return criar(coisa);
			}
		}
		return mudar(coisa);
	}
	
	public boolean apagar(T coisa) throws SQLException {
		throw new UnsupportedOperationException();
	}
	
	public T localizar(Object... ids) throws SQLException {
		throw new UnsupportedOperationException();
	}
	
	public List<T> getList() {
		throw new UnsupportedOperationException();
	}
}
