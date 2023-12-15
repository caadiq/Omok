public class State {
    private int playerCount = 0;
    private boolean gameState;
    private boolean previousTurnReturned;

    private static class StateHolder {
        private static final State INSTANCE = new State();
    }

    public static State getInstance() {
        return StateHolder.INSTANCE;
    }

    public int getPlayerCount() {
        return playerCount;
    }

    public void setPlayerCount(int playerCount) {
        this.playerCount = playerCount;
    }

    public boolean getGameState() {
        return gameState;
    }

    public void setGameState(boolean gameState) {
        this.gameState = gameState;
    }

    public boolean getPreviousTurnReturned() {
        return previousTurnReturned;
    }

    public void setPreviousTurnReturned(boolean previousTurnReturned) {
        this.previousTurnReturned = previousTurnReturned;
    }
}