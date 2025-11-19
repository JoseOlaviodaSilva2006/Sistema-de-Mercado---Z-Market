package com.supermercado.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.io.File; // Importar File para usar File.separator

public class ConexaoDB {

    // Caminho absoluto para o arquivo do banco de dados dentro do diretório do projeto
    private static final String DB_FILE_PATH = System.getProperty("user.dir") + File.separator + "supermercado.db";
    private static final String URL = "jdbc:sqlite:" + DB_FILE_PATH;

    public static Connection getConnection() {
        try {
            Class.forName("org.sqlite.JDBC");
            return DriverManager.getConnection(URL);
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException("Erro ao conectar ao banco de dados: " + e.getMessage(), e);
        }
    }
}