import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class GuiChat extends JPanel {
    private static final int layoutWidth = 380;
    private static final int layoutHeight = 470;

    private final Stream stream;

    private final JTextArea textArea;

    public GuiChat() {
        stream = Stream.getInstance();

        setLayout(new BorderLayout());
        setSize(layoutWidth, layoutHeight);

        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setFocusable(false);
        textArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        textArea.setFont(new Font("Dialog", Font.PLAIN, 18));

        JTextField textField = new JTextField();
        textField.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.GRAY, 1), BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        textField.setFont(new Font("Dialog", Font.PLAIN, 18));
        textField.addActionListener(event -> {
            String strTextField = textField.getText();
            String nickname = Nickname.getInstance().getNickname();

            try {
                stream.sendMessage("Chat|" + nickname + " : " + strTextField);
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }

            textField.selectAll();
            textArea.setCaretPosition(textArea.getDocument().getLength());
            textField.setText(null);
        });

        JScrollPane scrollPane = new JScrollPane(textArea);
        add(scrollPane, BorderLayout.CENTER);
        add(textField, BorderLayout.SOUTH);
    }

    public void setMessage(String[] message) {
        SwingUtilities.invokeLater(() -> textArea.append(message[1] + "\n"));
    }
}