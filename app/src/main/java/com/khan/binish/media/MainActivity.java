package com.khan.binish.media;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener, MediaPlayer.OnCompletionListener {

    //declaring all the required variables
    private SeekBar sb,sb2;
    private ToggleButton tb;
    private Thread th;
    private MediaPlayer mp;
    private TextView tv2,tv3,tv4;
    boolean status=true;
    private int[] songArray;
    private int currSong;
    private final static int MAX_VOLUME = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        //initialising variables
        tv2=(TextView)findViewById(R.id.tv2);
        tv3=(TextView)findViewById(R.id.tv3);
        tv4=(TextView)findViewById(R.id.tv4);
        sb=(SeekBar)findViewById(R.id.seekbar1);
        sb2=(SeekBar)findViewById(R.id.seekbar2);
        tb=(ToggleButton)findViewById(R.id.toggleButton1);

        //setting background
        tb.setBackgroundResource(R.drawable.play);

        //initialising song Array
        songArray=new int[]{R.raw.lovemashup,R.raw.bollymashup};

        setUpMp(currSong);
        sb2.setMax(4000);
        sb2.setProgress(2000);
        sb.setOnSeekBarChangeListener(this);
        sb2.setOnSeekBarChangeListener(this);
        mp.setVolume(2.0f,2.0f);


    }

    //playing song
    public void setUpMp(int songid)
    {
        mp=MediaPlayer.create(this,songArray[songid]);

        //setting duration
        sb.setMax(mp.getDuration());
        String str=this.getString(songArray[songid]);

        Toast.makeText(MainActivity.this,str, Toast.LENGTH_LONG).show();

        str=str.substring(str.lastIndexOf("/")+1);
        tv2.setText("Song name:"+str);
        int sec=mp.getDuration()/1000;
        int min=sec/60;
        tv4.setText("Length: "+min+":"+(sec%60));
        tv3.setText("00:00");
    }
    public void manage(View v)
    {
        if(tb.isChecked())
        {
            mp.start();
            mp.setOnCompletionListener(this);
            tb.setBackgroundResource(R.drawable.pause);
            status=true;
            th=new Thread(new MyThread());
            th.start();

        }
        else
        {
            mp.pause();
            tb.setBackgroundResource(R.drawable.play);
            status=false;
            th=null;
        }

    }
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
       if(seekBar==sb2)
        {
            float volume = (float)progress/1000;
            Log.i("Volume:",volume+"");
            mp.setVolume(volume, volume);
            return;
        }
        if(fromUser)
            mp.seekTo(progress);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        Log.i("Player comp","now player is "+mp.isPlaying());
        sb.setProgress(0);
        this.mp.stop();
        this.mp.release();
        currSong=(currSong+1)%songArray.length;
        setUpMp(currSong);
        tb.setBackgroundResource(R.drawable.play);
        tb.setChecked(false);
        status = false;
        th = null;
    }

    class MyThread implements Runnable
    {

        @Override
        public void run()
        {
            while(mp.isPlaying())
            {
                try
                {
                    Thread.sleep(1000);
                    if(mp.isPlaying()) {
                        final int currpos = mp.getCurrentPosition();
                        sb.setProgress(currpos);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                int sec=currpos/1000;
                                int min=sec/60;
                                sec=sec%60;
                                DecimalFormat formatter = new DecimalFormat("00");
                                String minFormat = formatter.format(min);
                                String secFormat=formatter.format(sec);
                                tv3.setText(minFormat+":"+secFormat);
                            }
                        });

                    }

                }
                catch(InterruptedException ex)
                {
                    Toast.makeText(MainActivity.this,ex.toString(), Toast.LENGTH_LONG).show();
                }
            }
        }

    }
}
