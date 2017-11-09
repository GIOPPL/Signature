package com.gioppl.signature.tcp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by GIOPPL on 2017/11/4.
 */

public class TcpController extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Socket socket = new Socket("192.168.1.103", 8088);
                    ServerThread serverThread = new ServerThread(socket);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}

