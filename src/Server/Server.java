package Server;

import java.awt.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Vector;

public class Server {
    private static final int SERVER_PORT = 10000;
    private static final int USER_LIMIT = 2;

    private ServerSocket serverSocket;
    private Socket clientSocket;

    private final Vector<UserService> userVector = new Vector<>();

    public int readyCount = 0;

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

    // 플레이어 감지
    public void detectPlayer() {
        // 현재 플레이어 수를 모든 유저에게 전송
        for (UserService userService : userVector) {
            userService.WriteOne("PlayerCount|" + userVector.size());
        }
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
                    String msg = dataInputStream.readUTF();
                    String[] type = msg.trim().split("\\|");

                    if (type[0].equals("State") && type[1].equals("Ready")) {
                        readyCount++; // 준비 완료된 플레이어 수 증가
                        System.out.println("readyCount: " + readyCount);
                        if (readyCount == USER_LIMIT) { // 모든 인원이 준비하면 게임 시작 상태로 변경
                            setStone();
                            setCharacter();
                            msg = "State|Start";
                        }
                    } else if (type[0].equals("Turn")) {
                        msg = "Turn|";
                        // 현재 턴이 검은색이면 흰색으로 바꾸고 흰색이라면 검은색으로 변경
                        if (type[1].equals("검은색")) {
                            msg += "흰색";
                        } else if (type[1].equals("흰색")) {
                            msg += "검은색";
                        }
                        System.out.println(msg);
                    } else if (type[0].equals("Winner")) {
                        readyCount = 0;
                    }

                    msg = msg.trim();
                    WriteAll(msg + "\n");
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