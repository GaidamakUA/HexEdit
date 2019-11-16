/*
 * Created on 15.02.2011
 *
 * Version: 0.1
 * Changes: check when values for BlinkRate is zero or below
 */

package at.HexLib.library;

import javax.swing.*;
import java.util.Timer;
import java.util.TimerTask;

public class CursorBlink {

    private Timer timer;
    private boolean active = false;
    private long blinkIntervall;
    private final HexLib he;
    private final int blinkRateDefault;

    public CursorBlink(final HexLib he) {
        /**
         * prevent incorrect setup due to UIManager delays <br>
         * 2013-12-06
         * */
        if (UIManager.getInt("TextArea.caretBlinkRate") <= 0) {
            blinkRateDefault = 500;
        } else {
            blinkRateDefault = UIManager.getInt("TextArea.caretBlinkRate");
        }

        setBlinkIntervall(blinkRateDefault);
        this.he = he;
        createNewTimer();
    }

    public void createNewTimer() {
        stop();
        timer = new Timer();
        timer.scheduleAtFixedRate(getTimerTask(), 0, getBlinkIntervall());
    }

    private TimerTask getTimerTask() {
        TimerTask task = new TimerTask() {

            @Override
            public void run() {
                setActive(!isActive());
                if (he != null) {
                    he.repaintCursorOnly();
                }
            }
        };
        return task;
    }

    public void stop() {
        if (timer != null) {
            timer.cancel();
        }
    }

    private void setActive(boolean active) {
        this.active = active;
    }

    public boolean isActive() {
        return active;
    }

    public void setBlinkIntervall(long blinkIntervall) {
        if (blinkIntervall <= 0) {
            /* prevent changes with invalid blink-timers 2013-12-06 */
            return;
        }
        this.blinkIntervall = blinkIntervall;
        createNewTimer();
    }

    public long getBlinkIntervall() {
        return blinkIntervall;
    }

}
