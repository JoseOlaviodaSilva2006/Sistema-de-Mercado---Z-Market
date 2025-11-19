package com.supermercado.dao;

import com.supermercado.model.Produto;
import com.supermercado.util.ConexaoDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ProdutoDAO {

    public void salvar(Produto produto) throws SQLException {
        String sql = "INSERT INTO produtos (nome, preco, quantidade_estoque) VALUES (?, ?, ?)";
        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, produto.getNome());
            ps.setDouble(2, produto.getPreco());
            ps.setInt(3, produto.getQuantidadeEstoque());
            
            ps.executeUpdate();
        }
    }

    public void atualizar(Produto produto) throws SQLException {
        String sql = "UPDATE produtos SET nome = ?, preco = ?, quantidade_estoque = ? WHERE id = ?";
        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, produto.getNome());
            ps.setDouble(2, produto.getPreco());
            ps.setInt(3, produto.getQuantidadeEstoque());
            ps.setInt(4, produto.getId());
            
            ps.executeUpdate();
        }
    }

    public void remover(int id) throws SQLException {
        String sql = "DELETE FROM produtos WHERE id = ?";
        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            
            ps.executeUpdate();
        }
    }

    public List<Produto> listarTodos() throws SQLException {
        List<Produto> produtos = new ArrayList<>();
        String sql = "SELECT * FROM produtos ORDER BY nome";
        try (Connection conn = ConexaoDB.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                int id = rs.getInt("id");
                String nome = rs.getString("nome");
                double preco = rs.getDouble("preco");
                int quantidadeEstoque = rs.getInt("quantidade_estoque");
                produtos.add(new Produto(id, nome, preco, quantidadeEstoque));
            }
        }
        return produtos;
    }
}