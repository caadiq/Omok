public class Turn {
    private String turn;

    private static class TurnHolder {
        private static final Turn INSTANCE = new Turn();
    }

    public static Turn getInstance() {
        return Turn.TurnHolder.INSTANCE;
    }

    public String getTurn() {
        return turn;
    }

    public void setTurn(String turn) {
        this.turn = turn;
    }
}
