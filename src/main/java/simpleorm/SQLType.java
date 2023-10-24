package simpleorm;
import java.util.Calendar;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
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
				System.out.println("Imprimindo coisa de NULL");
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
}
