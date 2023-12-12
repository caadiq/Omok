import javax.swing.*;
import java.awt.*;

public class GuiPlayer extends JPanel {
    private static final int layoutWidth = 380;
    private static final int layoutHeight = 380;

    private static final int imageLength = 180;

    private final Player player;
    private final Turn turn;

    private final JLabel character1;
    private final JLabel character2;

    public GuiPlayer() {
        player = Player.getInstance();
        turn = Turn.getInstance();

        setSize(layoutWidth, layoutHeight);
        setLayout(null);

        character1 = new JLabel();
        character1.setSize(imageLength, imageLength);
        character1.setLocation(0, 0);
        character1.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        add(character1);

        JLabel jLabel1 = new JLabel("나");
        jLabel1.setSize(20, 20);
        jLabel1.setLocation(imageLength / 2 - 5, imageLength + 10);
        jLabel1.setFont(new Font("Dialog", Font.BOLD, 20));
        add(jLabel1);

        character2 = new JLabel();
        character2.setSize(imageLength, imageLength);
        character2.setLocation(imageLength + 15, 0);
        character2.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        add(character2);

        JLabel jLabel2 = new JLabel("상대");
        jLabel2.setSize(40, 20);
        jLabel2.setLocation((imageLength / 2 - 5) + imageLength, imageLength + 10);
        jLabel2.setFont(new Font("Dialog", Font.BOLD, 20));
        add(jLabel2);
    }

    private ImageIcon setImage(String image) {
        ImageIcon originalImage = new ImageIcon("images/" + image + ".png");
        Image scaledImage = originalImage.getImage().getScaledInstance(imageLength, imageLength, Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImage);
    }

    public void setCharacter() {
        character1.setIcon(setImage(player.getMyCharacter()));
        character2.setIcon(setImage(player.getOpponentCharacter()));
    }

    public void setBorder() {
        character1.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        character2.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        if (player.getMyStone().equals(turn.getTurn())) {
            character1.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
        } else {
            character2.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
        }
    }
}
