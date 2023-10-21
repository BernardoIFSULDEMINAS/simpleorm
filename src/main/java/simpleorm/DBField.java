package simpleorm;
import simpleorm.SQLType;

class DBField {
	private String name;
	private SQLType type;
	private Class<?> relatedTo;
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
}
