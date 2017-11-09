package com.gioppl.signature

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.facebook.drawee.view.SimpleDraweeView

/**
 * 左侧画廊的适配器
 * Created by GIOPPL on 2017/10/28.
 */
class ImageAdapt(private var mContext:Context,private var mList:ArrayList<String>,var adaptOption: AdaptOption) : RecyclerView.Adapter<ImageAdapt.ImageViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int)= ImageViewHolder(LayoutInflater.from(mContext).inflate(R.layout.image_item,parent,false))

    override fun onBindViewHolder(holder: ImageViewHolder?, position: Int) {
        holder!!.sim_imageItem!!.setImageURI("file://"+mList[position])
        holder.tv_imageItem!!.text=split(mList[position])
        holder.im_delete!!.setOnClickListener(View.OnClickListener {
            adaptOption.deleteImage(mList[position],position)
        })
        holder.sim_imageItem!!.setOnClickListener(View.OnClickListener {adaptOption.loadImage(mList[position],position)})
    }

    override fun getItemCount()=mList.size

    class ImageViewHolder(item:View): RecyclerView.ViewHolder(item) {
        var sim_imageItem:SimpleDraweeView?=null
        var tv_imageItem:TextView?=null
        var im_delete:ImageView?=null
        init {
            sim_imageItem=item.findViewById(R.id.sim_imageItem)
            tv_imageItem=item.findViewById(R.id.tv_imageItem_name)
            im_delete=item.findViewById(R.id.im_imageItem_delete)
        }
    }
    private fun split(path:String):String{
        var nameList=path.split("/")
        return nameList[nameList.size-1]
    }

    public interface AdaptOption{
        fun deleteImage(path: String,position:Int)
        fun loadImage(path : String,position: Int)
    }

}