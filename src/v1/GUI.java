package v1;
import ilog.concert.IloException;
import ilog.concert.IloIntVar;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Line2D;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class GUI extends JFrame {
    
    private int[][] xy;
    private IloIntVar[][] s;
    
    public GUI(float[][] xy){
        //float to int
        this.xy = new int[xy.length][xy[0].length];
        this.s = s;
        for(int i = 0; i < this.xy.length; ++i){
            for (int j = 0; j < this.xy[0].length; j++) {
                this.xy[i][j] = (int) xy[i][j];
            }
        }
        
        drawCities();
        paint(this.getGraphics());
        
        //close window on button press
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                System.exit(0);
            }
        });
    }
    
    public void drawCities(){
        JPanel[] panel = new JPanel[Data.cities];
        for(int i = 0; i < panel.length; i++){
            panel[i] = new JPanel();
            panel[i].setBounds(xy[i][0] * 6,xy[i][1] * 6,10,10);
            
            panel[i].setBackground(Color.gray);
            add(panel[i]); //adding JPanel into JFrame
        }
        
        setLocation(200, 100); //initial position of window in the screen
        setLayout(null);  //no layout manager
        setSize(1000,800);//frame size 1000 width x 800 height
        setVisible(true); //frame will be visible, by default not visible
    }
    
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2 = (Graphics2D) g;
        
        //drawing lines between cities
        for(int i = 0; i < Data.cities; i++) {
            for(int j = 0; j < Data.cities; j++) {
                try {
                    // == 1
                    if(Model.model.getValue(Model.s[i][j]) > 0.9){
                        g2.draw(new Line2D.Float(
                        xy[i][0] * 6 + 5, xy[i][1] * 6 + 35,
                        xy[j][0] * 6 + 5, xy[j][1] * 6 + 35));
                    }
                } catch (IloException ex) {
                    Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
}
