import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class GuiButton extends JPanel {
    private static final int layoutWidth = 375;
    private static final int layoutHeight = 70;

    private final Stream stream;
    private final MainFrame mainFrame;
    private final MyStone myStone;

    private final JButton buttonBacksies;
    private final JButton buttonReady;

    private int playerCount = 0;

    public GuiButton(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        stream = Stream.getInstance();
        myStone = MyStone.getInstance();

        setSize(layoutWidth, layoutHeight);
        setLayout(null);

        // 준비 버튼
        buttonReady = new JButton("준비");
        buttonReady.setSize(180, 70);
        buttonReady.setLocation(0, 0);
        buttonReady.setFont(new Font("Dialog", Font.BOLD, 22));
        buttonReady.addActionListener(e -> {
            if (playerCount != 2) {
                JOptionPane.showMessageDialog(null, "상대방이 아직 들어오지 않았습니다", "", JOptionPane.INFORMATION_MESSAGE);
            } else {
                try {
                    stream.sendMessage("Ready|" + "Ready");
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
//            TODO
//                - 버튼 클릭 시 준비 완료 상태로 변경 (버튼 비활성화 하기)
//                - 두 명 모두 준비 완료 시 게임 시작
                buttonReady.setEnabled(false);
            }
        });
        add(buttonReady);

        // 무르기 버튼
        buttonBacksies = new JButton("무르기");
        buttonBacksies.setSize(180, 70);
        buttonBacksies.setLocation(195, 0);
        buttonBacksies.setFont(new Font("Dialog", Font.BOLD, 22));
        buttonBacksies.setEnabled(false);
        buttonBacksies.addActionListener(e -> {
//            TODO
//                - 자기 턴에 버튼 활성화
//                - 버튼 클릭 시 한 번 무르기
//                - 무르기를 한 번 사용했다면 버튼 계속 비활성화 상태로 두기 (한 게임에 한 번만 사용 가능)
        });
        add(buttonBacksies);
    }

    public void setPlayerCount(String[] message) {
        playerCount = Integer.parseInt(message[1]);
    }

    public void printGameStart(String[] message) {
        if (message[1].equals("2")) {
            String color = myStone.getMyStone();
            String msg = "게임시작! 당신은 " + color + "입니다.";
            JOptionPane.showMessageDialog(mainFrame, msg, "", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
