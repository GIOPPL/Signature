package com.gioppl.signature;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

/**
 * Created by GIOPPL on 2017/10/30.
 */

public class UploadUtil {
    private static final String TAG = "uploadFile";
    private static final int TIME_OUT = 10*1000;   //超时时间
    private static final String CHARSET = "utf-8"; //设置编码
    private  UpImageSuccessful upImageSuccessful;
    private Context context;

    public UploadUtil(final File file, final String RequestURL, final Context context, final UpImageSuccessful upImageSuccessful){
        new Thread(new Runnable() {
            @Override
            public void run() {
                start( file,  RequestURL,  context,   upImageSuccessful);
            }
        }).start();
    }

    /**
     * android上传文件到服务器
     * @param file  需要上传的文件
     * @param RequestURL  请求的rul
     * @return  返回响应的内容
     */
    public void start(File file, String RequestURL, Context context, final UpImageSuccessful upImageSuccessful){
        this.context=context;
        this.upImageSuccessful=upImageSuccessful;
        FinalValue.Companion.successMessage("上传地址为"+RequestURL);
        String result = null;
        String  BOUNDARY =  UUID.randomUUID().toString();  //边界标识   随机生成
        String PREFIX = "--" , LINE_END = "\r\n";
        String CONTENT_TYPE = "multipart/form-data";   //内容类型
        Message msg=new Message();
        try {
            URL url = new URL(RequestURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(TIME_OUT);
            conn.setConnectTimeout(TIME_OUT);
            conn.setDoInput(true);  //允许输入流
            conn.setDoOutput(true); //允许输出流
            conn.setUseCaches(false);  //不允许使用缓存
            conn.setRequestMethod("POST");  //请求方式
            conn.setRequestProperty("Charset", CHARSET);  //设置编码
            conn.setRequestProperty("connection", "keep-alive");
            conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary=" + BOUNDARY);
            conn.connect();

            if(file!=null){
                /**
                 * 当文件不为空，把文件包装并且上传
                 */
                DataOutputStream dos = new DataOutputStream( conn.getOutputStream());
                StringBuffer sb = new StringBuffer();
                sb.append(PREFIX);
                sb.append(BOUNDARY);
                sb.append(LINE_END);
                /**
                 * 这里重点注意：
                 * name里面的值为服务器端需要key   只有这个key 才可以得到对应的文件
                 * filename是文件的名字，包含后缀名的   比如:abc.png
                 */

                sb.append("Content-Disposition: form-data; name=\"img\"; filename=\""+file.getName()+"\""+LINE_END);
                sb.append("Content-Type: application/octet-stream; charset="+CHARSET+LINE_END);
                sb.append(LINE_END);
                dos.write(sb.toString().getBytes());
                InputStream is = new FileInputStream(file);
                byte[] bytes = new byte[1024];
                int len = 0;
                while((len=is.read(bytes))!=-1){
                    dos.write(bytes, 0, len);
                }
                is.close();
                dos.write(LINE_END.getBytes());
                byte[] end_data = (PREFIX+BOUNDARY+PREFIX+LINE_END).getBytes();
                dos.write(end_data);
                dos.flush();
                /**
                 * 获取响应码  200=成功
                 * 当响应成功，获取响应的流
                 */
                int res = conn.getResponseCode();

                if(res==200){
                    InputStream input =  conn.getInputStream();
                    StringBuffer sb1= new StringBuffer();
                    int ss ;
                    while((ss=input.read())!=-1){
                        sb1.append((char)ss);
                    }
                    msg.arg1=0x8;
                    handler.sendMessage(msg);
                }else {

                    msg.arg1=0x9;
                    handler.sendMessage(msg);
                }

            }
        } catch (MalformedURLException e) {
            msg.arg1=0x9;
            handler.sendMessage(msg);
        } catch (IOException e) {
            msg.arg1=0x9;
            handler.sendMessage(msg);
        }
    }

    public interface UpImageSuccessful{
        void uploadImageSuccessful();
        void uploadImageError();
    }


    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.arg1==0x8){
                FinalValue.Companion.toast(context,"上传成功");
                upImageSuccessful.uploadImageSuccessful();
            }else if(msg.arg1==0x9){
                FinalValue.Companion.toast(context,"上传失败");
                upImageSuccessful.uploadImageError();
            }
        }
    };
}
