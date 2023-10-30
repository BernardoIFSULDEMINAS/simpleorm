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
	private List<PathToDbField> fields;
	private List<PathToDbField> ids;
	private SQLTable t;
	private DBConnection db;
	private Class<T> classe;
	
	private static void addField(SQLField s_f, FieldTree new_t, FieldTree fieldsTree, FieldTree idsTree) {
		fieldsTree.getSubFields().add(new_t);
		if(s_f.isId()) {
			idsTree.getSubFields().add(new_t);
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
                FieldTree fieldsTree = new FieldTree(null);
                FieldTree idsTree = new FieldTree(null);
		for(Field p : cl.getDeclaredFields()) {
			SQLField s_f = p.getAnnotation(SQLField.class);
			if(s_f == null) {
				continue;
			}
			DBField d_fmaybe = DBField.fromSimpleField(p);
			if(d_fmaybe != null) {
				addField(s_f, new FieldTree(p, d_fmaybe), fieldsTree, idsTree);
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
				addField(s_f, ids, fieldsTree, idsTree);
			}
		}
                this.fields = fieldsTree.traverse();
                this.ids = idsTree.traverse();
	}
	
	private static Object getFromObj(Object coisa, Field p) {
		String javaFieldName = p.getName();
		String methodName = "get" + javaFieldName.substring(0,1).toUpperCase() + javaFieldName.substring(1);
		Class<?> classe = coisa.getClass();
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
			throw new RuntimeException(e.getMessage());
		}
	}
	
	private static void setFromObj(Object coisa, Field p, Object c) {
		String javaFieldName = p.getName();
		Class<?> classe = coisa.getClass();
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
	
	private static Object getFromPathToDbField(Object coisa, ImStack<Field> st) {
		Field f = st.peek();
		st = st.pop();
		while(st != null && f != null) {
			coisa = getFromObj(coisa, f);
			f = st.peek();
			st = st.pop();
		}
		return coisa;
	}
	
	//Eu tenho mil dessas subclasses que têm literalmente só dois campos
	//Por isso que Haskell é melhor: tem tuplas onde você pode armazenar
	//qualquer grupo de coisas
	private class ValueAndDbField {
		Object val;
		DBField dbf;
	}
        
        private List<ValueAndDbField> addNonIdParens(T coisa, StringBuilder sb) {
            if(sb != null) sb.append(" (");
            List<ValueAndDbField> nonidparens = new ArrayList<>();
            for(int i = 0; i < fields.size(); i++) {
                PathToDbField pdbf = fields.get(i);
                Object this_val = getFromPathToDbField(coisa, pdbf.fieldStack);
                if(this_val == null && ids.contains(pdbf)) {
                        continue;
                }
                ValueAndDbField vdbf = new ValueAndDbField();
                vdbf.val = this_val;
                vdbf.dbf = pdbf.dbf;
                nonidparens.add(vdbf);
                if(sb != null) sb.append(pdbf.dbf.getName());
                if(i != fields.size() - 1 && sb != null) {
                        sb.append(",");
                }
            }
            return nonidparens;
        }
        
        private List<ValueAndDbField> addIdParens(T coisa, StringBuilder sb) {
            if(sb != null) sb.append(" (");
            List<ValueAndDbField> idparens = new ArrayList<>();
            for(int i = 0; i < ids.size(); i++) {
                PathToDbField pdbf = ids.get(i);
                Object this_val = getFromPathToDbField(coisa, pdbf.fieldStack);
                ValueAndDbField vdbf = new ValueAndDbField();
                vdbf.val = this_val;
                vdbf.dbf = pdbf.dbf;
                idparens.add(vdbf);
                if(sb != null) sb.append(pdbf.dbf.getName());
                if(i != ids.size() - 1 && sb != null) {
                        sb.append(",");
                }
            }
            return idparens;
        }
        
	private boolean criar(T coisa) throws SQLException {
		StringBuilder sb = new StringBuilder();
		sb.append("insert into ");
		sb.append(this.t.value());
		List<ValueAndDbField> nonidfields = addNonIdParens(coisa, sb);
		sb.append(") values (");
		for(int i = 0; i < nonidfields.size(); i++) {
			sb.append("?");
			if(i != nonidfields.size() - 1) {
				sb.append(",");
			}
		}
		sb.append(");");
		String sql_completo = sb.toString();
		PreparedStatement ps = this.db.getStatement(sql_completo);
		int i_fields = 1;
		for(ValueAndDbField field : nonidfields) {
			SQLType.addSimpleToPst(field.dbf.getType(), field.val, ps, i_fields);
			i_fields++;
		}
		int ret = ps.executeUpdate();
		// Teria algo de muito errado se retornasse mais de 1, mas deixa assim por enquanto
		return ret > 0;
	}
	
	private boolean mudar(T coisa) throws SQLException {
            StringBuilder sb = new StringBuilder();
            sb.append("update ");
            sb.append(this.t.value());
            sb.append(" set ");
            List<ValueAndDbField> nonidfields = addNonIdParens(coisa, null);
            for(int i = 0; i < nonidfields.size(); i++) {
                ValueAndDbField vdbf = nonidfields.get(i);
                sb.append(vdbf.dbf.getName());
                sb.append("=?");
                if(i != nonidfields.size() - 1) {
                    sb.append(",");
                }
            }
            sb.append(" where ");
            List<ValueAndDbField> idfields = addIdParens(coisa, null);
            for(int i = 0; i < idfields.size(); i++) {
                ValueAndDbField vdbf = idfields.get(i);
                sb.append(vdbf.dbf.getName());
                sb.append("=?");
                if(i != idfields.size() - 1) {
                    sb.append(" and ");
                }
            }
            sb.append(";");
            PreparedStatement ps = this.db.getStatement(sb.toString());
            int i_fields = 1;
            for(ValueAndDbField vdbf : nonidfields) {
                SQLType.addSimpleToPst(vdbf.dbf.getType(), vdbf.val, ps, i_fields);
                i_fields++;
            }
            for(ValueAndDbField vdbf : idfields) {
                SQLType.addSimpleToPst(vdbf.dbf.getType(), vdbf.val, ps, i_fields);
                i_fields++;
            }
            return ps.executeUpdate() > 0;
	}
	
	public boolean salvar(T coisa) throws SQLException {
		for(PathToDbField pdbf : this.ids) {
			if(getFromPathToDbField(coisa,pdbf.fieldStack) == null) {
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

