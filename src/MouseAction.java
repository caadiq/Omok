import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.Objects;

public class MouseAction extends MouseAdapter {
    private final GameMethod gameMethod;
    private final GuiBoard guiBoard;
    private final Stream stream;
    private final Player player;
    private final State state;
    private final Turn turn;
    String currentPlayer;
    String OtherPlayer;
    public MouseAction(GameMethod gameMethod, GuiBoard guiBoard) {
        this.gameMethod = gameMethod;
        this.guiBoard = guiBoard;

        stream = Stream.getInstance();
        player = Player.getInstance();
        state = State.getInstance();
        turn = Turn.getInstance();
    }

    @Override
    public void mousePressed(MouseEvent mouseEvent) {
        String currentTurn = turn.getTurn();
        String myStoneColor = player.getMyStone();
        if(myStoneColor.equals("흰색")){
            OtherPlayer = "검은색";
        }
        else if(myStoneColor.equals("검은색")){
            OtherPlayer = "흰색";
        }
        currentPlayer = myStoneColor;
        int currentPlayerCount = state.getPlayerCount();
        boolean canGameStart = state.getGameState();

        // 내 턴이 아니거나 플레이어가 모두 준비되지 않았을 경우 클릭 방지
        if (currentTurn != null && !currentTurn.equals(myStoneColor) || currentPlayerCount != 2 || !canGameStart) {
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
            System.out.println("돌이 이미 놓여져 있습니다");
            return;
        }
        if(myStoneColor.equals("검은색")){
            if(samsam(x,y)){
                System.out.println("삼삼입니다. 돌을 놓을 수 없습니다.");
                return;
            }

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

    private boolean samsam(int x, int y) {
        int open_sam_count = 0;
        open_sam_count += find1(x,y);
        open_sam_count += find2(x,y);
        open_sam_count += find3(x,y);
        open_sam_count += find4(x,y);

        if (open_sam_count >= 2)
            return true;
        else
            return false;
    }

    private int find1(int x, int y) {
        String p = currentPlayer;
        String o = OtherPlayer;
        int stone1 = 0;
        int stone2 = 0;
        int allStone = 0;
        //열린 3인지 체크하기위한것..
        int blink1 = 1;

        //blink2 는 blink1 과 같음 중간에서넣어줄거임.
        //int blink2 = blink1;


        // ←
        int xx = x - 1; //달라지는 좌표
        boolean check = false;
        left:
        while (true) {

            //좌표끝도달
            if (xx == -1)
                break left;

            //check를 false로 바꿈으로 두번연속으로 만나는지 확인할수있게.
            if (Objects.equals(gameMethod.getStone(y, xx), p)) {
                check = false;
                stone1++;
            }

            //상대돌을 만나면 탐색중지
            if (Objects.equals(gameMethod.getStone(y, xx), o))
                break left;

            if (gameMethod.getStone(y,xx) == null) {
                //처음 빈공간을만나 check가 true가 됬는데
                //연달아 빈공간을만나면 탐색중지
                //두번연속으로 빈공간만날시 blink카운트를 되돌림.
                if (check == false) {
                    check = true;
                } else {
                    blink1++;
                    break left;
                }

                if (blink1 == 1) {
                    blink1--;
                } else {
                    break left; //빈공간을만났으나 빈공간을 두번만나면 끝임
                }
            }
            //계속탐색
            xx--;
        }


        // →
        xx = x + 1; //달라지는 좌표
        int blink2 = blink1; //blink1남은거만큼 blink2,
        if (blink1 == 1) //빈공간을 만나지않은경우 없었음을기록
            blink1 = 0;
        check = false;
        right:
        while (true) {
            //좌표끝도달
            if (xx == 19)
                break right;

            if (Objects.equals(gameMethod.getStone(y, xx), p)) {
                check = false;
                stone2++;
            }

            //상대돌을 만나면 탐색중지
            if (Objects.equals(gameMethod.getStone(y, xx), o))
                break right;

            if (gameMethod.getStone(y,xx) == null) {
                //두번연속으로 빈공간만날시 blink카운트를 되돌림.
                if (check == false) {
                    check = true;
                } else {
                    blink2++;
                    break right;
                }

                if (blink2 == 1) {
                    blink2--;
                } else {
                    break right; //빈공간을만났으나 빈공간을 두번만나면 끝임
                }
            }
            xx++;
        }

        allStone = stone1 + stone2;
        //삼삼이므로 돌갯수가 2 + 1(현재돌)이아니면 0리턴
        //이부분이 43을 허용하게해줌. 33만 찾게됨
        if (allStone != 2) {
            return 0;
        }
        //돌갯수가 3이면 열린 3인지 파악.

        int left = (stone1 + blink1);
        int right = (stone2 + blink2);

        //벽으로 막힌경우 - 열린3이 아님
        if (x - left == 0 || x + right == 18) {
            return 0;
        } else //상대돌로 막힌경우 - 열린3이 아님
            if (Objects.equals(gameMethod.getStone(y,x - left - 1),p) || Objects.equals(gameMethod.getStone(y,x + right + 1),o)) {
                return 0;
            } else {
                return 1; //열린3 일때 1 리턴
            }

    }

    // ↖ ↘ 탐색
    private int find2(int x, int y) {
        String p = currentPlayer;
        String o = OtherPlayer;
        int stone1 = 0;
        int stone2 = 0;
        int allStone = 0;
        int blink1 = 1;


        // ↖
        int xx = x - 1;
        int yy = y - 1;
        boolean check = false;
        leftUp:
        while (true) {
            if (xx == -1 || yy == -1)
                break leftUp;

            if (Objects.equals(gameMethod.getStone(yy, xx), p)) {
                check = false;
                stone1++;
            }

            if (Objects.equals(gameMethod.getStone(yy, xx), o))
                break leftUp;

            if (gameMethod.getStone(yy,xx) == null) {
                if (check == false) {
                    check = true;
                } else {
                    blink1++;
                    break leftUp;
                }

                if (blink1 == 1) {
                    blink1--;
                } else {
                    break leftUp;
                }
            }
            xx--;
            yy--;
        }


        // ↘
        int blink2 = blink1;
        if (blink1 == 1)
            blink1 = 0;
        xx = x + 1;
        yy = y + 1;
        check = false;
        rightDown:
        while (true) {
            if (xx == 19 || yy == 19)
                break rightDown;

            if (Objects.equals(gameMethod.getStone(yy, xx), p)) {
                check = false;
                stone2++;
            }

            if (Objects.equals(gameMethod.getStone(yy, xx), o))
                break rightDown;

            if (gameMethod.getStone(yy,xx) == null) {
                if (check == false) {
                    check = true;
                } else {
                    blink2++;
                    break rightDown;
                }

                if (blink2 == 1) {
                    blink2--;
                } else {
                    break rightDown;
                }
            }

            xx++;
            yy++;
        }

        allStone = stone1 + stone2;
        if (allStone != 2) {
            return 0;
        }

        int leftUp = (stone1 + blink1);
        int rightDown = (stone2 + blink2);

        if (y - leftUp == 0 || x - leftUp == 0 || y + rightDown == 18 || x + rightDown == 18) {
            return 0;
        } else if (Objects.equals(gameMethod.getStone(y - leftUp - 1,x - leftUp - 1),o) || Objects.equals(gameMethod.getStone(y + rightDown + 1,x + rightDown + 1),o)) {
            return 0;
        } else {
            return 1;
        }


    }

    // ↑ ↓ 탐색
    private int find3(int x, int y) {
        String p = currentPlayer;
        String o = OtherPlayer;
        int stone1 = 0;
        int stone2 = 0;
        int allStone = 0;
        int blink1 = 1;

        // ↑
        int yy = y - 1;
        boolean check = false;
        up:
        while (true) {
            if (yy == -1)
                break up;

            if (Objects.equals(gameMethod.getStone(yy, x), p)) {
                check = false;
                stone1++;
            }

            if (Objects.equals(gameMethod.getStone(yy, x), o))
                break up;

            if (gameMethod.getStone(yy,x) == null) {
                if (check == false) {
                    check = true;
                } else {
                    blink1++;
                    break up;
                }

                if (blink1 == 1) {
                    blink1--;
                } else {
                    break up;
                }
            }
            yy--;
        }

        // ↓
        int blink2 = blink1;
        if (blink1 == 1)
            blink1 = 0;
        yy = y + 1;
        check = false;
        down:
        while (true) {
            if (yy == 19)
                break down;

            if (Objects.equals(gameMethod.getStone(yy, x), p)) {
                check = false;
                stone2++;
            }

            if (Objects.equals(gameMethod.getStone(yy, x), o))
                break down;

            if (gameMethod.getStone(yy,x) == null) {
                if (check == false) {
                    check = true;
                } else {
                    blink2++;
                    break down;
                }

                if (blink2 == 1) {
                    blink2--;
                } else {
                    break down;
                }
            }

            yy++;
        }

        allStone = stone1 + stone2;
        if (allStone != 2) {
            return 0;
        }

        int up = (stone1 + blink1);
        int down = (stone2 + blink2);

        if (y - up == 0 || y + down == 18) {
            return 0;
        } else if (Objects.equals(gameMethod.getStone(y - up - 1,x),o) || Objects.equals(gameMethod.getStone(y + down + 1,x),o)) {
            return 0;
        } else {
            return 1;
        }
    }

    // ／ 탐색
    // ↙ ↗ 탐색
    private int find4(int x, int y) {
        String p = currentPlayer;
        String o = OtherPlayer;
        int stone1 = 0;
        int stone2 = 0;
        int allStone = 0;
        int blink1 = 1;

        // ↙
        int xx = x - 1;
        int yy = y + 1;
        boolean check = false;
        leftDown:
        while (true) {
            if (xx == -1 || yy == 19)
                break leftDown;

            if (Objects.equals(gameMethod.getStone(yy, xx), p)) {
                check = false;
                stone1++;
            }

            if (Objects.equals(gameMethod.getStone(yy, xx), o))
                break leftDown;

            if (gameMethod.getStone(yy,xx) == null) {
                if (check == false) {
                    check = true;
                } else {
                    blink1++;
                    break leftDown;
                }

                if (blink1 == 1) {
                    blink1--;
                } else {
                    break leftDown;
                }
            }
            xx--;
            yy++;
        }

        // ↗
        int blink2 = blink1;
        if (blink1 == 1)
            blink1 = 0;
        xx = x + 1;
        yy = y - 1;
        check = false;
        rightUp:
        while (true) {
            if (xx == 19 || yy == -1)
                break rightUp;

            if (Objects.equals(gameMethod.getStone(yy, xx), p)) {
                check = false;
                stone2++;
            }

            if (Objects.equals(gameMethod.getStone(yy, xx), o))
                break rightUp;

            if (gameMethod.getStone(yy,xx) == null) {
                if (check == false) {
                    check = true;
                } else {
                    blink2++;
                    break rightUp;
                }

                if (blink2 == 1) {
                    blink2--;
                } else {
                    break rightUp;
                }
            }
            xx++;
            yy--;
        }
        allStone = stone1 + stone2;
        if (allStone != 2) {

            return 0;
        }
        int leftDown = (stone1 + blink1);
        int rightUp = (stone2 + blink2);

        if (x - leftDown == 0 || y - rightUp == 0 || y + leftDown == 18 || x + rightUp == 18) {
            return 0;
        } else if (Objects.equals(gameMethod.getStone(y + leftDown + 1,x - leftDown - 1),o) || Objects.equals(gameMethod.getStone(y - rightUp - 1,x + rightUp + 1),o)) {
            return 0;
        } else {
            return 1;
        }

    }
}