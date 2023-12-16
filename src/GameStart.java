import javax.swing.*;
import java.awt.*;
import java.io.IOException;


public class GameStart extends JFrame {
    private static final int frameWidth = 500;
    private static final int frameHeight = 120;

    private final Stream stream;

    private final JTextField textField;

    public GameStart() {
        stream = Stream.getInstance();

        setTitle("오목");
        setSize(frameWidth, frameHeight);
        setResizable(false);
        setLocationRelativeTo(null);
        setLayout(null);

        JLabel label = new JLabel("닉네임");
        label.setBounds(20, 28, 100, 26);
        label.setFont(new Font("Dialog", Font.PLAIN, 26));
        add(label);

        textField = new JTextField();
        textField.setBounds(120, 20, 240, 45);
        textField.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.GRAY, 1), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        textField.setFont(new Font("Dialog", Font.PLAIN, 22));
        add(textField);

        JButton button = new JButton("입장");
        button.setBounds(380, 20, 90, 45);
        button.setFont(new Font("Dialog", Font.PLAIN, 22));
        button.addActionListener(e -> enterServer());
        add(button);

        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }

    private void enterServer() {
        String nickname = textField.getText().trim();

        if (nickname.isEmpty()) {
            JOptionPane.showMessageDialog(this, "닉네임을 입력해주세요.");
        } else {
            try {
                String[] message = stream.receiveMessage();
                if (message[0].equals("Room")) {
                    String roomState = message[1]; //서버로부터 접속가능 여부를 받아와서 접속이 가능한 아닌지 구분
                    if (roomState.equals("입장가능")) {
                        Player.getInstance().setNickname(nickname);
                        try {
                            stream.sendMessage("PlayerEnter|" + nickname);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                        SwingUtilities.invokeLater(MainFrame::new);
                        this.dispose();
                    } else if (roomState.equals("입장불가능")) {
                        JOptionPane.showMessageDialog(this, "방이 꽉찼습니다.", "", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(GameStart::new);
    }
}