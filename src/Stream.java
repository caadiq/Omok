import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Stream {
    private static final String SERVER_IP = "localhost";
    private static final int SERVER_PORT = 10000;

    private Socket socket;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;

    private static class StreamHolder {
        private static final Stream INSTANCE = new Stream();
    }

    private Stream() {
        try {
            socket = new Socket(SERVER_IP, SERVER_PORT);
            inputStream = new DataInputStream(socket.getInputStream());
            outputStream = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            System.out.println("Error : " + e.getMessage());
        }
    }

    public static Stream getInstance() {
        return StreamHolder.INSTANCE;
    }

    // 서버 재접속
    public void reconnect() {
        close();
        try {
            socket = new Socket(SERVER_IP, SERVER_PORT);
            inputStream = new DataInputStream(socket.getInputStream());
            outputStream = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            System.out.println("Error : " + e.getMessage());
        }
    }

    // 서버로 값 전송
    public void sendMessage(String result) throws IOException {
        if (outputStream != null) {
            outputStream.writeUTF(result);
            outputStream.flush();
        }
    }

    // 서버에서 값 가져오기
    public String[] receiveMessage() throws IOException {
        if (inputStream != null)
            return inputStream.readUTF().trim().split("\\|");
        return new String[0];
    }

    // 서버와 접속 종료
    public void close() {
        try {
            if (socket != null) socket.close();
            if (inputStream != null) inputStream.close();
            if (outputStream != null) outputStream.close();
        } catch (IOException e) {
            System.out.println("Error : " + e.getMessage());
        }
    }
}