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
        int[][] dir = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}, {-1, 1}, {1, -1}, {-1, -1}, {1, 1}};

        for (int i = 0; i < 8; i += 2) {
            int stoneLine = 1;
            int stoneY = stone.getY();
            int stoneX = stone.getX();

            // 돌 기준 한쪽 방향으로 연속으로 놓여진 같은 색상의 돌 세기
            for (int j = 1; j < 5; j++) {
                stoneY += dir[i][0];
                stoneX += dir[i][1];
                if (stoneY < 0 || stoneY >= MaxSize || stoneX < 0 || stoneX >= MaxSize || !nowTurn.equals(Map[stoneY][stoneX])) {
                    break;
                }
                stoneLine++;
            }

            // 연속으로 놓여진 돌이 6개 이상인지 확인
            int nextY = stoneY + dir[i][0];
            int nextX = stoneX + dir[i][1];
            boolean isMoreThanFive = nextY >= 0 && nextY < MaxSize && nextX >= 0 && nextX < MaxSize && nowTurn.equals(Map[nextY][nextX]);

            stoneY = stone.getY();
            stoneX = stone.getX();

            // 돌 기준 반대쪽 방향으로 연속으로 놓여진 같은 색상의 돌 세기
            for (int j = 1; j < 5; j++) {
                stoneY += dir[i + 1][0];
                stoneX += dir[i + 1][1];

                if (stoneY < 0 || stoneY >= MaxSize || stoneX < 0 || stoneX >= MaxSize || !nowTurn.equals(Map[stoneY][stoneX])) {
                    break;
                }
                stoneLine++;
            }

            // 연속으로 놓여진 돌이 6개 이상인지 확인
            nextY = stoneY + dir[i + 1][0];
            nextX = stoneX + dir[i + 1][1];
            isMoreThanFive = isMoreThanFive || (nextY >= 0 && nextY < MaxSize && nextX >= 0 && nextX < MaxSize && nowTurn.equals(Map[nextY][nextX]));

            // 돌을 5개 연속으로 놓고, 6개 이상이 아니라면 승리
            if (stoneLine == 5 && !isMoreThanFive) {
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

    public String getStone(int y,int x){return Map[y][x];}

    public String[][] getMap() {
        return Map;
    }
}