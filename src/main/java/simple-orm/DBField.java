package simple-orm;
import simple-orm.SQLType;

class DBField {
	private String name;
	private SQLType type;
	String getName() {
		return this.name;
	}
	void setName(String v) {
		this.name = v;
	}
	Type getType() {
		return this.type;
	}
	void setType(Type v) {
		this.type = v;
	}
}