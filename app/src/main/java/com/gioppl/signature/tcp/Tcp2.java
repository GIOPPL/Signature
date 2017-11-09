package com.gioppl.signature.tcp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.gioppl.signature.R;

/**
 * Created by GIOPPL on 2017/11/5.
 */

public class Tcp2 extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.socket);
        new Thread(new Runnable() {
            @Override
            public void run() {
                connect();
            }
        }).start();
    }

    private void connect() {
//        SocketUtil su=new SocketUtil(this,"192.168.1.103",8088);
//        su.backMessage(new SocketUtil.MessageSocket() {
//            @Override
//            public void backMessage(String message) {
//                FinalValue.Companion.successMessage("成功收到消息"+message);
//            }
//        });

    }
}
