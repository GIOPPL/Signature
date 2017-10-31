package com.gioppl.signature;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;

/**
 * Created by GIOPPL on 2017/4/3.
 */

public class InitApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this);//为了Facebook的图片库Fresco的初始化

    }
    /**
 *							  				佛祖保佑          永无BUG
 *							                      _ooOoo_
 *							                     o8888888o
 *							           			 88" . "88
 *							                     (| -_- |)
 *							           			 O\  =  /O
 *							                  ____/`---'\____
 *							      			.'  \\|     |//  `.
 *							     		   /  \\|||  :  |||//  \
 *							    		  /  _||||| -:- |||||-  \
 *							              |   | \\\  -  /// |   |
 *							              | \_|  ''\---/''  |   |
 *							              \  .-\__  `-`  ___/-. /
 *							            ___`. .'  /--.--\  `. . __
 *							         ."" '<  `.___\_<|>_/___.'  >'"".
 *							        | | :  `- \`.;`\ _ /`;.`/ - ` : | |
 *							        \  \ `-.   \_ __\ /__ _/   .-` /  /
 *							   ======`-.____`-.___\_____/___.-`____.-'======
 *							                      `=---='
 *							   ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
 *
 */
 }
