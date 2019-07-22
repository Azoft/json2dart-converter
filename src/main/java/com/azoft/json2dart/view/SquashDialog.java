package com.azoft.json2dart.view;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;

import javax.swing.*;
import java.awt.*;

public class SquashDialog {
    private JButton enterButton;
    private JButton donTSquashButton;
    private JPanel rootView;

    private OnEnterListener onEnterListener;
    private OnDontSquashListener onDontSquashListener;

    public SquashDialog() {
        enterButton.addActionListener(e -> {
            if (onEnterListener != null) {
                onEnterListener.onEnter("", "");
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

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        rootView = new JPanel();
        rootView.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        enterButton = new JButton();
        enterButton.setText("Enter");
        rootView.add(enterButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 2, false));
        donTSquashButton = new JButton();
        donTSquashButton.setText("Don't squash");
        rootView.add(donTSquashButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return rootView;
    }

    public interface OnEnterListener {
        public void onEnter(String nameLeft, String nameRight);
    }

    public interface OnDontSquashListener {
        public void onDontSquash();
    }
}
