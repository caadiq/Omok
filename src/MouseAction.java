import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MouseAction extends MouseAdapter {
    private GameMethod gm;
    private GuiBoard sm;
    private MainFrame g;

    public MouseAction(GameMethod gm, GuiBoard mm, MainFrame g) {
        this.g=g;
        this.gm=gm;
        this.sm=mm;
    }
    @Override
    public void mousePressed(MouseEvent me) {
        int x = (int)Math.round(me.getX()/(double) 47)-1;
        int y = (int)Math.round(me.getY()/(double) 47)-2;

        if(gm.checkInput(y, x) == false) {
            return;
        }

        Word w = new Word(y,x,gm.getCun_GamePlayer());
        gm.inputWord(w);
        gm.nextPlayer(gm.getCun_GamePlayer());
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