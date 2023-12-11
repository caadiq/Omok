import javax.swing.*;

class MainFrame extends JFrame {
    private static final int frameWidth = 1360;
    private static final int frameHeight = 1000;

    private final Stream stream = Stream.getInstance();

    public MainFrame() {
        setTitle("오목");
        setSize(frameWidth, frameHeight);
        setResizable(false); // 창 크기 변경 방지
        setLocationRelativeTo(null); // 창 가운데 표시
        setLayout(null); // 레이아웃 맘대로 설정

        // 오목판
        GuiBoard guiBoard = new GuiBoard();
        guiBoard.setLocation(10, 10);
        add(guiBoard);

        // 플레이어
        GuiPlayer guiPlayer = new GuiPlayer();
        guiPlayer.setLocation(960, 10);
        add(guiPlayer);

        // 채팅
        GuiChat guiChat = new GuiChat();
        guiChat.setLocation(960, 400);
        add(guiChat);

        // 버튼
        GuiButton guiButton = new GuiButton();
        guiButton.setLocation(960, 880);
        add(guiButton);

        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        new Thread(() -> {
            while (true) {
                try {
                    String[] message = stream.receiveMessage();
                    switch (message[0]) {
                        case "Chat" -> guiChat.setMessage(message);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }
            }
        }).start();
    }
}

public class Main {
    public static void main(String[] args) {
        new MainFrame();
    }
}
