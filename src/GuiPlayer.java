import javax.swing.*;
import java.awt.*;

public class GuiPlayer extends JPanel {
    private static final int layoutWidth = 375;
    private static final int layoutHeight = 380;

    public GuiPlayer() {
        setSize(layoutWidth, layoutHeight);
        setLayout(null);
        setBackground(Color.GRAY);
        
        // 임시 텍스트
        JLabel jLabel = new JLabel("플레이어 이미지 넣을곳");
        jLabel.setSize(380, 380);
        jLabel.setLocation(0, 0);
        jLabel.setFont(new Font("Dialog", Font.PLAIN, 30));
        add(jLabel);
    }
}
