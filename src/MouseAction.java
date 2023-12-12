import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

public class MouseAction extends MouseAdapter {
    private final GameMethod gameMethod;
    private final GuiBoard guiBoard;

    private final Stream stream;
    private final MyStone myStone;
    private final Turn turn;

    public MouseAction(GameMethod gameMethod, GuiBoard guiBoard) {
        this.gameMethod = gameMethod;
        this.guiBoard = guiBoard;

        stream  = Stream.getInstance();
        myStone = MyStone.getInstance();
        turn = Turn.getInstance();
    }

    @Override
    public void mousePressed(MouseEvent mouseEvent) {
        String currentTurn = turn.getTurn();
        String myStoneColor = myStone.getMyStone();

        // 내 턴이 아니면 클릭 방지
        if (!currentTurn.equals(myStoneColor)) {
            return;
        }

        // 클릭 좌표값
        int x = (int) Math.round(mouseEvent.getX() / (double) 47 - 1);
        int y = (int) Math.round(mouseEvent.getY() / (double) 47 - 1);

        // 오목판 범위 벗어나면 클릭 방지
        if (x < 0 || x >= 19 || y < 0 || y >= 19) {
            return;
        }

        // 클릭한 위치에 돌이 이미 놓여져 있는지 확인
        if (!gameMethod.checkStone(y, x)) {
            return;
        }

        Stone stone = new Stone(y, x, myStoneColor);
        gameMethod.putStone(stone);
        guiBoard.repaint();

        try {
            stream.sendMessage("Turn|" + myStoneColor); // 내 턴임을 서버로 전송
            stream.sendMessage("StonePosition|" + y + "," + x + "," + myStoneColor); // 놓은 돌의 좌표와 색을 서버로 전송
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (gameMethod.endGame(stone)) {
            try {
                stream.sendMessage("Winner|" + currentTurn);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}