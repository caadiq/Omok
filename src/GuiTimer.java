import javax.swing.*;
import java.awt.*;

public class GuiTimer extends JPanel {
    private static final int layoutWidth = 375;
    private static final int layoutHeight = 30;

    private final JLabel timeLabel;
    private final JProgressBar progressBar;

    public GuiTimer() {
        setSize(layoutWidth, layoutHeight);
        setLayout(null);

        progressBar = new JProgressBar(0, 30);
        progressBar.setSize(300, 24);
        progressBar.setLocation(0, 2);
        add(progressBar);

        timeLabel = new JLabel("00:30");
        timeLabel.setSize(65, 24);
        timeLabel.setLocation(310, 0);
        timeLabel.setFont(new Font("Dialog", Font.BOLD, 24));
        add(timeLabel);
    }

    public void setTimer(String time) {
        int timeLeft = Integer.parseInt(time);
        progressBar.setValue(timeLeft);
        timeLabel.setText(String.format("00:%02d", timeLeft));
    }
}
