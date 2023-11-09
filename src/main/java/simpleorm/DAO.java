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
import java.sql.ResultSet;
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
        private Map<Class<?>,DAO<?>> mapaDaos = new HashMap<>();
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
                                mapaDaos.put(p.getType(), new DAO(p.getType(), db));
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
	private static void setFromPathToDbField(Object coisa, ImStack<Field> st, Object val) {
            Field f;
            f = st.peek();
            st = st.pop();
            try {
                while(st != null && f != null && !st.isEmpty()) {
                    Object coisa_t = getFromObj(coisa, f);
                    if(coisa_t == null) {
                        setFromObj(coisa, f, f.getType().getConstructor().newInstance());
                        coisa_t = getFromObj(coisa, f);
                    }
                    coisa = coisa_t;
                    f = st.peek();
                    st = st.pop();
                }
                setFromObj(coisa, f, val);
            } catch(InstantiationException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException("Classe " + f.getType() + " precisa ter um construtor vazio público");
            }
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
            StringBuilder sb = new StringBuilder();
            sb.append("delete from ");
            sb.append(this.t.value());
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
            for(ValueAndDbField vdbf : idfields) {
                SQLType.addSimpleToPst(vdbf.dbf.getType(), vdbf.val, ps, i_fields);
                i_fields++;
            }
            return ps.executeUpdate() > 0;
	}
	
        private T fromRs(ResultSet rs) throws SQLException {
            try {
                T coisa = this.classe.getConstructor().newInstance();
                Map<Field,List<Object>> fieldToIds = new HashMap<>();
                for(PathToDbField pdbf : this.fields) {
                    DAO<?> daoField = mapaDaos.getOrDefault(pdbf.fieldStack.peek(), null);
                    if(daoField == null) {
                        setFromPathToDbField(coisa, pdbf.fieldStack, SQLType.fromSimpleToPst(pdbf.dbf, rs, pdbf.dbf.getJavaClass()));
                    } else {
                        // Settar algo relacionado.
                        List<Object> maybeIds = fieldToIds.getOrDefault(pdbf.fieldStack.peek(), null);
                        if(maybeIds == null) {
                            List<Object> ids = new ArrayList<>();
                            fieldToIds.put(pdbf.fieldStack.peek(), ids);
                            maybeIds = ids;
                        }
                        maybeIds.add(SQLType.fromSimpleToPst(pdbf.dbf, rs, pdbf.fieldStack.getLast().getType()));
                    }
                }
                for(Map.Entry<Field,List<Object>> item : fieldToIds.entrySet()) {
                    setFromPathToDbField(coisa, new ImStack(item.getKey(), null), mapaDaos.get(item.getKey().getType()).localizar(item.getValue()));
                }
                return coisa;
            } catch(NoSuchMethodException e) {
                System.err.println(e.getMessage());
                throw new RuntimeException("Classe " + this.classe + " deve ter um construtor vazio!");
            } catch (Exception e) {
                //Espero que Java esteja feliz agora
                throw new RuntimeException(e.getMessage());
            }
        }
        
	public T localizar(Object... ids) throws SQLException {
            StringBuilder sb = new StringBuilder();
            sb.append("select * from ");
            sb.append(this.t.value());
            sb.append(" where ");
            if(ids.length != this.ids.size()) {
                throw new IllegalArgumentException("Por favor dê " + this.ids.size() + " argumentos");
            }
            List<ValueAndDbField> idparams = new ArrayList<>();
            for(int i = 0; i < this.ids.size(); i++) {
                PathToDbField pdbf = this.ids.get(i);
                sb.append(pdbf.dbf.getName());
                sb.append("=?");
                ValueAndDbField vdbf = new ValueAndDbField();
                vdbf.dbf = pdbf.dbf;
                vdbf.val = ids[i];
                idparams.add(vdbf);
                if(i != this.ids.size() - 1) {
                    sb.append(",");
                }
            }
            sb.append(";");
            PreparedStatement ps = this.db.getStatement(sb.toString());
            int i_query = 1;
            for(ValueAndDbField vdbf : idparams) {
                SQLType.addSimpleToPst(vdbf.dbf.getType(), vdbf.val, ps, i_query);
                i_query++;
            }
            ResultSet rs = ps.executeQuery();
            rs.next();
            return fromRs(rs);
	}
	
	public List<T> getList() throws SQLException {
            StringBuilder sb = new StringBuilder();
            sb.append("select * from ");
            sb.append(this.t.value());
            sb.append(";");
            PreparedStatement ps = this.db.getStatement(sb.toString());
            ResultSet rs = ps.executeQuery();
            List<T> ret = new ArrayList<>();
            while(rs.next()) {
                ret.add(fromRs(rs));
            }
            return ret;
	}
}


