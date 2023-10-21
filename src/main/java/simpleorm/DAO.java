package simpleorm;
import java.lang.Annotation;
import java.lang.reflect.Field;
import java.util.List;
import java.util.HashMap;
import simpleorm.Table;
import simpleorm.DBConnection;
import simpleorm.DBField;
import simpleorm.SQLField;
import simpleorm.SQLType;

/**
 * Hello world!
 *
 */
public class DAO
{
	private Map<String,DBField> fields = new HashMap<>();
	private DBConnection db;
    public DAO(Class<?> cl, DBConnection db) {
		this.db = db;
		Table t = cl.getAnnotation(Table.class);
		if(t == null) {
			throw new IllegalArgumentException("Classe " + cl + " não é tabela!");
		}
		String t_name = t.getValue();
		for(Field p : cl.getFields()) {
			SQLField s_f = p.getAnnotation(SQLField.class);
			if(s_f == null) {
				continue;
			}
			DBField d_f = new DBField();
			d_f.setName(s_f.value());
			d_f.setType(SQLType.fromClass(p.getDeclaringClass()));
			fields.put(p.getName(), d_f);
		}
	}
}
