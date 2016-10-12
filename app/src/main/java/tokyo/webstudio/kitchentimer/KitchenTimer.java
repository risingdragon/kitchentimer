package tokyo.webstudio.kitchentimer;

import java.util.Timer;
import java.util.TimerTask;

import android.os.Handler;

public class KitchenTimer {
    private int rest;
    private Timer timer = null;

    public KitchenTimer() {
        clear();
    }

    public void clear() {
        stop();
        rest = 0;
    }

    public void add(int time) {
        rest += time;
    }

    public String restTime() {
        if (rest <= 0) return "00:00:00";

        return String.format(
                "%02d:%02d:%02d",
                (int) Math.floor(rest / 3600),
                (int) Math.floor((rest % 3600) / 60),
                rest % 60
        );
    }

    public void stop() {
        if (timer != null) timer.cancel();
    }

    public boolean isFinished() {
        return rest == 0;
    }

    public void start(final Handler handler) {
        stop();

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                rest--;
                if (rest <= 0) {
                    timer.cancel();
                    clear();
                }

                handler.sendEmptyMessage(0);
            }
        }, 1000, 1000);
    }
}
