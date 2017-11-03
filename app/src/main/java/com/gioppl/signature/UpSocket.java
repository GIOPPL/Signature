package com.gioppl.signature;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by GIOPPL on 2017/11/3.
 */

public class UpSocket {
    private Socket socket = null;
    private BufferedReader in = null;
    private PrintWriter out = null;
    private static final String HOST = "10.0.2.2";
    private static final int PORT = 9999;
    UpSocket(String s){
        try {
            socket = new Socket(HOST, PORT);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
        } catch (IOException ex) {
            ex.printStackTrace();
//            ShowDialog("login exception" + ex.getMessage());
        }
    }
}
