package com.arialean.administrator.musicplay;

import android.media.MediaPlayer;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ImageButton pauseButton, stopButton, returnButton,
                            nextButton, fonteButton;
    private MediaPlayer musicPlay;
    private  SeekBar seekBar;
    private TextView startTimeView, allTimeView;
    private ArrayList<String> musicpath = new ArrayList<>();
    Handler handler;
    Thread thread;
    private boolean bl = false;
    private  int counter = 0;
    private int totalTime = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        returnButton = (ImageButton) findViewById(R.id.returnButton);
        fonteButton = (ImageButton) findViewById(R.id.fonteButton);
        pauseButton = (ImageButton) findViewById(R.id.pastaButton);
        nextButton = (ImageButton) findViewById(R.id.nextButton);
        stopButton = (ImageButton) findViewById(R.id.stopButton);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        startTimeView = (TextView) findViewById(R.id.startTimetext);
        allTimeView = (TextView) findViewById(R.id.allTimetext);

        //获取SD卡权限
        ActivityCompat.requestPermissions(this,
                new String[] {android.Manifest.permission.READ_EXTERNAL_STORAGE,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                124);
        File file = new File("/storage/emulated/0/kgmusic/download/");
        File[] files = file.listFiles();

        for (int i = 0; i < files.length; i++){
            File fPath = files[i];
            String str = fPath.getPath();
            musicpath.add(str);
        }
        musicPlay = new MediaPlayer();
        try {
            musicPlay.setDataSource(musicpath.get(counter));
            musicPlay.prepare();
        }catch (IOException a){

        }

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser == true){
                    musicPlay.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                int time = msg.arg1;
                totalTime = msg.arg2;
                seekBar.setMax(totalTime);
                seekBar.setProgress(time);

                startTimeView.setText(fenZhong(miaoZhong(time)) + ":" + miao(miaoZhong(time)));
                allTimeView.setText(fenZhong(miaoZhong(totalTime)) + ":" + miao(miaoZhong(totalTime)));
                if(startTimeView.getText().equals(allTimeView.getText())) {
                    counter += 1;
                    if (counter>musicpath.size() - 1){
                        counter = 0;
                    }
                    try {
                        musicPlay.reset();
                        musicPlay.setDataSource(musicpath.get(counter));
                        musicPlay.prepare();
                        musicPlay.start();

                    }catch (IOException e){}

                    }
                }
        };

        thread = new Thread(){
            @Override
            public void run() {
                super.run();

                int cun = 0;
                for (;;){
                    cun = musicPlay.getCurrentPosition();
                    totalTime = musicPlay.getDuration();
                    Message message = new Message();
                    message.arg1 = cun;
                    message.arg2 = totalTime;
                    handler.sendMessage(message);
                    try {

                        sleep(1000);
                    }catch (InterruptedException e){

                    }
                }
            }
        };
        thread.start();

        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (musicPlay.isPlaying()){
                    musicPlay.seekTo(0);
                    musicPlay.start();
                }else {

                }
            }
        });

        fonteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                counter--;
                if (counter < 0){
                    counter = musicpath.size() - 1;
                }
                try {
                    musicPlay.reset();
                    musicPlay.setDataSource(musicpath.get(counter));
                    musicPlay.prepare();
                }catch (IOException a){

                }
                musicPlay.start();
            }
        });

        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!bl && musicPlay != null) {
                    pauseButton.setImageResource(R.drawable.start1);
                    musicPlay.start();
                    bl = true;
                    return;
                }else if(bl) {
                    pauseButton.setImageResource(R.drawable.pause1);
                    musicPlay.pause();
                    bl = false;
                    return;
                }else {

                }
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                counter++;
                if (counter  > musicpath.size() - 1){
                    counter = 0;
                }
                try {
                    musicPlay.reset();
                    musicPlay.setDataSource(musicpath.get(counter));
                    musicPlay.prepare();
                }catch (IOException a){

                }
                musicPlay.start();
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (musicPlay.isPlaying()){
                    musicPlay.stop();
                }
            }
        });


    }
    //秒钟转换为分钟
    public int fenZhong(int miaoZhong){
        return miaoZhong / 60;
    }
    //毫秒转换为秒
    public int miaoZhong(int haoMiao) {
        return  haoMiao / 1000;
    }
    //毫秒转换为分钟
    public int haoFen(int haoMiao){
        return haoMiao / 1000 / 60;
    }
    //分秒中求秒
    public  int miao(int miaoZhong){
        return miaoZhong % 60;
    }
}