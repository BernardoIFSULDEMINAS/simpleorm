package simpleorm;
import java.util.Calendar;
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
}
