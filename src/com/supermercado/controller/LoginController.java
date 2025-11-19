package com.supermercado.controller;

import com.supermercado.dao.UsuarioDAO;
import com.supermercado.model.Usuario;
import java.sql.SQLException;

public class LoginController {

    private final UsuarioDAO usuarioDAO;
    private static Usuario usuarioLogado;

    public LoginController(UsuarioDAO usuarioDAO) {
        this.usuarioDAO = usuarioDAO;
    }

    public Usuario autenticar(String cpf, String nome) {
        if (cpf == null || cpf.trim().isEmpty() || nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("CPF e Nome não podem ser vazios.");
        }
        
        try {
            Usuario usuario = usuarioDAO.buscarPorCpf(cpf);
            if (usuario != null && usuario.getNome().equalsIgnoreCase(nome)) {
                usuarioLogado = usuario;
                return usuario;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao autenticar usuário: " + e.getMessage());
        }
        return null;
    }

    public void deslogar() {
        usuarioLogado = null;
    }
    
    public static Usuario getUsuarioLogado() {
        return usuarioLogado;
    }
}