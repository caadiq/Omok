import javax.swing.*;

public class MainFrame extends JFrame {
    private static final int frameWidth = 1360;
    private static final int frameHeight = 1000;

    private final Stream stream;
    private final MyStone myStone;
    private final Turn turn;

    public MainFrame() {
        stream = Stream.getInstance();
        myStone = MyStone.getInstance();
        turn = Turn.getInstance();

        setTitle("오목");
        setSize(frameWidth, frameHeight);
        setResizable(false); // 창 크기 변경 방지
        setLocationRelativeTo(null); // 창 가운데 표시
        setLayout(null); // 레이아웃 맘대로 설정

        // 버튼
        GuiButton guiButton = new GuiButton(this);
        guiButton.setLocation(960, 880);
        add(guiButton);

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

        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        new Thread(() -> {
            while (true) {
                try {
                    String[] message = stream.receiveMessage();
                    switch (message[0]) {
                        case "StonePosition" -> guiBoard.setStone(message);
                        case "PlayerCount" -> guiButton.setPlayerCount(message);
                        case "Ready" -> guiButton.printGameStart(message);
                        case "StoneColor" -> myStone.setMyStone(message[1]);
                        case "Chat" -> guiChat.setMessage(message);
                        case "Turn" -> turn.setTurn(message[1]);
                        case "Winner" -> {
                            String result;
                            if (message[1].equals(myStone.getMyStone()))
                                result = "승리!";
                            else
                                result = "패배...";
                            JOptionPane.showMessageDialog(this, result, "", JOptionPane.INFORMATION_MESSAGE);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }
            }
        }).start();
    }
}