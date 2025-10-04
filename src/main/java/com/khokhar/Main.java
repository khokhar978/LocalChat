package com.khokhar;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.themes.FlatMacLightLaf;
import com.khokhar.ui.StartupWin;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        FlatLaf.setup(new FlatMacLightLaf());

        SwingUtilities.invokeLater(() -> {
            new StartupWin();
        });
    }
}