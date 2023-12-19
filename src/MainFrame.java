import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.SocketException;

public class MainFrame extends JFrame {
    private static final int frameWidth = 1360;
    private static final int frameHeight = 1000;

    private final Stream stream;
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
        GuiButton guiButton = new GuiButton();
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
                    if (state.getGameState())
                        stream.sendMessage("GameOver|PlayerExit");
                    else
                        stream.sendMessage("PlayerExit|");
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
                                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "게임시작! 당신은 " + player.getMyStone() + "입니다.", "", JOptionPane.INFORMATION_MESSAGE));
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
                        case "PlayerExit" -> guiButton.setbuttonReadyEnable();
                        case "GameOver" -> {
                            if (message[1].equals("PlayerExit")) {
                                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "상대방이 나가서\n게임이 종료됩니다.", "", JOptionPane.INFORMATION_MESSAGE));
                            } else {
                                String result = message[1].equals(player.getMyStone()) ? "승리!" : "패배...";
                                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, result, "", JOptionPane.INFORMATION_MESSAGE));
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
                        case "RequestReturn" -> {
                            if (!message[1].equals(player.getMyStone())) {
                                SwingUtilities.invokeLater(() -> {
                                    int result = JOptionPane.showConfirmDialog(this, "상대방이 무르기를 요청했습니다.\n수락하시겠습니까?", "무르기 요청", JOptionPane.YES_NO_OPTION);

                                    try {
                                        if (result == JOptionPane.YES_OPTION) {
                                            stream.sendMessage("AllowReturn|Yes," + message[1]);
                                        } else {
                                            stream.sendMessage("AllowReturn|No," + message[1]);
                                        }
                                    } catch (Exception e) {
                                        System.out.println("ERROR : " + e.getMessage());
                                    }
                                });
                            }
                        }
                        case "AllowReturn" -> {
                            String[] allowState = message[1].split(",");
                            if (allowState[0].equals("Yes") && allowState[1].equals(player.getMyStone())) {
                                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "상대방이 요청을 수락했습니다.\n내 턴입니다.", "무르기", JOptionPane.INFORMATION_MESSAGE));
                            } else if (allowState[0].equals("No") && allowState[1].equals(player.getMyStone())) {
                                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "상대방이 요청을 거절했습니다.", "무르기", JOptionPane.INFORMATION_MESSAGE));
                            }
                        }
                        case "PreviousTurnReturned" -> {
                            state.setPreviousTurnReturned(Boolean.parseBoolean(message[1]));
                            guiButton.setButtonBacksiesState();
                        }
                    }
                } catch (SocketException e) {
                    SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "서버 연결이 끊어졌습니다.", "", JOptionPane.ERROR_MESSAGE));
                    break;
                } catch (IOException e) {
                    System.out.println("ERROR : " + e.getMessage());
                    break;
                }
            }
        }).start();
    }
}