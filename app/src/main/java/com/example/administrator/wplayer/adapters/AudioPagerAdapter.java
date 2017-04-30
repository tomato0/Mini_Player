package com.example.administrator.wplayer.adapters;

import android.content.Context;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.wplayer.R;
import com.example.administrator.wplayer.models.MediaItem;
import com.example.administrator.wplayer.utils.Utils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者：wangshaoqiang on 2016/7/18 10:16
 * 作用：localAudio的适配器
 */
public class AudioPagerAdapter extends BaseAdapter {

    private final boolean isVideo;
    private Context context;
    private final List<MediaItem> mediaItems;
    private Utils utils;

    public AudioPagerAdapter(Context context, List<MediaItem> mediaItems, boolean isVideo){
        this.context = context;
        this.mediaItems = mediaItems;
        this.isVideo = isVideo;
        utils = new Utils();
    }

    @Override
    public int getCount() {
        return mediaItems.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHoder viewHoder;
        if(convertView ==null){
            convertView = View.inflate(context, R.layout.item_audio_pager,null);
            viewHoder = new ViewHoder();
            viewHoder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
            viewHoder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            viewHoder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
            viewHoder.tv_size = (TextView) convertView.findViewById(R.id.tv_size);

            convertView.setTag(viewHoder);
        }else{
            viewHoder = (ViewHoder) convertView.getTag();
        }

        //根据position得到列表中对应位置的数据
        MediaItem mediaItem = mediaItems.get(position);
        viewHoder.tv_name.setText(mediaItem.getName());
        if (mediaItem.getDuration() == 0){
            viewHoder.tv_size.setVisibility(View.GONE);
            viewHoder.tv_time.setText(mediaItem.getArtist());
        }else {
            viewHoder.tv_size.setText(Formatter.formatFileSize(context, mediaItem.getSize()));
            viewHoder.tv_time.setText(utils.stringForTime((int) mediaItem.getDuration()));
        }

        if(!isVideo){
            //音频
            if (mediaItem.getImageUrl() == null){
                viewHoder.iv_icon.setImageResource(R.drawable.music_default_bg);
            }else {
                Picasso.with(context)
                        .load(mediaItem.getImageUrl())
                        .into(viewHoder.iv_icon);
            }
        }

        return convertView;
    }


    static class ViewHoder{
        ImageView iv_icon;
        TextView tv_name;
        TextView tv_time;
        TextView tv_size;
    }

}

