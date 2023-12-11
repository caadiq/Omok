import javax.swing.*;
import java.awt.*;

public class GuiBoard extends JPanel {
    private static final int layoutWidth = 940;
    private static final int layoutHeight = 940;

    GameMethod gm = new GameMethod();

    private final int CELL = 47; // 선 간격
    private final int SIZE = 19; // 가로, 세로 선 개수
    private final int STONE_SIZE = 28;

    public GuiBoard(MainFrame m) {
        setSize(layoutWidth, layoutHeight);
        setLayout(null);
        setBackground(new Color(206, 167, 61));
        MouseAction Mc = new MouseAction(gm,this, m);
        addMouseListener(Mc);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.BLACK);
        drawBoard(g);
        drawStone(g);
    }

    // 오목판 그리기
    private void drawBoard(Graphics g) {
        // 선 그리기
        for(int i = 1; i <= SIZE; i++) {
            g.drawLine(CELL, i * CELL, CELL * SIZE, i * CELL);
            g.drawLine(i * CELL, CELL, i * CELL, CELL * SIZE);
        }

        // 숫자 및 알파벳 그리기
        g.setFont(new Font("Dialog", Font.BOLD, 15));
        for (int i = 1; i <= SIZE; i++) {
            g.drawString(Integer.toString(i), i * CELL - CELL / 2 + 17, CELL / 2 + 10);
            g.drawString(String.valueOf((char) ('a' + i - 1)), CELL / 4 + 10, i * CELL + CELL / 4 - 5);
        }

        // 해당 좌표에 작은 점 그리기
        drawDot(g, 'd', 4);
        drawDot(g, 'd', 16);
        drawDot(g, 'p', 4);
        drawDot(g, 'p', 16);
        drawDot(g, 'j', 10);
    }

    // 선 위에 작은 점 그리기
    private void drawDot(Graphics g, char row, int column) {
        int dotSize = 10;
        int rowNum = row - 'a' + 1; // 알파벳 → 숫자
        
        int x = column * CELL - (dotSize / 2);
        int y = rowNum * CELL - (dotSize / 2);

        g.fillOval(x, y, dotSize, dotSize);
    }

    private void drawStone(Graphics g) {
        for(int y=0;y<SIZE;y++){
            for(int x=0;x<SIZE;x++){
                if(gm.getMap()[y][x]==1)
                    drawBlackStone(g,x,y);
                else if(gm.getMap()[y][x]==2)
                    drawWhiteStone(g, x, y);
            }
        }
    }

    private void drawBlackStone(Graphics g, int x, int y) {
        g.setColor(Color.BLACK);
        g.fillOval(x*CELL+32, y*CELL+79, STONE_SIZE, STONE_SIZE);
    }

    private void drawWhiteStone(Graphics g, int x, int y) {
        g.setColor(Color.WHITE);
        g.fillOval(x*CELL+32, y*CELL+79, STONE_SIZE, STONE_SIZE);
    }
}
