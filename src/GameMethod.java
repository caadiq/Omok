public class GameMethod {
    private final int MaxSize = 19;
    private final String[][] Map = new String[MaxSize][MaxSize];

    // 오목판 초기화
    public void init() {
        for (int i = 0; i < MaxSize; i++) {
            for (int j = 0; j < MaxSize; j++) {
                Map[i][j] = null; // Map 배열을 모두 null로 초기화 → null = 아무것도 놓지 않은 상태
            }
        }
    }

    // 승리 조건
    public boolean endGame(Stone stone) {
        String nowTurn = Turn.getInstance().getTurn();
        int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}, {-1, 1}, {1, -1}, {-1, -1}, {1, 1}};

        for (int i = 0; i < 8; i += 2) {
            int stoneLine = 1;
            int stoneY = stone.getY();
            int stoneX = stone.getX();

            // 한 방향으로 연속된 돌 세기
            for (int j = 1; j < 5; j++) {
                int nextY = stoneY + directions[i][0];
                int nextX = stoneX + directions[i][1];
                if (nextY < 0 || nextY >= MaxSize || nextX < 0 || nextX >= MaxSize || Map[nextY][nextX] == null || !Map[nextY][nextX].equals(nowTurn)) {
                    break;
                }
                stoneLine++;
                stoneY = nextY;
                stoneX = nextX;
            }

            stoneY = stone.getY();
            stoneX = stone.getX();

            // 반대 방향으로 연속된 돌 세기
            for (int j = 1; j < 5; j++) {
                int nextY = stoneY + directions[i + 1][0];
                int nextX = stoneX + directions[i + 1][1];
                if (nextY < 0 || nextY >= MaxSize || nextX < 0 || nextX >= MaxSize || Map[nextY][nextX] == null || !Map[nextY][nextX].equals(nowTurn)) {
                    break;
                }
                stoneLine++;
                stoneY = nextY;
                stoneX = nextX;
            }

            // 돌이 정확히 5개 연속으로 놓여있는지 확인
            if (stoneLine == 5) {
                return true;
            }
        }
        return false;
    }

    // 돌 놓기
    public void putStone(Stone stone) {
        Map[stone.getY()][stone.getX()] = stone.getColor();
    }

    // 돌이 이미 놓여져 있는지 확인
    public boolean checkStone(int y, int x) {
        return Map[y][x] == null;
    }

    public String getStone(int y, int x) {
        return Map[y][x];
    }

    public String[][] getMap() {
        return Map;
    }
}