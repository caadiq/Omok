import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class GuiChat extends JPanel {
    private static final int layoutWidth = 375;
    private static final int layoutHeight = 580;

    private final Stream stream;

    private final JTextArea textArea;

    public GuiChat() {
        stream = Stream.getInstance();

        setSize(layoutWidth, layoutHeight);
        setLayout(null);

        // 텍스트 출력 영역
        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setFocusable(false);
        textArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        textArea.setFont(new Font("Dialog", Font.PLAIN, 16));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);

        // textArea에 스크롤 추가
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setSize(375, 524);
        scrollPane.setLocation(0, 0);
        add(scrollPane);

        // 텍스트 입력 영역
        JTextField textField = new JTextField();
        textField.setSize(375, 50);
        textField.setLocation(0, 529);
        textField.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.GRAY, 1), BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        textField.setFont(new Font("Dialog", Font.PLAIN, 18));
        textField.addActionListener(event -> {
            String strTextField = textField.getText();
            String nickname = Player.getInstance().getNickname();

            try {
                stream.sendMessage("Chat|" + nickname + " : " + strTextField);
            } catch (IOException e) {
                System.out.println("Error : " + e.getMessage());
            }

            textField.selectAll();
            textArea.setCaretPosition(textArea.getDocument().getLength());
            textField.setText(null);
        });
        add(textField);
    }

    public void setMessage(String message) {
        SwingUtilities.invokeLater(() -> textArea.append(message + "\n"));
    }

    // 플레이어 접속 및 퇴장 메시지 출력
    public void setUserStateMessage(String message) {
        String[] messages = message.split(",");
        String nickname = messages[0];
        String state = messages[1];
        SwingUtilities.invokeLater(() -> textArea.append(nickname + "님이 " + state + "하셨습니다" + "\n"));
    }
}