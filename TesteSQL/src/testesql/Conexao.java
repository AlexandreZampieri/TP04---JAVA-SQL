package testesql;

import java.sql.*;

public class Conexao {

    private static final String URL =
        "jdbc:sqlserver://127.0.0.1;databaseName=AulaJava;integratedSecurity=true;encrypt=true;trustServerCertificate=true;";

    // Método reutilizável: devolve uma conexão aberta
    public static Connection getConexao() throws SQLException, ClassNotFoundException {
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        return DriverManager.getConnection(URL);
    }

    // Seu teste de conexão continua funcionando
    public static void main(String args[]) {
        try {
            Connection con = getConexao();
            System.out.println("Conexao OK");

            // dados da conexao
            DatabaseMetaData db = con.getMetaData();
            System.out.println("Conectado a " + db.getURL());
            System.out.println("Conexao " + db.getConnection());
            System.out.println("Driver " + db.getDriverName());
            System.out.println("Usuario " + db.getUserName());
            System.out.println("----------------------------------");

            con.close();
        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }
}