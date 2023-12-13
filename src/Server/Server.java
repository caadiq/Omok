package Server;

import java.awt.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.List;

public class Server {
    private static final int SERVER_PORT = 10000;
    private static final int USER_LIMIT = 2;

    private ServerSocket serverSocket;
    private Socket clientSocket;

    private final Vector<UserService> userVector = new Vector<>();

    private int readyCount = 0;
    private String currentTurn = "검은색";

    private Timer timer;
    private final int timeLimit = 30;

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                new Server();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public Server() {
        try {
            serverSocket = new ServerSocket(SERVER_PORT);

        } catch (IOException e) {
            e.printStackTrace();
        }
        AcceptServer acceptServer = new AcceptServer();
        acceptServer.start();
    }

    // 모든 클라이언트에게 메시지 전송
    public void sendMessageToClient(String message) {
        for (UserService user : userVector) {
            user.WriteOne(message);
        }
    }

    // 플레이어 감지
    public void detectPlayer() {
        // 현재 플레이어 수를 모든 유저에게 전송
        sendMessageToClient("PlayerCount|" + userVector.size());
    }

    // 게임 시작
    public void startGame() {
        setStone();
        setCharacter();
        sendMessageToClient("State|Start");
        startTimer();
    }

    // 흑돌, 백돌 랜덤으로 정하기
    public void setStone() {
        List<String> stones = new ArrayList<>();
        stones.add("검은색");
        stones.add("흰색");
        Collections.shuffle(stones); // 리스트 섞기

        // 섞은 값을 각 유저에게 전송
        for (int i = 0; i < userVector.size(); i++) {
            UserService user = userVector.get(i);
            user.WriteOne("StoneColor|" + stones.get(i));
            user.WriteOne("Turn|검은색");
        }
    }

    // 각 플레이어에게 캐릭터 랜덤으로 부여하기
    public void setCharacter() {
        List<String> characters = new ArrayList<>();
        characters.add("sangsang_bugi");
        characters.add("hansung_nyangi");
        characters.add("ggoggo");
        characters.add("gguggu");
        characters.add("sangjji");
        Collections.shuffle(characters); // 리스트 섞기

        String character1 = characters.get(0);
        String character2 = characters.get(1);

        String player1 = "Character|" + character1 + "," + character2;
        String player2 = "Character|" + character2 + "," + character1;

        UserService user1 = userVector.get(0);
        user1.WriteOne(player1);
        UserService user2 = userVector.get(1);
        user2.WriteOne(player2);
    }

    // 턴 전환
    public void switchTurn(String turn) {
        timer.cancel();
        if (turn.equals("검은색")) {
            sendMessageToClient("Turn|흰색");
            currentTurn = "흰색";
        } else if (turn.equals("흰색")) {
            sendMessageToClient("Turn|검은색");
            currentTurn = "검은색";
        }
        startTimer();
    }

    // 타이머 시작
    public void startTimer() {
        if (timer != null) {
            timer.cancel(); // 현재 타이머가 있으면 취소
        }
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            int timeLeft = timeLimit;

            @Override
            public void run() {
                timeLeft--;
                sendMessageToClient("Timer|" + timeLeft);
                if (timeLeft <= 0) {
                    timer.cancel();
                    switchTurn(currentTurn);
                }
            }
        }, 0, 1000);
    }

    class AcceptServer extends Thread {
        public void run() {
            while (true) {
                try {
                    clientSocket = serverSocket.accept();

                    UserService newUser = new UserService(clientSocket);
                    if (userVector.size() < USER_LIMIT) {
                        userVector.add(newUser);
                        System.out.println("플레이어 입장, 현재 플레이어 수 : " + userVector.size());
                        newUser.start();
                        detectPlayer();
                    } else {
                        clientSocket.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class UserService extends Thread {
        private InputStream inputStream;
        private OutputStream outputStream;
        private DataInputStream dataInputStream;
        private DataOutputStream dataOutputStream;

        public UserService(Socket clientSocket) {
            try {
                inputStream = clientSocket.getInputStream();
                outputStream = clientSocket.getOutputStream();
                dataInputStream = new DataInputStream(inputStream);
                dataOutputStream = new DataOutputStream(outputStream);

                // 입장 가능 여부를 플레이어에게 전송
                if (userVector.size() < USER_LIMIT) {
                    dataOutputStream.writeUTF("Room|입장가능");
                } else {
                    dataOutputStream.writeUTF("Room|입장불가능");
                }
            } catch (Exception e) {
                System.out.println("ERROR : UserService");
            }

        }

        public void WriteOne(String msg) {
            try {
                dataOutputStream.writeUTF(msg);
            } catch (IOException e1) {
                System.out.println("ERROR : dataOutputStream.writeUTF()");
                try {
                    dataOutputStream.close();
                    dataInputStream.close();
                    clientSocket.close();
                } catch (IOException e2) {
                    e2.printStackTrace();
                }

                userVector.removeElement(this);
                detectPlayer();
            }
        }

        public void WriteAll(String str) {
            for (UserService userService : userVector) {
                userService.WriteOne(str);
            }
        }

        public void run() {
            while (true) {
                try {
                    String msg = dataInputStream.readUTF().trim();
                    String[] type = msg.split("\\|");

                    if (type[0].equals("State") && type[1].equals("Ready")) {
                        readyCount++; // 준비 완료된 플레이어 수 증가
                        System.out.println("준비 완료 : " + readyCount + "명");
                        if (readyCount == USER_LIMIT) { // 모든 인원이 준비하면 게임 시작 상태로 변경
                            startGame();
                        }
                        WriteAll(msg + "\n");
                    } else if (type[0].equals("Turn")) {
                        currentTurn = type[1];
                        switchTurn(currentTurn);
                    } else if (type[0].equals("Winner")) {
                        readyCount = 0;
                        timer.cancel();
                        WriteAll(msg + "\n");
                    } else {
                        WriteAll(msg + "\n");
                    }
                } catch (IOException e1) {
                    try {
                        dataOutputStream.close();
                        dataInputStream.close();
                        clientSocket.close();
                        userVector.removeElement(this);
                        detectPlayer();
                        if (readyCount == USER_LIMIT) {
                            for (UserService userService : userVector) {
                                userService.WriteOne("Gameover|out");
                            }
                            readyCount = 0;
                            timer.cancel();
                        }
                        if (readyCount > 0) {
                            readyCount--;
                        }

                        System.out.println("플레이어 퇴장. 현재 플레이어 수 : " + userVector.size());
                        break;
                    } catch (Exception e2) {
                        break;
                    }
                }
            }
        }
    }
}