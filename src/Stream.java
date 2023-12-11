import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Stream {
    private static final String SERVER_IP = "localhost";
    private static final int SERVER_PORT = 10000;

    private final Socket socket;
    private final DataInputStream inputStream;
    private final DataOutputStream outputStream;

    private static class StreamHolder {
        private static final Stream INSTANCE;

        static {
            try {
                INSTANCE = new Stream();
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage());
            }
        }
    }

    private Stream() throws IOException {
        socket = new Socket(SERVER_IP, SERVER_PORT);
        inputStream = new DataInputStream(socket.getInputStream());
        outputStream = new DataOutputStream(socket.getOutputStream());
    }

    public static Stream getInstance() {
        return StreamHolder.INSTANCE;
    }

    // 서버로 값 전송
    public void sendMessage(String result) throws IOException {
        outputStream.writeUTF(result);
        outputStream.flush();
    }

    // 서버에서 값 가져오기
    public String[] receiveMessage() throws IOException {
        return inputStream.readUTF().trim().split("\\|");
    }

    public void close() throws IOException {
        inputStream.close();
        outputStream.close();
        socket.close();
    }
}