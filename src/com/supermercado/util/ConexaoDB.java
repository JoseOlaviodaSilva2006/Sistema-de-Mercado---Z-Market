package com.supermercado.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.io.File; // Importar File para usar File.separator

public class ConexaoDB {

    // Caminho absoluto para o arquivo do banco de dados dentro do diretório do projeto
    private static final String DB_FILE_PATH = System.getProperty("user.dir") + File.separator + "BancoDB" + File.separator + "supermercado.db";
    private static final String URL = "jdbc:sqlite:" + DB_FILE_PATH;

    public static Connection getConnection() {
        // # TRATAMENTO DE EXCEÇÃO (CAMADA DE DADOS / UTIL)
        // Este é o nível mais baixo de tratamento de exceção.
        try {
            // Tenta carregar o driver do banco de dados SQLite.
            Class.forName("org.sqlite.JDBC");
            // Tenta estabelecer a conexão com o banco.
            return DriverManager.getConnection(URL);

        // Captura os dois erros mais comuns nesta etapa:
        // 1. ClassNotFoundException: Ocorre se a biblioteca (o .jar) do SQLite não for encontrada.
        // 2. SQLException: Ocorre se houver um erro ao tentar se conectar (ex: caminho do arquivo inválido).
        } catch (ClassNotFoundException | SQLException e) {
            // Lança uma RuntimeException, "embrulhando" a exceção original.
            // Isso evita que todas as camadas superiores sejam forçadas a tratar essas exceções específicas,
            // centralizando o tratamento de erros críticos de conexão em um único ponto.
            throw new RuntimeException("Erro ao conectar ao banco de dados: " + e.getMessage(), e);
        }
    }
}