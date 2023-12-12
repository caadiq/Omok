import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

public class MainFrame extends JFrame {
    private static final int frameWidth = 1360;
    private static final int frameHeight = 1000;

    private final Stream stream;
    private final MyStone myStone;
    private final Turn turn;

    public MainFrame() {
        String nickname = Nickname.getInstance().getNickname();
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


        Thread shutdownHook = new Thread(() -> { //프로그램 종료시 상대방에게 메세지 보냄
            try {
                stream.sendMessage("Nicknameout|" + nickname);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            // 여기에 종료 시 수행하고자 하는 특정 연산을 추가
            System.out.println("프로그램 종료 혹은 다른 작업 수행");
        });

        Runtime.getRuntime().addShutdownHook(shutdownHook);

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
                        case "Nicknamein" -> guiChat.setUserEntered(message);
                        case "Nicknameout" -> guiChat.setUserOut(message);
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
        try {
            stream.sendMessage("Nicknamein|" + nickname);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}