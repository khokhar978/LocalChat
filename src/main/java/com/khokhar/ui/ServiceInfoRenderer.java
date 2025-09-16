package com.khokhar.ui;

import javax.jmdns.ServiceInfo;
import javax.swing.*;
import java.awt.*;

public class ServiceInfoRenderer extends JLabel implements ListCellRenderer<ServiceInfo> {

    public ServiceInfoRenderer() {
        // This is crucial for ensuring the background color is painted.
        setOpaque(true);
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends ServiceInfo> list,
                                                  ServiceInfo value,    // The ServiceInfo object for the current cell
                                                  int index,
                                                  boolean isSelected,   // True if the user has clicked on this cell
                                                  boolean cellHasFocus) {

        // Set the text of the JLabel to be just the service name
        if (value != null) {
            String txt=value.getName()+" | "+value.getHostAddress();
            setText(txt);
        } else {
            setText("");
        }
        setSize(100,40);

        // Handle the colors for when an item is selected
        if (isSelected) {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        } else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }

        return this;
    }
}