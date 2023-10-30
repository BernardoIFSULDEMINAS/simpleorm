package simpleorm;
import java.lang.reflect.Field;

class DBField {
	private String name;
	private SQLType type;
        private Class<?> javaClass;
	String getName() {
		return this.name;
	}
	void setName(String v) {
		this.name = v;
	}
	SQLType getType() {
		return this.type;
	}
	void setType(SQLType v) {
		this.type = v;
	}
        public Class<?> getJavaClass() {
            return javaClass;
        }
        public void setJavaClass(Class<?> javaClass) {
            this.javaClass = javaClass;
        }
	static DBField fromSimpleField(Field p) {
		SQLField s_f = p.getAnnotation(SQLField.class);
		if(s_f == null) {
			return null;
		}
		SQLType s_type = SQLType.fromClass(p.getType());
		if(s_type != null) {
			DBField d_f = new DBField();
			d_f.setName(s_f.value());
			d_f.setType(s_type);
                        d_f.setJavaClass(p.getType());
			return d_f;
		} else {
			return null;
		}
	}
}
