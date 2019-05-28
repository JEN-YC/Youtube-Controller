package com.miller.phone_controll;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class mfragment extends Fragment {
    ImageButton raise, down, mute, replay, stop, next, play, fullscreen;
    SeekBar volume_setting;
    TextView curr_volume;
//    static final String RAISE = "2";
//    static final String DOWN = "3";
    static final String VOLUME = "2";
    static final String REPLAY = "4";
    static final String STOP = "5";
    static final String NEXT = "6";
    static final String PLAY = "7";
    static final String FULLSCREEN= "8";
//    static final String MUTE = "9";
    static Boolean isFullScreen = false;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.activity_fragment, container, false);
        curr_volume = contentView.findViewById(R.id.curr_volume);
        replay = contentView.findViewById(R.id.replay);
        stop = contentView.findViewById(R.id.pause);
        next = contentView.findViewById(R.id.next);
        play = contentView.findViewById(R.id.play);
        fullscreen = contentView.findViewById(R.id.fullscreen);
        volume_setting = contentView.findViewById(R.id.volume_setting);
        processControllers();
        //  raise = contentView.findViewById(R.id.louder);
        //  down = contentView.findViewById(R.id.quieter);
        //  mute = contentView.findViewById(R.id.mute);

        return contentView;
    }

    private void processControllers(){

//        mute.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Map<String, String> map = new HashMap<>();
//                map.put("command", MUTE);
//                JSONObject json = new JSONObject(map);
//                String jsonString = json.toString();
//                Connected_page.writeClass.write_msg(jsonString.getBytes());
//            }
//        });
//        raise.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Map<String, String> map = new HashMap<>();
//                map.put("command", RAISE);
//                JSONObject json = new JSONObject(map);
//                String jsonString = json.toString();
//                Connected_page.writeClass.write_msg(jsonString.getBytes());
//            }
//        });
//
//        down.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Map<String, String> map = new HashMap<>();
//                map.put("command", DOWN);
//                JSONObject json = new JSONObject(map);
//                String jsonString = json.toString();
//                Connected_page.writeClass.write_msg(jsonString.getBytes());
//            }
//        });
        volume_setting.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                curr_volume.setText("目前音量：" + progress + " / 100 ");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {


            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                Map<String, String> map = new HashMap<>();
                map.put("command", VOLUME);
                map.put("value", String.valueOf(progress));
                JSONObject json = new JSONObject(map);
                String jsonString = json.toString();
                Connected_page.writeClass.write_msg(jsonString.getBytes());
            }
        });
        replay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, String> map = new HashMap<>();
                map.put("command", REPLAY);
                JSONObject json = new JSONObject(map);
                String jsonString = json.toString();
                Connected_page.writeClass.write_msg(jsonString.getBytes());
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, String> map = new HashMap<>();
                map.put("command", STOP);
                JSONObject json = new JSONObject(map);
                String jsonString = json.toString();
                Connected_page.writeClass.write_msg(jsonString.getBytes());
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, String> map = new HashMap<>();
                map.put("command", NEXT);
                JSONObject json = new JSONObject(map);
                String jsonString = json.toString();
                Connected_page.writeClass.write_msg(jsonString.getBytes());
            }
        });

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, String> map = new HashMap<>();
                map.put("command", PLAY);
                JSONObject json = new JSONObject(map);
                String jsonString = json.toString();
                Connected_page.writeClass.write_msg(jsonString.getBytes());
            }
        });

        fullscreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, String> map = new HashMap<>();
                map.put("command", FULLSCREEN);
                JSONObject json = new JSONObject(map);
                String jsonString = json.toString();
                Connected_page.writeClass.write_msg(jsonString.getBytes());
                if (isFullScreen){
                    isFullScreen = false;
                    fullscreen.setImageResource(R.drawable.fullscreen);
                }
                else{
                    isFullScreen = true;
                    fullscreen.setImageResource(R.drawable.exit_fullscreen);
                }
            }
        });



    }
}
