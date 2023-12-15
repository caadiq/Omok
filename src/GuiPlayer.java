import javax.swing.*;
import java.awt.*;

public class GuiPlayer extends JPanel {
    private static final int layoutWidth = 375;
    private static final int layoutHeight = 220;

    private static final int imageLength = 180;

    private final Player player;
    private final Turn turn;

    private final JLabel myCharacter;
    private final JLabel opCharacter;

    public GuiPlayer() {
        player = Player.getInstance();
        turn = Turn.getInstance();

        setSize(layoutWidth, layoutHeight);
        setLayout(null);

        myCharacter = new JLabel();
        myCharacter.setSize(imageLength, imageLength);
        myCharacter.setLocation(0, 0);
        myCharacter.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        add(myCharacter);

        JLabel meLabel = new JLabel("나");
        meLabel.setSize(20, 20);
        meLabel.setLocation(imageLength / 2 - 5, imageLength + 10);
        meLabel.setFont(new Font("Dialog", Font.BOLD, 20));
        add(meLabel);

        opCharacter = new JLabel();
        opCharacter.setSize(imageLength, imageLength);
        opCharacter.setLocation(imageLength + 15, 0);
        opCharacter.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        add(opCharacter);

        JLabel opLabel = new JLabel("상대");
        opLabel.setSize(40, 20);
        opLabel.setLocation((imageLength / 2 - 5) + imageLength, imageLength + 10);
        opLabel.setFont(new Font("Dialog", Font.BOLD, 20));
        add(opLabel);
    }

    @Override
    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        String myStone = player.getMyStone();

        // 캐릭터 옆에 돌 그리기
        if (myStone != null) {
            String opStone = myStone.equals("검은색") ? "흰색" : "검은색";
            drawStone(graphics, 140, 140, myStone);
            drawStone(graphics, 340, 140, opStone);
        }
    }

    // 돌 그리기
    private void drawStone(Graphics graphics, int x, int y, String stoneColor) {
        int stoneSize = 28;
        Color color = stoneColor.equals("검은색") ? Color.BLACK : Color.WHITE;

        graphics.setColor(color);
        graphics.fillOval(x, y, stoneSize, stoneSize);

        if (color == Color.WHITE) {
            graphics.setColor(Color.BLACK);
            graphics.drawOval(x, y, stoneSize, stoneSize);
        }
    }

    private ImageIcon setImage(String image) {
        ImageIcon originalImage = new ImageIcon("images/" + image + ".png");
        Image scaledImage = originalImage.getImage().getScaledInstance(imageLength, imageLength, Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImage);
    }

    public void setCharacter() {
        myCharacter.setIcon(setImage(player.getMyCharacter()));
        opCharacter.setIcon(setImage(player.getOpponentCharacter()));
    }

    public void setBorder() {
        myCharacter.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        opCharacter.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        if (player.getMyStone().equals(turn.getTurn())) {
            myCharacter.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
        } else {
            opCharacter.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
        }
    }

    public void resetCharacter() {
        myCharacter.setIcon(null);
        myCharacter.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        opCharacter.setIcon(null);
        opCharacter.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

    }
}
