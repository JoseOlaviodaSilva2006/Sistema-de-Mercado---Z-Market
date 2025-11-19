package com.supermercado.util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.HashMap;
import java.util.Map;

public class ComponentScaler {

    private final JFrame frame;
    private final Map<Component, Font> initialFonts = new HashMap<>();
    private Dimension initialSize;

    public ComponentScaler(JFrame frame) {
        this.frame = frame;
    }

    public void enableScaling() {
        // Use invokeLater to ensure we capture the state after the UI is fully initialized and packed.
        SwingUtilities.invokeLater(() -> {
            this.initialSize = frame.getSize();
            recordInitialState(frame);

            frame.addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(ComponentEvent e) {
                    rescale();
                }
            });
        });
    }

    private void recordInitialState(Container container) {
        for (Component comp : container.getComponents()) {
            initialFonts.put(comp, comp.getFont());
            if (comp instanceof Container) {
                // Recursively record state for sub-containers
                recordInitialState((Container) comp);
            }
        }
    }

    private void rescale() {
        if (initialSize == null || initialSize.width == 0 || initialSize.height == 0) {
            return;
        }

        // Calculate the scale factor, using the minimum of width/height to maintain aspect ratio
        double scaleFactor = Math.min(
            (double) frame.getWidth() / initialSize.width,
            (double) frame.getHeight() / initialSize.height
        );

        // Avoid rescaling for very minor changes to prevent jitter
        if (Math.abs(1.0 - scaleFactor) < 0.05) {
            return;
        }

        rescaleFonts(frame, scaleFactor);
    }

    private void rescaleFonts(Container container, double scaleFactor) {
        for (Component comp : container.getComponents()) {
            Font initialFont = initialFonts.get(comp);
            if (initialFont != null) {
                float newSize = (float) (initialFont.getSize() * scaleFactor);
                comp.setFont(initialFont.deriveFont(newSize));
            }
            if (comp instanceof Container) {
                // Recursively rescale fonts for sub-containers
                rescaleFonts((Container) comp, scaleFactor);
            }
        }
    }
}
