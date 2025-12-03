package com.supermercado.util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.HashMap;
import java.util.Map;

/**
 * # PARTE RESPONSIVA
 * Esta classe é o núcleo da responsividade da interface.
 * Sua função é redimensionar as fontes de todos os componentes de uma janela (JFrame)
 * de forma proporcional, sempre que a janela for redimensionada pelo usuário.
 */
public class ComponentScaler {

    private final JFrame frame;
    private final Map<Component, Font> initialFonts = new HashMap<>();
    private Dimension initialSize;

    public ComponentScaler(JFrame frame) {
        this.frame = frame;
    }

    /**
     * Ativa o dimensionamento responsivo para o JFrame.
     * Este método captura o estado inicial da UI (tamanho da tela e fontes dos componentes)
     * e adiciona um "ouvinte" que reage a eventos de redimensionamento da janela.
     */
    public void enableScaling() {
        // Usa invokeLater para garantir que o estado inicial seja capturado após a UI estar totalmente pronta.
        SwingUtilities.invokeLater(() -> {
            this.initialSize = frame.getSize();
            recordInitialState(frame);

            // Adiciona um listener que será acionado toda vez que a janela for redimensionada.
            frame.addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(ComponentEvent e) {
                    rescale();
                }
            });
        });
    }

    /**
     * Salva o estado inicial (a fonte original) de cada componente da tela.
     * Isso é feito de forma recursiva para todos os componentes dentro de containers.
     */
    private void recordInitialState(Container container) {
        for (Component comp : container.getComponents()) {
            initialFonts.put(comp, comp.getFont());
            if (comp instanceof Container) {
                // Chama a si mesmo para salvar as fontes dos componentes filhos.
                recordInitialState((Container) comp);
            }
        }
    }

    /**
     * Calcula o fator de escala e inicia o processo de redimensionamento das fontes.
     * O fator é baseado na mudança de tamanho da janela.
     */
    private void rescale() {
        if (initialSize == null || initialSize.width == 0 || initialSize.height == 0) {
            return;
        }

        // # CÁLCULO DA RESPONSIVIDADE
        // Calcula o fator de escala comparando o tamanho atual com o original.
        // Usa o menor valor (entre largura e altura) para manter a proporção e evitar que o texto estique.
        double scaleFactor = Math.min(
            (double) frame.getWidth() / initialSize.width,
            (double) frame.getHeight() / initialSize.height
        );

        // Evita redimensionamentos desnecessários para pequenas mudanças, o que poderia causar "tremor" na UI.
        if (Math.abs(1.0 - scaleFactor) < 0.05) {
            return;
        }

        rescaleFonts(frame, scaleFactor);
    }

    /**
     * Aplica as novas fontes redimensionadas a todos os componentes da tela.
     * Percorre cada componente, pega sua fonte original, e a redimensiona usando o fator de escala.
     */
    private void rescaleFonts(Container container, double scaleFactor) {
        for (Component comp : container.getComponents()) {
            Font initialFont = initialFonts.get(comp);
            if (initialFont != null) {
                // Calcula o novo tamanho da fonte e a aplica ao componente.
                float newSize = (float) (initialFont.getSize() * scaleFactor);
                comp.setFont(initialFont.deriveFont(newSize));
            }
            if (comp instanceof Container) {
                // Chama a si mesmo para redimensionar as fontes dos componentes filhos.
                rescaleFonts((Container) comp, scaleFactor);
            }
        }
    }
}
