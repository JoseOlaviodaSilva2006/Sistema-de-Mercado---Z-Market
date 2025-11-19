package com.supermercado.dao;

import com.supermercado.model.Usuario;
import com.supermercado.util.ConexaoDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsuarioDAO {

    public void salvar(Usuario usuario) throws SQLException {
        // Primeiro, verifica se o CPF já existe
        if (buscarPorCpf(usuario.getCpf()) != null) {
            throw new SQLException("Já existe um usuário cadastrado com este CPF.");
        }

        String sql = "INSERT INTO usuarios (nome, cpf, is_admin) VALUES (?, ?, ?)";
        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, usuario.getNome());
            ps.setString(2, usuario.getCpf());
            ps.setBoolean(3, usuario.isAdmin());
            
            ps.executeUpdate();
        }
    }

    public Usuario buscarPorCpf(String cpf) throws SQLException {
        String sql = "SELECT * FROM usuarios WHERE cpf = ?";
        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, cpf);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String nome = rs.getString("nome");
                boolean isAdmin = rs.getBoolean("is_admin");
                return new Usuario(nome, cpf, isAdmin);
            }
        }
        return null;
    }
}