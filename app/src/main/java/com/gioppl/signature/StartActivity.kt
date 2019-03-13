package com.gioppl.signature

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.WindowManager
import me.wangyuwei.particleview.ParticleView

/**
 * Created by GIOPPL on 2017/11/22.
 */
class StartActivity :AppCompatActivity(){
    var mParticleView:ParticleView?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.start_activity)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        ShareData.ReadData(this@StartActivity);
        ShareData.ReadSizeData(this@StartActivity)
        mParticleView= findViewById(R.id.pv_start) as ParticleView?
        mParticleView!!.startAnim()
        mParticleView!!.setOnParticleAnimListener(ParticleView.ParticleAnimListener {
            startActivity(Intent(this@StartActivity,MainActivity::class.java))
            finish();
        })

    }
}