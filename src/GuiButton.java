import javax.swing.*;
import java.awt.*;

public class GuiButton extends JPanel {
    private static final int layoutWidth = 375;
    private static final int layoutHeight = 70;

    private final JButton buttonBacksies;
    private final JButton buttonReady;
    
    public GuiButton() {
        setSize(layoutWidth, layoutHeight);
        setLayout(null);

        // 준비 버튼
        buttonReady = new JButton("준비");
        buttonReady.setSize(180, 70);
        buttonReady.setLocation(0, 0);
        buttonReady.setFont(new Font("Dialog", Font.BOLD, 22));
        buttonReady.addActionListener(e -> {
//            TODO
//                - 버튼 클릭 시 준비 완료 상태로 변경 (버튼 비활성화 하기)
//                - 두 명 모두 준비 완료 시 게임 시작
            buttonReady.setEnabled(false);
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
}
