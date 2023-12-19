import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class GuiButton extends JPanel {
    private static final int layoutWidth = 375;
    private static final int layoutHeight = 70;

    private final Stream stream;
    private final Player player;
    private final State state;
    private final Turn turn;

    private final JButton buttonBacksies;
    private final JButton buttonReady;

    public GuiButton() {
        stream = Stream.getInstance();
        player = Player.getInstance();
        state = State.getInstance();
        turn = Turn.getInstance();

        setSize(layoutWidth, layoutHeight);
        setLayout(null);

        // 준비 버튼
        buttonReady = new JButton("준비");
        buttonReady.setSize(180, 70);
        buttonReady.setLocation(0, 0);
        buttonReady.setFont(new Font("Dialog", Font.BOLD, 22));
        buttonReady.addActionListener(event -> {
            if (state.getPlayerCount() != 2) { //클릭시 서버에 접속한 사람이 2명이 아니라면, 메세지를 띄움
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(null, "상대방이 아직 들어오지 않았습니다", "", JOptionPane.INFORMATION_MESSAGE));
            } else {
                try {
                    stream.sendMessage("State|Ready");
                } catch (IOException e) {
                    System.out.println("Error : " + e.getMessage());
                }
                buttonReady.setEnabled(false);
            }
        });
        add(buttonReady);

        // 무르기 버튼
        buttonBacksies = new JButton("무르기 요청");
        buttonBacksies.setSize(180, 70);
        buttonBacksies.setLocation(195, 0);
        buttonBacksies.setFont(new Font("Dialog", Font.BOLD, 22));
        buttonBacksies.setEnabled(false);
        buttonBacksies.addActionListener(event -> {
            try {
                stream.sendMessage("RequestReturn|" + player.getMyStone());
            } catch (IOException e) {
                System.out.println("Error : " + e.getMessage());
            }
        });
        add(buttonBacksies);
    }

    public void setbuttonReadyEnable() {
        buttonReady.setEnabled(true);
    }

    public void setButtonBacksiesDisable() {
        buttonBacksies.setEnabled(false);
    }

    public void setButtonBacksiesState() {
        if (state.getGameState()) {
            buttonBacksies.setEnabled(!player.getMyStone().equals(turn.getTurn()) && !state.getPreviousTurnReturned());
        }
    }
}