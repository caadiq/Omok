package Server;

import java.awt.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.List;

public class Server {

    private boolean IsPlaying = false;
    private static final int SERVER_PORT = 10000;
    private static final int USER_LIMIT = 2;

    private ServerSocket serverSocket;
    private Socket clientSocket;

    private final Vector<UserSession> userVector = new Vector<>();

    private int readyCount = 0; // 준비 완료한 플레이어 수
    private String currentTurn = "검은색"; // 현재 턴
    private String stonePosition; // 돌의 좌표 및 색상
    private boolean canBlackReturn = true; // 검은색 돌의 무르기 가능 여부
    private boolean canWhiteReturn = true; // 흰색 돌의 무르기 가능 여부
    private boolean previousTurnReturned = false; // 상대방이 무르기를 사용 했는지 여부

    private Timer timer;
    private final int timeLimit = 30; // 타이머 30초

    private record UserSession(UserService userService, String nickname) {
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                new Server();
            } catch (Exception e) {
                System.out.println("Error : " + e.getMessage());
            }
        });
    }

    public Server() {
        try {
            serverSocket = new ServerSocket(SERVER_PORT);
        } catch (IOException e) {
            System.out.println("Error : " + e.getMessage());
        }

        AcceptServer acceptServer = new AcceptServer();
        acceptServer.start();
    }

    // 모든 클라이언트에게 메시지 전송
    private void sendMessageToAllClient(String message) {
        for (UserSession userSession : userVector) {
            UserService userService = userSession.userService();
            userService.sendMessageToClient(message);
        }
    }

    // 플레이어 감지
    private void detectPlayer(UserSession userSession, String userState) {
        sendMessageToAllClient("PlayerCount|" + userVector.size()); // 현재 플레이어 수를 모든 유저에게 전송
        sendMessageToAllClient("Nickname|" + userSession.nickname() + "," + userState); // 유저 입장 및 퇴장 메시지 전송
    }

    // 게임 시작
    private void startGame() {
        setStone();
        setCharacter();
        sendMessageToAllClient("State|Start");
        sendMessageToAllClient("CanReturn|검은색,true");
        sendMessageToAllClient("CanReturn|흰색,true");
        canWhiteReturn = true;
        canBlackReturn = true;
        previousTurnReturned = false;
        startTimer();
    }

    // 흑돌, 백돌 랜덤으로 정하기
    private void setStone() {
        List<String> stones = new ArrayList<>(Arrays.asList("검은색", "흰색")); // 돌 리스트 생성
        Collections.shuffle(stones); // 돌 리스트 섞기

        // 섞은 값을 각 유저에게 전송
        for (int i = 0; i < 2; i++) {
            UserService user = userVector.get(i).userService();
            user.sendMessageToClient("StoneColor|" + stones.get(i));
            user.sendMessageToClient("Turn|검은색");
        }
    }

    // 각 플레이어에게 캐릭터 랜덤으로 부여하기
    private void setCharacter() {
        List<String> characters = new ArrayList<>(Arrays.asList("sangsang_bugi", "hansung_nyangi", "ggoggo", "gguggu", "sangjji")); // 캐릭터 리스트 생성
        Collections.shuffle(characters); // 캐릭터 리스트 섞기
        List<String> selectedCharacters = characters.subList(0, 2); // 섞은 리스트 중 앞에 두 항목 선택

        // 섞은 값을 각 유저에게 전송
        for (int i = 0; i < 2; i++) {
            String character = "Character|" + selectedCharacters.get(i) + "," + selectedCharacters.get((i + 1) % 2);
            UserService user = userVector.get(i).userService();
            user.sendMessageToClient(character);
        }
    }

    // 턴 전환
    private void switchTurn(String turn, boolean isReturned) {
        timer.cancel();
        previousTurnReturned = isReturned;

        if (turn.equals("검은색")) currentTurn = "흰색";
        else if (turn.equals("흰색")) currentTurn = "검은색";

        sendMessageToAllClient("Turn|" + currentTurn); // 바뀐 턴을 전송
        sendMessageToAllClient("PreviousTurnReturned|" + previousTurnReturned); // 상대방이 방금 무르기를 사용했는지 여부를 전송
        startTimer();
    }

    // 무르기
    private void returnStone(String stone) {
        if (!stone.equals(currentTurn)) {
            if (stone.equals("검은색") && canBlackReturn) {
                canBlackReturn = false;
                sendMessageToAllClient("Return|" + stonePosition);
                sendMessageToAllClient("CanReturn|검은색,false");
                switchTurn("흰색", true);
            } else if (stone.equals("흰색") && canWhiteReturn) {
                canWhiteReturn = false;
                sendMessageToAllClient("Return|" + stonePosition);
                sendMessageToAllClient("CanReturn|흰색,false");
                switchTurn("검은색", true);
            }
        }
    }

    // 타이머 시작
    private void startTimer() {
        if (timer != null) timer.cancel(); // 현재 작동 중인 타이머가 있으면 취소

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            int timeLeft = timeLimit; // 타이머가 시작되면 남은 시간을 시간 제한(30초)으로 설정

            @Override
            public void run() {
                timeLeft--;
                if(IsPlaying){
                    sendMessageToAllClient("Timer|" + timeLeft);
                    if (timeLeft <= 0) { // 타이머가 0초가 되면
                        timer.cancel(); // 타이머 중지
                        switchTurn(currentTurn, previousTurnReturned); // 턴 전환
                    }
                }
            }
        }, 0, 1000); // 1초마다 반복
    }

    // 유저 세선 종료
    private void removeUserSession(UserService userService) {
        UserSession exitUserSession = null;
        // UserVector에서 해당 유저 인스턴스 찾기
        for (UserSession userSession : userVector) {
            if (userSession.userService() == userService) { // 일치하는 값을 찾으면
                exitUserSession = userSession; // 인스턴스를 종료할 유저 세션으로 설정
                break;
            }
        }

        if (exitUserSession != null) {
            userVector.remove(exitUserSession); // UserVector에서 해당 유저 제거
            detectPlayer(exitUserSession, "퇴장"); // 퇴장 처리
            if (readyCount > 0) readyCount--; // 준비 완료 수 감소

            System.out.println(exitUserSession.nickname + "퇴장. 현재 플레이어 수 : " + userVector.size() + "명");
            System.out.println("준비 완료 : " + readyCount + "명");
        }
    }

    class AcceptServer extends Thread {
        public void run() {
            while (true) {
                try {
                    clientSocket = serverSocket.accept();

                    DataInputStream dataInputStream = new DataInputStream(clientSocket.getInputStream());
                    DataOutputStream dataOutputStream = new DataOutputStream(clientSocket.getOutputStream());

                    String[] messages = dataInputStream.readUTF().trim().split("\\|");
                    String nickname = messages[0].equals("Nickname") ? messages[1] : "";

                    boolean nicknameExists = userVector.stream().anyMatch(userSession -> userSession.nickname().equals(nickname)); // 닉네임 중복 확인

                    if (nicknameExists) { // 닉네임 중복 시
                        dataOutputStream.writeUTF("Nickname|중복");
                        clientSocket.close(); // 해당 클라이언트 소켓 닫기
                        System.out.println("소켓 종료1");
                    } else if (userVector.size() < USER_LIMIT) { // 접속 중인 인원이 2명보다 적으면
                        UserService userService = new UserService(dataInputStream, dataOutputStream);
                        UserSession userSession = new UserSession(userService, nickname);

                        dataOutputStream.writeUTF("Room|입장가능");
                        userVector.add(userSession); // UserVector에 유저 추가
                        detectPlayer(userSession, "입장"); // 유저 입장 처리
                        userService.start(); // 스레드 시작
                    } else { // 접속 중인 인원이 2명이라면
                        dataOutputStream.writeUTF("Room|입장불가능");
                        clientSocket.close(); // 해당 클라이언트 소켓 닫기
                        System.out.println("소켓 종료2");
                    }
                } catch (IOException e) {
                    System.out.println("Error : " + e.getMessage());
                }
            }
        }
    }

    class UserService extends Thread {
        private DataInputStream dataInputStream;
        private DataOutputStream dataOutputStream;

        public UserService(DataInputStream dataInputStream, DataOutputStream dataOutputStream) {
            try {
                this.dataInputStream = dataInputStream;
                this.dataOutputStream = dataOutputStream;
            } catch (Exception e) {
                System.out.println("Error : " + e.getMessage());
            }
        }

        public void sendMessageToClient(String msg) {
            try {
                dataOutputStream.writeUTF(msg);
            } catch (IOException e1) {
                System.out.println("Error : " + e1.getMessage());
                System.out.println("아웃풋 데이터 오류");
                try {
                    dataOutputStream.close();
                    dataInputStream.close();
                    clientSocket.close();
                    System.out.println("소켓 종료3");
                } catch (IOException e2) {
                    System.out.println("Error : " + e2.getMessage());
                }

                removeUserSession(this);
            }
        }

        public void run() {
            while (true) {
                try {
                    String message = dataInputStream.readUTF().trim();
                    String[] messages = message.split("\\|");

                    switch (messages[0]) {
                        case "PlayerEnter" ->
                                System.out.println(messages[1] + "입장. 현재 플레이어 수 : " + userVector.size() + "명");
                        case "State" -> {
                            if (messages[1].equals("Ready")) {
                                readyCount++; // 준비 완료된 플레이어 수 증가
                                System.out.println("준비 완료 : " + readyCount + "명");

                                if (readyCount == USER_LIMIT) { // 모든 인원이 준비하면 게임 시작 상태로 변경
                                    startGame();
                                    System.out.println("게임 시작");
                                    IsPlaying =true;
                                }
                                sendMessageToAllClient(message);
                            }
                        }
                        case "Turn" -> {
                            currentTurn = messages[1];
                            switchTurn(currentTurn, false);
                        }
                        case "GameOver" -> {
                            if (messages[1].equals("PlayerExit"))
                                sendMessageToAllClient(message);
                            else
                                sendMessageToAllClient("GameOver|" + messages[1]);

                            readyCount = 0;
                            if(timer !=null){
                                timer.cancel();
                            }

                            System.out.println("게임 종료");
                            IsPlaying =false;
                        }
                        case "StonePosition" -> {
                            stonePosition = messages[1];
                            sendMessageToAllClient(message);
                        }
                        case "Return" -> returnStone(messages[1]);
                        default -> sendMessageToAllClient(message + "\n");
                    }
                } catch (IOException e1) {
                    try {
//                        dataOutputStream.close();
//                        dataInputStream.close();
//                        clientSocket.close();
                        System.out.println("소켓 종료4");
                        removeUserSession(this);
                        break;
                    } catch (Exception e2) {
                        break;
                    }
                }
            }
        }
    }
}