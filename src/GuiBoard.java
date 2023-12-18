import javax.swing.*;
import java.awt.*;

public class GuiBoard extends JPanel {
    private static final int layoutWidth = 940;
    private static final int layoutHeight = 940;

    private final int CELL = 47; // 선 간격
    private final int SIZE = 19; // 가로, 세로 선 개수
    private final int STONE_SIZE = 34; // 돌 크기

    private final GameMethod gameMethod;
    private final Player player;

    public GuiBoard(GameMethod gameMethod) {
        this.gameMethod = gameMethod;
        player = Player.getInstance();

        setSize(layoutWidth, layoutHeight);
        setLayout(null);
        setBackground(new Color(206, 167, 61));

        MouseAction mouseAction = new MouseAction(gameMethod, this);
        addMouseListener(mouseAction);
    }

    @Override
    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        graphics.setColor(Color.BLACK);
        drawBoard(graphics);
        drawStone(graphics);
    }

    // 오목판 그리기
    private void drawBoard(Graphics graphics) {
        // 선 그리기
        for (int i = 1; i <= SIZE; i++) {
            graphics.drawLine(CELL, i * CELL, CELL * SIZE, i * CELL);
            graphics.drawLine(i * CELL, CELL, i * CELL, CELL * SIZE);
        }

        // 숫자 및 알파벳 그리기
        graphics.setFont(new Font("Dialog", Font.BOLD, 15));
        for (int i = 1; i <= SIZE; i++) {
            graphics.drawString(Integer.toString(i), i * CELL - CELL / 2 + 17, CELL / 2 + 10);
            graphics.drawString(String.valueOf((char) ('a' + i - 1)), CELL / 4 + 10, i * CELL + CELL / 4 - 5);
        }

        // 해당 좌표에 작은 점 그리기
        drawDot(graphics, 'd', 4);
        drawDot(graphics, 'd', 10);
        drawDot(graphics, 'd', 16);
        drawDot(graphics, 'j', 4);
        drawDot(graphics, 'j', 10);
        drawDot(graphics, 'j', 16);
        drawDot(graphics, 'p', 4);
        drawDot(graphics, 'p', 10);
        drawDot(graphics, 'p', 16);
    }

    // 선 위에 작은 점 그리기
    private void drawDot(Graphics graphics, char row, int column) {
        int dotSize = 10;
        int rowNum = row - 'a' + 1; // 알파벳 → 숫자

        int x = column * CELL - (dotSize / 2);
        int y = rowNum * CELL - (dotSize / 2);

        graphics.fillOval(x, y, dotSize, dotSize);
    }

    // 돌 그리기
    private void drawStone(Graphics graphics) {
        for (int y = 0; y < SIZE; y++) {
            for (int x = 0; x < SIZE; x++) {
                if (gameMethod.getMap()[y][x] != null) {
                    if (gameMethod.getMap()[y][x].equals("검은색")) // 해당 좌표의 값이 검은색이면 검은돌 놓기
                        drawBlackStone(graphics, x, y);
                    else if (gameMethod.getMap()[y][x].equals("흰색")) // 해당 좌표의 값이 흰색이면 흰돌 놓기
                        drawWhiteStone(graphics, x, y);
                }
            }
        }
    }

    // 검은돌
    private void drawBlackStone(Graphics graphics, int x, int y) {
        graphics.setColor(Color.BLACK);
        graphics.fillOval(x * CELL + 30, y * CELL + 30, STONE_SIZE, STONE_SIZE);
    }

    // 흰돌
    private void drawWhiteStone(Graphics graphics, int x, int y) {
        graphics.setColor(Color.WHITE);
        graphics.fillOval(x * CELL + 30, y * CELL + 30, STONE_SIZE, STONE_SIZE);
    }

    // 돌 놓기
    private void putStone(int y, int x, String stoneColor) {
        gameMethod.putStone(new Stone(y, x, stoneColor));
        repaint();
    }

    // 돌 상태(좌표, 색상) 가져오기
    public void setStone(String message) {
        String[] stonePosition = message.split(",");

        int y = Integer.parseInt(stonePosition[0]);
        int x = Integer.parseInt(stonePosition[1]);
        String stoneColor = stonePosition[2];

        putStone(y, x, stoneColor);
    }

    // 무르기 돌 상태 가져오기
    public void returnStone(String message) {
        String[] stonePosition = message.split(",");

        int y = Integer.parseInt(stonePosition[0]);
        int x = Integer.parseInt(stonePosition[1]);
        String turn = stonePosition[2];

        putStone(y, x, null);

        if (!player.getMyStone().equals(turn))
            SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "상대방이 무르기를 사용했습니다.\n상대방에게 턴이 넘어갑니다.", "", JOptionPane.INFORMATION_MESSAGE));
        else
            SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "무르기를 사용했습니다.\n남은 무르기 횟수는 0입니다.", "", JOptionPane.INFORMATION_MESSAGE));
    }
}