public class Player {
    private String myCharacter;
    private String opponentCharacter;
    private String myStone;
    private String nickname;

    private static class PlayerHolder {
        private static final Player INSTANCE = new Player();
    }

    public static Player getInstance() {
        return Player.PlayerHolder.INSTANCE;
    }

    public String getMyCharacter() {
        return myCharacter;
    }

    public void setMyCharacter(String myCharacter) {
        this.myCharacter = myCharacter;
    }

    public String getOpponentCharacter() {
        return opponentCharacter;
    }

    public void setOpponentCharacter(String opponentCharacter) {
        this.opponentCharacter = opponentCharacter;
    }

    public String getMyStone() {
        return myStone;
    }

    public void setMyStone(String myStone) {
        this.myStone = myStone;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
