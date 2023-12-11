import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MouseAction extends MouseAdapter {
    private GameMethod gm;
    private GuiBoard sm;
    private MainFrame g;
    private GuiButton guiButton;

    public MouseAction(GameMethod gm, GuiBoard mm, MainFrame g, GuiButton guiButton) {
        this.g=g;
        this.gm=gm;
        this.sm=mm;
        this.guiButton=guiButton;
    }
    @Override
    public void mousePressed(MouseEvent me) {
        if(guiButton.getIngame() == 0){
            return;
        }
        int x = (int)Math.round(me.getX()/(double) 47)-1;
        int y = (int)Math.round(me.getY()/(double) 47)-2;

        if(gm.checkInput(y, x) == false) {
            return;
        }

        Word w = new Word(y,x, guiButton.getColor()+1);
        gm.inputWord(w);
        sm.repaint();
        if(gm.endGame(w)==true) {
            String ms = " ";
            if(w.getColor()==1) {
                ms="검돌승리!";
            }
            else if(w.getColor()==2) {
                ms="백돌승리!";
            }
            showWin(ms);
        }
    }
    public void showWin(String msg) {
        System.out.println(msg);
        gm.init();
        JOptionPane.showMessageDialog(g, msg, "",JOptionPane.INFORMATION_MESSAGE);


    }
}