package simpleorm;
import java.sql.PreparedStatement;
import java.sql.SQLException;
public interface DBConnection {
	PreparedStatement getStatement(String sql) throws SQLException;
}
