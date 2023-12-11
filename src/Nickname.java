public class Nickname {
    private String nickname;

    private static class NicknameHolder {
        private static final Nickname INSTANCE = new Nickname();
    }

    public static Nickname getInstance() {
        return NicknameHolder.INSTANCE;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
