package com.azoft.json2dart.view;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rsyntaxtextarea.Theme;

import java.io.IOException;

public class Utils {

    public static <T> RSyntaxTextArea buildRSyntaxTextArea(Class<T> tClass) {
        return buildRSyntaxTextArea(tClass, null, true);
    }

    public static <T> RSyntaxTextArea buildRSyntaxTextArea(Class<T> tClass, String text, boolean editable) {
        RSyntaxTextArea area = new RSyntaxTextArea(text);
        area.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JSON);
        area.setCodeFoldingEnabled(true);
        area.setEditable(editable);
        try {
            Theme theme = Theme.load(tClass.getResourceAsStream(
                "/org/fife/ui/rsyntaxtextarea/themes/monokai.xml"));
            theme.apply(area);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return area;
    }
}
