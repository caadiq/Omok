import javax.swing.*;
import java.awt.*;
import java.io.IOException;


public class GameStart extends JFrame {
    private static final int frameWidth = 500;
    private static final int frameHeight = 120;

    private final Stream stream;
    private final Player player;

    private final JTextField textField;

    public GameStart() {
        stream = Stream.getInstance();
        player = Player.getInstance();

        setTitle("오목");
        setSize(frameWidth, frameHeight);
        setResizable(false);
        setLocationRelativeTo(null);
        setLayout(null);

        // 닉네임 텍스트
        JLabel label = new JLabel("닉네임");
        label.setBounds(20, 28, 100, 26);
        label.setFont(new Font("Dialog", Font.PLAIN, 26));
        add(label);

        // 닉네임 입력
        textField = new JTextField();
        textField.setBounds(120, 20, 240, 45);
        textField.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.GRAY, 1), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        textField.setFont(new Font("Dialog", Font.PLAIN, 22));
        add(textField);

        // 입장 버튼
        JButton button = new JButton("입장");
        button.setBounds(380, 20, 90, 45);
        button.setFont(new Font("Dialog", Font.PLAIN, 22));
        button.addActionListener(event -> enterServer());
        add(button);

        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    // 버튼 클릭 시
    private void enterServer() {
        String nickname = textField.getText().trim();

        if (nickname.isEmpty()) { // 닉네임을 입력하지 않으면
            JOptionPane.showMessageDialog(this, "닉네임을 입력해주세요.");
            return;
        }

        try {
            stream.sendMessage("Nickname|" + nickname); // 서버로 닉네임 전송
            String[] message = stream.receiveMessage();

            if (message[0].equals("Nickname") && message[1].equals("중복")) { // 닉네임 중복 시
                JOptionPane.showMessageDialog(this, "해당 닉네임의 유저가\n이미 접속 중입니다.", "", JOptionPane.INFORMATION_MESSAGE);
                stream.reconnect(); // 서버와 연결이 종료되었으므로 재접속
            } else if (message[0].equals("Room")) {
                String roomState = message[1];

                if (roomState.equals("입장가능")) {
                    player.setNickname(nickname);
                    stream.sendMessage("PlayerEnter|" + player.getNickname());
                    SwingUtilities.invokeLater(MainFrame::new);
                    this.dispose();
                } else if (roomState.equals("입장불가능")) {
                    JOptionPane.showMessageDialog(this, "방이 꽉찼습니다.", "", JOptionPane.INFORMATION_MESSAGE);
                    stream.reconnect(); // 서버와 연결이 종료되었으므로 재접속
                }
            }
        } catch (IOException e) {
            System.out.println("Error : " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(GameStart::new);
    }
}