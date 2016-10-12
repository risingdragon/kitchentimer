package tokyo.webstudio.kitchentimer;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_AUDIO_PICKUP = 1;
    private KitchenTimer ktimer;
    private Ringtone ringtone;
    private MediaPlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ktimer = new KitchenTimer();
        drawTime();

        player = MediaPlayer.create(this, R.raw.sel);

        String authority = Common.getProperty(getApplicationContext(), "sound.authority");
        String scheme = Common.getProperty(getApplicationContext(), "sound.scheme");
        String path = Common.getProperty(getApplicationContext(), "sound.path");
        String title = Common.getProperty(getApplicationContext(), "sound.title");
        String time = Common.getProperty(getApplicationContext(), "sound.time");
        if (authority != null && scheme != null && path != null) {
            Uri uri = (new Uri.Builder()).authority(authority).scheme(scheme).path(path).build();
            setRingtone(uri);
        }

        if (title != null) {
            TextView text = (TextView) findViewById(R.id.soundNameText);
            text.setText(title);
        }

        if (time != null) {
            SeekBar ringTime = (SeekBar) findViewById(R.id.ringTime);
            ringTime.setProgress(Integer.parseInt(time));
        }

        if (ringtone == null) selectSound();

        Button button = (Button) findViewById(R.id.tenminBtn);
        button.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                _click();
                ktimer.add(10 * 60);
                drawTime();
            }
        });

        button = (Button) findViewById(R.id.oneminBtn);
        button.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                _click();
                ktimer.add(1 * 60);
                drawTime();
            }
        });

        button = (Button) findViewById(R.id.tensecBtn);
        button.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                _click();
                ktimer.add(10);
                drawTime();
            }
        });

        button = (Button) findViewById(R.id.onesecBtn);
        button.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                _click();
                ktimer.add(1);
                drawTime();
            }
        });

        button = (Button) findViewById(R.id.startBtn);
        button.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                _click();

                Handler handler = new Handler() {
                    public void handleMessage(Message msg) {
                        drawTime();
                        if (ktimer.isFinished()) ring();
                    }
                };

                ktimer.start(handler);
            }
        });

        button = (Button) findViewById(R.id.stopBtn);
        button.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                _click();
                ktimer.stop();
            }
        });

        button = (Button) findViewById(R.id.clearBtn);
        button.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                _click();
                ktimer.clear();
                drawTime();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_pickup_sound) {
            selectSound();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        SeekBar ringTime = (SeekBar) findViewById(R.id.ringTime);
        int time = ringTime.getProgress();
        Common.saveProperty(getApplicationContext(), "sound.time", String.valueOf(time));
        super.onStop();
    }

    private void drawTime() {
        TextView text = (TextView) findViewById(R.id.timeText);
        text.setText(ktimer.restTime());
    }

    private void ring() {
        stopRing();
        SeekBar ringTime = (SeekBar) findViewById(R.id.ringTime);
        if (ringtone != null) {
            ringtone.play();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    stopRing();
                }
            }, ringTime.getProgress() * 500);
        }
    }

    private void _click() {
        stopRing();
        player.seekTo(0);
        player.start();
    }

    private void stopRing() {
        if (ringtone != null) ringtone.stop();
    }

    private void selectSound() {
        stopRing();
        Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
        startActivityForResult(intent, REQUEST_AUDIO_PICKUP);
    }

    private void setRingtone(Uri uri) {
        ringtone = RingtoneManager.getRingtone(getApplicationContext(), uri);
            // ringtone.setStreamType(AudioManager.STREAM_ALARM);
//            ringtone.setAudioAttributes(new AudioAttributes.Builder()
//                            .setUsage(AudioAttributes.USAGE_ALARM)
//                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
//                            .build()
//            );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_AUDIO_PICKUP && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
            setRingtone(uri);
            String title = ringtone.getTitle(getApplicationContext());
            TextView text = (TextView) findViewById(R.id.soundNameText);
            text.setText(title);
            Common.saveProperty(getApplicationContext(), "sound.scheme", uri.getScheme());
            Common.saveProperty(getApplicationContext(), "sound.path", uri.getPath());
            Common.saveProperty(getApplicationContext(), "sound.authority", uri.getAuthority());
            Common.saveProperty(getApplicationContext(), "sound.title", title);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        stopRing();
        return super.onTouchEvent(event);
    }

}
