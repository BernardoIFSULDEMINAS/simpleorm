package simpleorm;

import java.sql.*;

public class ConectorMySql implements DBConnection {
    private static final String banco = "jdbc:mysql://localhost:3306/databaseExemplo";
    private static final String driver = "com.mysql.jdbc.Driver";
    private static final String usuario = "root";
    private static final String senha = "";
    private static Connection con = null;

    public ConectorMySql() {
        if (con == null) {
            try {
                Class.forName(driver);
                con = DriverManager.getConnection(banco, usuario, senha);
            } catch (ClassNotFoundException e) {
                System.out.println("Não encontrou o driver: " + e.getMessage());
            } catch(SQLException e) {
                System.out.println("Erro de conexão: "+e.getMessage());
            }
        }
    }
    public PreparedStatement getStatement(String sql) throws SQLException {
        System.out.println("Rodando sql: " + sql);
        return con.prepareStatement(sql);
    }
}
