package com.GuoGuo.JuicyChat.model;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

import com.GuoGuo.JuicyChat.R;

import io.rong.imkit.RongExtension;
import io.rong.imkit.plugin.IPluginModule;

/**
 * Created by chenshuai12619 on 2017-12-29.
 */

public class VideoFilePlugin implements IPluginModule {
    
    public static VideoFilePlugin videoFilePlugin;
    
    public static VideoFilePlugin getInstance() {
        if (videoFilePlugin == null) {
            synchronized (VideoFilePlugin.class) {
                if (videoFilePlugin == null) {
                    videoFilePlugin = new VideoFilePlugin();
                }
            }
        }
        return videoFilePlugin;
    }
    
    @Override
    public Drawable obtainDrawable(Context context) {
        return ContextCompat.getDrawable(context, R.drawable.selector_video_file);
    }
    
    @Override
    public String obtainTitle(Context context) {
        return "视频文件";
    }
    
    @Override
    public void onClick(Fragment fragment, RongExtension rongExtension) {
    
    }
    
    @Override
    public void onActivityResult(int i, int i1, Intent intent) {
    
    }
}
