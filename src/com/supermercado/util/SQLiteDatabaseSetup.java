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

        try (Connection conn = ConexaoDB.getConnection();
             Statement stmt = conn.createStatement()) {
            // Cria a tabela de usuários
            stmt.execute(sqlUsuarios);
            // Cria a tabela de produtos
            stmt.execute(sqlProdutos);
            
            System.out.println("Tabelas criadas com sucesso (se não existiam).");

        } catch (SQLException e) {
            // A exceção já é tratada em ConexaoDB, mas adicionamos um catch para o Statement
            throw new RuntimeException("Erro ao criar as tabelas: " + e.getMessage(), e);
        }
    }
}
