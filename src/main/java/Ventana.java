import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;

public class Ventana {
    private int h = 0;
    private JPanel panel1;
    private JButton iniciarButton;
    volatile JTextArea tweets;
    volatile JTextArea ubicacion;
    volatile JTextArea palabras_clave;
    volatile JLabel tweetCounter;
    volatile JLabel tweetAnalized;
    volatile JLabel tweetFiltered;
    private ProcessInfo controller;


    public Ventana() {
        controller = new ProcessInfo(this);
        tweetCounter.setText(String.valueOf(controller.getMaxTweets()));
        iniciarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (!controller.isIsRunning()) {
                            controller.start();
                        }
                    }
                });
                t.start();
            }
        });

        tweets.setEditable(false);
        ubicacion.setEditable(false);
        palabras_clave.setEditable(false);

        for (String key : controller.getKeywords()) {
            palabras_clave.append(key + "\n");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame frame = new JFrame("UltraMapping");
                frame.setBounds(250, 150, 0, 0);
                frame.setMinimumSize(new Dimension(1000, 500));
                frame.setContentPane(new Ventana().panel1);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.pack();
                frame.setVisible(true);
            }
        });
    }

    public synchronized void updatetweetAnalized(String n) {
        this.tweetAnalized.setText(n);
    }

    public synchronized void updatetweetFiltered(String n) {
        this.tweetFiltered.setText(n);
    }

    public synchronized void appendTweet(String toApp) {
        this.tweets.append(toApp);
    }

    public synchronized void appendUbicacion(String toApp){
        this.ubicacion.append(toApp);
    }
}
