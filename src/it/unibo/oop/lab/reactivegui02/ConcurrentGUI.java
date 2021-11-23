package it.unibo.oop.lab.reactivegui02;

import java.awt.Dimension;
import javax.swing.JButton;
import javax.swing.JFrame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * Implements a reactive GUI with increasing and decreasing counter.
 *
 */
public final class ConcurrentGUI extends JFrame {

    private static final long serialVersionUID = 1L;
    private static final double WIDTH_PERC = 0.2;
    private static final double HEIGHT_PERC = 0.1;
    private final JLabel display = new JLabel();
    private final JButton up = new JButton("up");
    private final JButton down = new JButton("down");
    private final JButton stop = new JButton("stop");

    /**
     * Builds a new ConcurrentGUI.
     */
    public ConcurrentGUI() {
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setSize((int) (screenSize.getWidth() * WIDTH_PERC), (int) (screenSize.getHeight() * HEIGHT_PERC));
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        final JPanel panel = new JPanel();
        panel.add(this.display);
        panel.add(this.up);
        panel.add(this.down);
        panel.add(this.stop);
        this.getContentPane().add(panel);
        this.setVisible(true);

        final Agent agent = new Agent();
        new Thread(agent).start();

        stop.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                agent.stopCounting();
                up.setEnabled(false);
                down.setEnabled(false);
                stop.setEnabled(false);
            }
        });

        up.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                agent.setInc();
            }
        });

        down.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                agent.setDec();
            }
        });
    }

    private class Agent implements Runnable {
        private volatile boolean stop;
        private int counter;
        private volatile boolean inc = true;

        @Override
        public void run() {
            try {
                while (!this.stop) {
                    final int currCounter = this.counter;
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            ConcurrentGUI.this.display.setText(Integer.toString(currCounter));
                        }
                    });
                    if (this.isInc()) {
                        this.counter++;
                    } else {
                        this.counter--;
                    }
                    Thread.sleep(100);
                }
            } catch (InterruptedException e) {
                System.err.println(e.getMessage());
                e.printStackTrace();
            }
        }

        /**
         * Returns true if the counter is increasing, false is decreasing.
         * @return {@link boolean} value representing the direction of the counter.
         */
        public boolean isInc() {
            return this.inc;
        }

        /**
         * Sets the value of inc to true.
         */
        public void setInc() {
            this.inc = true;
        }

        /**
         * Sets the value of inc to false.
         */
        public void setDec() {
            this.inc = false;
        }

        public void stopCounting() {
            this.stop = true;
        }
    }
}
