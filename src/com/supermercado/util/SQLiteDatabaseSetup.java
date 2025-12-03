package com.supermercado.util;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLiteDatabaseSetup {

    public static void createTables() {
        String sqlUsuarios = "CREATE TABLE IF NOT EXISTS usuarios ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "nome TEXT NOT NULL,"
                + "cpf TEXT NOT NULL UNIQUE,"
                + "is_admin BOOLEAN NOT NULL"
                + ");";

        String sqlProdutos = "CREATE TABLE IF NOT EXISTS produtos ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "nome TEXT NOT NULL,"
                + "preco REAL NOT NULL,"
                + "quantidade_estoque INTEGER NOT NULL"
                + ");";

        // O "try-with-resources" garante que a conexão (conn) e o statement (stmt)
        // sejam fechados automaticamente no final, evitando vazamento de recursos.
        try (Connection conn = ConexaoDB.getConnection();
             Statement stmt = conn.createStatement()) {
            // Cria a tabela de usuários
            stmt.execute(sqlUsuarios);
            // Cria a tabela de produtos
            stmt.execute(sqlProdutos);
            
            System.out.println("Tabelas criadas com sucesso (se não existiam).");

        // # TRATAMENTO DE EXCEÇÃO (CAMADA DE DADOS / UTIL)
        // O método ConexaoDB.getConnection() já pode lançar uma RuntimeException se a conexão falhar.
        // Este 'catch' trata de erros que podem ocorrer durante a execução dos comandos SQL
        // pela interface Statement (stmt.execute()), como um comando SQL malformado.
        } catch (SQLException e) {
            // Assim como em ConexaoDB, a exceção específica (SQLException) é convertida
            // para uma RuntimeException genérica para simplificar o tratamento nas camadas superiores.
            throw new RuntimeException("Erro ao criar as tabelas: " + e.getMessage(), e);
        }
    }
}
