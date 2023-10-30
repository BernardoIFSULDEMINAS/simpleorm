package simpleorm;
import java.util.Calendar;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.GregorianCalendar;
enum SQLType {
	VarChar, Int, DateTime, Float;
	static SQLType fromClass(Class<?> classe) {
		if(classe.equals(String.class)) {
			return VarChar;
		} else if (classe.equals(Integer.class)) {
			return Int;
		} else if (Calendar.class.isAssignableFrom(classe)) {
			return DateTime;
		} else if (classe.equals(Double.class)) {
			return Float;
		} else {
			return null;
		}
	}
	static void addSimpleToPst(SQLType type, Object value, PreparedStatement pst, int i) throws SQLException {
		try {
			if(value == null) {
				pst.setNull(i, Types.NULL);
			}
			switch(type) {
				case VarChar:
					pst.setString(i, (String)value);
					break;
				case Int:
					pst.setInt(i, (Integer)value);
					break;
				case DateTime:
					if(Calendar.class.isAssignableFrom(value.getClass())) {
						java.util.Date d = ((Calendar)value).getTime();
						pst.setDate(i+1, new java.sql.Date(d.getTime()));
					} else {
						throw new IllegalArgumentException("Classe " + value.getClass() + " não bate com tipo " + type);
					}
					break;
				case Float:
					pst.setDouble(i, (Double)value);
					break;
			}
		} catch(ClassCastException e) {
			throw new IllegalArgumentException("Classe " + value.getClass() + " não bate com tipo " + type);
		}
	}
        
        static Object fromSimpleToPst(DBField dbf, ResultSet rs, Class<?> to) throws SQLException {
            String n = dbf.getName();
            switch(dbf.getType()) {
                case VarChar:
                    return rs.getString(n);
                case Int:
                    return rs.getInt(n);
                case DateTime:
                    java.util.Date d = rs.getDate(n);
                    if(to.isAssignableFrom(Calendar.class)) {
                        Calendar c = new GregorianCalendar();
                        c.setTime(d);
                        return c;
                    } else {
                        throw new IllegalArgumentException("Classe " + to + " não bate com tipo " + dbf.getType());
                    }
                case Float:
                    return rs.getDouble(n);
                default:
                    throw new IllegalArgumentException("Não sei processar " + dbf.getType());
            }
        }
}
