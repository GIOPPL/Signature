package com.gioppl.signature.tcp;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.gioppl.signature.FinalValue;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Socket  建立与服务端的交互
 * Created by Administrator on 2017/4/22.
 */
public class SocketUtil {

    private Context context;
    private Socket clientSocket;
    private String ipStr = "192.168.1.103";
    private int ipStrProt = 8088;
    private BufferedWriter bfWriter;
    private MessageSocket messageSocket;

    public SocketUtil(Context context, String ipStr, int ipStrPort,MessageSocket messageSocket) {
        this.messageSocket=messageSocket;
        this.context = context;
        this.ipStr = ipStr;
        this.ipStrProt = ipStrPort;
        if (isIP(ipStr) && ipStrProt > 0 && ipStrProt < 1000000) {
            connect();
        } else {
            Toast.makeText(context, "IP或者端口号有误", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 建立Socket连接
     */
    private void connect() {
        AsyncTask<Void, String, Void> reader = new AsyncTask<Void, String, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    clientSocket = new Socket(ipStr, ipStrProt);
                    //客户端建立与服务端socket的连接,"10.62.37.152"为我的局域网ip地址,读者按照自己的ip地址进行相应修改
                    bfWriter = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
                    publishProgress("success");
                    while (clientSocket.isConnected() && !clientSocket.isClosed()) {//isConnected 代表是否连接成功过
                        InputStream isRead = clientSocket.getInputStream();
                        // 缓冲区
                        byte[] buffer = new byte[isRead.available()];
                        // 读取缓冲区
                        isRead.read(buffer);
                        // 转换为字符串
                        String responseInfo = new String(buffer);
                        // 日志中输出
                        if (!responseInfo.equals("")) {
                            publishProgress(responseInfo);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    publishProgress("链接建立失败" + "--" + e.toString());
                    Log.e("SocketActivity", e.toString());
                }
                return null;
            }

            @Override
            protected void onProgressUpdate(String... values) {
                if (values[0].equals("success")) {
                    Toast.makeText(context, "链接建立成功", Toast.LENGTH_SHORT).show();
                    messageSocket.connectSuccess(bfWriter);
                } else {
                    Log.e("Socket Server", values[0]);
                    messageSocket.backMessage(values[0]);
                }

                super.onProgressUpdate(values);
            }
        };
        reader.execute();
    }

    //循环发送
    public void sendRound(final String msg) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    sendOption(msg);
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }


    /**
     * 发送消息给服务端
     *
     * @param message 发送内容
     */
    public void sendOption(String message) {
        try {
            bfWriter.write(message + "\n");
            bfWriter.flush();
            FinalValue.Companion.successMessage("成功发送" + message);
        } catch (IOException e) {
            Toast.makeText(context, "无法建立连接", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    public interface MessageSocket {
        void connectSuccess(BufferedWriter bfWriter);
        void backMessage(String message);
    }

    /**
     * 判断是否为正确的IP地址
     *
     * @param addr IP地址
     * @return 是否为IP地址
     */
    private boolean isIP(String addr) {
        if (addr.length() < 7 || addr.length() > 15 || "".equals(addr)) {
            return false;
        }
        /**
         * 判断IP格式和范围
         */
        String rexp = "([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}";
        Pattern pat = Pattern.compile(rexp);
        Matcher mat = pat.matcher(addr);
//        boolean ipAddress = mat.find();
        return mat.find();
    }

}
