import javax.swing.*;
import java.awt.*;
import java.io.IOException;


public class GuiButton extends JPanel {
    private static final int layoutWidth = 375;
    private static final int layoutHeight = 70;
    private final Stream stream = Stream.getInstance();
    private final JButton buttonBacksies;
    private final JButton buttonReady;
    MainFrame m;
    private int count=0;

    private int Ingame = 0;

    int color=0;


    public GuiButton(MainFrame m) {
        this.m=m;
        setSize(layoutWidth, layoutHeight);
        setLayout(null);

        // 준비 버튼
        buttonReady = new JButton("준비");
        buttonReady.setSize(180, 70);
        buttonReady.setLocation(0, 0);
        buttonReady.setFont(new Font("Dialog", Font.BOLD, 22));
        buttonReady.addActionListener(e -> {
            if(count!=2){
                JOptionPane.showMessageDialog(m, "상대방이 아직 들어오지 않았습니다", "",JOptionPane.INFORMATION_MESSAGE);
            }
            else{
                try {
                    stream.sendMessage("Ready|"+"Ready");
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
//            TODO
//                - 버튼 클릭 시 준비 완료 상태로 변경 (버튼 비활성화 하기)
//                - 두 명 모두 준비 완료 시 게임 시작
                buttonReady.setEnabled(false);
            }
        });
        add(buttonReady);

        // 무르기 버튼
        buttonBacksies = new JButton("무르기");
        buttonBacksies.setSize(180, 70);
        buttonBacksies.setLocation(195, 0);
        buttonBacksies.setFont(new Font("Dialog", Font.BOLD, 22));
        buttonBacksies.setEnabled(false);
        buttonBacksies.addActionListener(e -> {
//            TODO
//                - 자기 턴에 버튼 활성화
//                - 버튼 클릭 시 한 번 무르기
//                - 무르기를 한 번 사용했다면 버튼 계속 비활성화 상태로 두기 (한 게임에 한 번만 사용 가능)
        });
        add(buttonBacksies);
    }

    public void printGameStart(String[] message){
        if(message[2].equals("1")){
        }
        else if(message[2].equals("2")){
            String msg=" ";
            if(color==1){
                msg="게임 시작! 당신은 흰색입니다.";
            }
            if(color==0){
                msg="게임 시작! 당신은 검은색입니다.";
            }
            JOptionPane.showMessageDialog(m, msg, "",JOptionPane.INFORMATION_MESSAGE);
            Ingame = 1;
        }
    }

    public void canGameStart(String[] message){
        int number = Integer.parseInt(message[1]);
        System.out.println("서버에 접속중인 사람 수:" + number);
        count = number;
        color = Integer.parseInt(message[2]);
        System.out.println(color);
    }

    public int getColor() {
        return color;
    }

    public int getIngame() {
        return Ingame;
    }
}
