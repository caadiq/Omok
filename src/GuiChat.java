import javax.swing.*;
import java.awt.*;

public class GuiChat extends JPanel {
    private static final int layoutWidth = 375;
    private static final int layoutHeight = 470;

    private final JTextArea textArea;

    public GuiChat() {
        setSize(layoutWidth, layoutHeight);
        setLayout(new BorderLayout());

        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setFocusable(false);
        textArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        textArea.setFont(new Font("Dialog", Font.PLAIN, 18));

        JTextField textField = new JTextField();
        textField.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.GRAY, 1), BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        textField.setFont(new Font("Dialog", Font.PLAIN, 18));
        textField.addActionListener(event -> {
            // TODO : 채팅
        });

        JScrollPane scrollPane = new JScrollPane(textArea);
        add(scrollPane, BorderLayout.CENTER);
        add(textField, BorderLayout.SOUTH);
    }
}
