
import org.fife.ui.autocomplete.*;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class CodeEditor {

    private static ArrayList<CodeEditor> codeEditors = new ArrayList<>();

    private final RSyntaxTextArea textArea;
    private final Plugin plugin;
    private final CodeEditor _this;

    private CodeEditor(String title, Plugin p) {
        plugin = p;

        JFrame jframe = new JFrame();
        JPanel contentPane = new JPanel(new BorderLayout());
        textArea = new RSyntaxTextArea(20, 60);
        textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
        textArea.setCodeFoldingEnabled(true);
        contentPane.add(new RTextScrollPane(textArea));
        CompletionProvider provider = createCompletionProvider();
        AutoCompletion ac = new AutoCompletion(provider);
        ac.setTriggerKey(KeyStroke.getKeyStroke("ctrl released ENTER"));
        ac.install(textArea);
        jframe.setContentPane(contentPane);
        jframe.setTitle(title);
        jframe.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        jframe.pack();
        jframe.setLocationRelativeTo(null);
        jframe.setAlwaysOnTop(true);
        jframe.setVisible(true);
        _this = this;

        jframe.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                codeEditors.remove(_this);
            }
        });

        textArea.addKeyListener(new KeyListener() {
            @Override public void keyTyped(java.awt.event.KeyEvent e) { }
            @Override public void keyPressed(java.awt.event.KeyEvent e) { }
            @Override public void keyReleased(java.awt.event.KeyEvent e) { plugin.code = textArea.getText(); }
        });

        textArea.setText(plugin.code);
    }

    private CompletionProvider createCompletionProvider() {

        DefaultCompletionProvider provider = new DefaultCompletionProvider();

        // todo - maybe RSTA LanguageSupport would do this for free ? ...
        provider.addCompletion(new BasicCompletion(provider, "abstract"));
        provider.addCompletion(new BasicCompletion(provider, "assert"));
        provider.addCompletion(new BasicCompletion(provider, "break"));
        provider.addCompletion(new BasicCompletion(provider, "case"));
        provider.addCompletion(new BasicCompletion(provider, "transient"));
        provider.addCompletion(new BasicCompletion(provider, "try"));
        provider.addCompletion(new BasicCompletion(provider, "void"));
        provider.addCompletion(new BasicCompletion(provider, "volatile"));
        provider.addCompletion(new BasicCompletion(provider, "while"));

        provider.addCompletion(new ShorthandCompletion(provider, "sysout", "System.out.println(", "System.out.println("));

        // add methods etc code completions here... 

        return provider;
    }

    public static void create(String title, Plugin p) {
        for(CodeEditor ce : codeEditors) if(ce.plugin == p) {
            System.out.println("Already editing this !");
            return;
        }
        codeEditors.add(new CodeEditor(title + p.id, p));
    }
}

class Plugin {
    String id;
    String code;

    // etc...
}
