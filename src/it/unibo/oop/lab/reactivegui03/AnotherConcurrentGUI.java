package it.unibo.oop.lab.reactivegui03;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * 
 *
 */
public final class AnotherConcurrentGUI extends JFrame {
    private static final long serialVersionUID = 1L;
    private static final double WIDTH_PERC = 0.2;
    private static final double HEIGHT_PERC = 0.1;
    private final JLabel display = new JLabel();
    private final JButton up = new JButton("up");
    private final JButton down = new JButton("down");
    private final JButton stop = new JButton("stop");

    /**
     * Builds a new AnotherConcurrentGUI.
     */
    public AnotherConcurrentGUI() {
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
        final StopAgent stopAgent = new StopAgent(agent);
        new Thread(agent).start();
        new Thread(stopAgent).start();

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
            while (!this.stop) {
                try {
                    final int currCounter = this.counter;
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            AnotherConcurrentGUI.this.display.setText(Integer.toString(currCounter));
                        }
                    });
                    if (this.isInc()) {
                        this.counter++;
                    } else {
                        this.counter--;
                    }
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    System.err.println(e.getMessage());
                    e.printStackTrace();
                }
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

        /**
         * Sets the value of stop to true, which causes the thread
         * of Agent to stop its execution.
         */
        public void stopCounting() {
            this.stop = true;
        }
    }

    private class StopAgent implements Runnable {

        private static final int WAIT_TIME = 10_000;
        private final Agent agent;

        /**
         * Builds a new StopAgent.
         * @param agent Agent that the new StopAgent will stop after 10 seconds.
         */
        StopAgent(final Agent agent) {
            this.agent = agent;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(WAIT_TIME);
                this.agent.stopCounting();
                AnotherConcurrentGUI.this.up.setEnabled(false);
                AnotherConcurrentGUI.this.down.setEnabled(false);
                AnotherConcurrentGUI.this.stop.setEnabled(false);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}
