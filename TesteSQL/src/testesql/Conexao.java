package testesql;

import java.sql.*;

public class Conexao {

    private static final String URL =
        "jdbc:sqlserver://127.0.0.1:1433;databaseName=AulaJava;"
        + "integratedSecurity=true;encrypt=false;trustServerCertificate=true;";

    public static Connection getConexao() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    public static void main(String[] args) {
        try (Connection con = getConexao()) {
            System.out.println("Conexao OK");
            DatabaseMetaData db = con.getMetaData();
            System.out.println("Driver " + db.getDriverName());
            System.out.println("Usuario " + db.getUserName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}