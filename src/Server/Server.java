package Server;

import java.awt.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

public class Server {
    private static final int SERVER_PORT = 10000;

    private static final int USER_LIMIT = 2;
    public int readyCount =0;
    private ServerSocket socket;
    private Socket clientSocket;

    private final Vector<UserService> userVector = new Vector<>();

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
            socket = new ServerSocket(SERVER_PORT);

        } catch (IOException e) {
            e.printStackTrace();
        }
        AcceptServer acceptServer = new AcceptServer();
        acceptServer.start();
    }

    class AcceptServer extends Thread {
        public void run() {
            while (true) {
                try {
                    System.out.println("플레이어 대기 중...");
                    clientSocket = socket.accept();

                    UserService newUser = new UserService(clientSocket);
                    if (userVector.size() < USER_LIMIT) {
                        userVector.add(newUser);
                        System.out.println("플레이어 입장, 현재 플레이어 수 : " + userVector.size());
                        newUser.start();

                        int i = 0;
                        for(UserService userService: userVector) {
                            userService.dataOutputStream.writeUTF("numbercheck|" + userVector.size() + "|" + i);
                            i++;
                        }
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
        public DataOutputStream dataOutputStream;

        public UserService(Socket clientSocket) {
            try {
                inputStream = clientSocket.getInputStream();
                outputStream = clientSocket.getOutputStream();
                dataInputStream = new DataInputStream(inputStream);
                dataOutputStream = new DataOutputStream(outputStream);

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
                    if(type[0].equals("Ready")){
                        readyCount+=1;
                        msg+="|";
                        msg+=readyCount;
                    }
                    msg = msg.trim();
                    System.out.println(msg);
                    WriteAll(msg + "\n");
                } catch (IOException e1) {
                    try {
                        dataOutputStream.close();
                        dataInputStream.close();
                        clientSocket.close();
                        userVector.removeElement(this);
                        System.out.println("플레이어 퇴장. 현재 플레이어 수 : " + userVector.size());
                        readyCount-=1;
                        int i = 0;
                        for(UserService userService : userVector) {
                            userService.dataOutputStream.writeUTF("numbercheck|" + userVector.size() + "|" + i);
                            i++;
                        }

                        break;
                    } catch (Exception e2) {
                        break;
                    }
                }
            }
        }
    }
}