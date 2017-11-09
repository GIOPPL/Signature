package com.gioppl.signature.tcp;

import com.gioppl.signature.FinalValue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by GIOPPL on 2017/11/4.
 */

public class ServerThread implements Runnable {
    Socket s=null;
    BufferedReader br=null;
    public ServerThread(Socket s)throws IOException{
        this.s=s;
        br=new BufferedReader(new InputStreamReader(s.getInputStream(),"utf-8"));

    }
    @Override
    public void run() {
        String content=null;
        while ((content=readFromClient())!=null){
            try {
                OutputStream os=s.getOutputStream();
                os.write((content+"\n").getBytes("utf-8"));
                FinalValue.Companion.successMessage(content);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
    private String readFromClient(){
        try {
            return br.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}

