public class MyStone {
    private String stone;

    private static class MyStoneHolder {
        private static final MyStone INSTANCE = new MyStone();
    }

    public static MyStone getInstance() {
        return MyStone.MyStoneHolder.INSTANCE;
    }

    public String getMyStone() {
        return stone;
    }

    public void setMyStone(String stone) {
        this.stone = stone;
    }
}
