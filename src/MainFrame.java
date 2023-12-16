import com.sun.tools.javac.Main;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.SocketException;

public class MainFrame extends JFrame {
    private static final int frameWidth = 1360;
    private static final int frameHeight = 1000;

    private Stream stream;
    private final Player player;
    private final State state;
    private final Turn turn;
    private final GameMethod gameMethod;

    public MainFrame() {
        stream = Stream.getInstance();
        player = Player.getInstance();
        state = State.getInstance();
        turn = Turn.getInstance();
        gameMethod = new GameMethod();

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
        GuiBoard guiBoard = new GuiBoard(gameMethod);
        guiBoard.setLocation(10, 10);
        add(guiBoard);

        // 플레이어
        GuiPlayer guiPlayer = new GuiPlayer();
        guiPlayer.setLocation(960, 10);
        add(guiPlayer);

        // 타이머
        GuiTimer guiTimer = new GuiTimer();
        guiTimer.setLocation(960, 240);
        add(guiTimer);

        // 채팅
        GuiChat guiChat = new GuiChat();
        guiChat.setLocation(960, 290);
        add(guiChat);

        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                super.windowClosing(event);
                try {
                    stream.sendMessage("GameOver|PlayerExit");
                } catch (IOException e) {
                    System.out.println("ERROR : " + e.getMessage());
                }
                stream.close();
            }
        });

        new Thread(() -> {
            while (true) {
                try {
                    String[] message = stream.receiveMessage();
                    switch (message[0]) {
                        case "PlayerCount" -> state.setPlayerCount(Integer.parseInt(message[1]));
                        case "State" -> {
                            if (message[1].equals("Start")) {
                                state.setGameState(true);
                                guiButton.printGameStart();
                            }
                        }
                        case "StoneColor" -> player.setMyStone(message[1]);
                        case "Turn" -> {
                            turn.setTurn(message[1]);
                            guiButton.setButtonBacksiesState();
                            guiPlayer.setBorder();
                        }
                        case "StonePosition" -> guiBoard.setStone(message[1]);
                        case "Character" -> {
                            String[] character = message[1].split(",");
                            player.setMyCharacter(character[0]);
                            player.setOpponentCharacter(character[1]);
                            guiPlayer.setCharacter();
                        }
                        case "Timer" -> guiTimer.setTimer(message[1]);
                        case "Chat" -> guiChat.setMessage(message[1]);
                        case "Nickname" -> guiChat.setUserStateMessage(message[1]);
                        case "GameOver" -> {
                            if (message[1].equals("PlayerExit")) {
                                JOptionPane.showMessageDialog(this, "상대방이 나가서\n게임이 종료됩니다.", "", JOptionPane.INFORMATION_MESSAGE);
                            } else {
                                String result = message[1].equals(player.getMyStone()) ? "승리!" : "패배...";
                                JOptionPane.showMessageDialog(this, result, "", JOptionPane.INFORMATION_MESSAGE);
                            }

                            gameMethod.init();
                            guiBoard.repaint();
                            guiPlayer.resetCharacter();
                            guiTimer.setTimer("30");
                            guiButton.setbuttonReadyEnable();
                            guiButton.setButtonBacksiesDisable();
                            state.setGameState(false);
                            player.setMyStone(null);
                        }
                        case "Return" -> guiBoard.returnStone(message[1]);
                        case "CanReturn" -> {
                            String[] canReturn = message[1].split(",");
                            if (player.getMyStone().equals(canReturn[0])) {
                                player.setCanReturn(Boolean.parseBoolean(canReturn[1]));
                            }
                        }
                        case "PreviousTurnReturned" -> {
                            state.setPreviousTurnReturned(Boolean.parseBoolean(message[1]));
                            guiButton.setButtonBacksiesState();
                        }
                    }
                } catch (SocketException e) {
                    JOptionPane.showMessageDialog(this, "서버 연결이 끊어졌습니다.", "", JOptionPane.ERROR_MESSAGE);
                    stream.reconnect();
                    break;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}