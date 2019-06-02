package com.azoft.json2dart.view;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SquashDialog {
    private JButton enterButton;
    private JButton donTSquashButton;
    private JPanel rootView;

    private OnEnterListener onEnterListener;
    private OnDontSquashListener onDontSquashListener;

    public SquashDialog() {
        enterButton.addActionListener(e -> {
            if (onEnterListener != null) {
                onEnterListener.onEnter("");
            }
        });
        donTSquashButton.addActionListener(e -> {
            if (onDontSquashListener != null) {
                onDontSquashListener.onDontSquash();
            }
        });
    }

    public JPanel getRootView() {
        return rootView;
    }

    public void setOnEnterListener(OnEnterListener onEnterListener) {
        this.onEnterListener = onEnterListener;
    }

    public void setOnDontSquashListener(OnDontSquashListener onDontSquashListener) {
        this.onDontSquashListener = onDontSquashListener;
    }

    public interface OnEnterListener {
        public void onEnter(String name);
    }

    public interface OnDontSquashListener {
        public void onDontSquash();
    }
}
