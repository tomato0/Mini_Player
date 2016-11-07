package com.example.administrator.wplayer.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.wplayer.R;
import com.example.administrator.wplayer.models.LocalVideoData;

import java.util.List;

/**
 * 知其然，而后知其所以然
 * 倔强小指，成名在望
 * 作者： Tomato
 * on 2016/10/20 0020.
 * com.example.administrator.wplayer.adapters
 * 功能、作用：ListView   Adapter
 */
public class LocalVideoListAdapter extends BaseAdapter {
    private Context context;
    private List<LocalVideoData> videoData;

    public LocalVideoListAdapter(Context context, List<LocalVideoData> videoData) {
        this.context = context;
        this.videoData = videoData;
    }

    @Override
    public int getCount() {
        int ret = 0;
        if (videoData != null){
            ret = videoData.size();
        }
        return ret;
    }

    @Override
    public Object getItem(int position) {
        return videoData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.local_video_item_list,parent,false);
            convertView.setTag(new ViewHolder(convertView));
        }

        ViewHolder holder = (ViewHolder) convertView.getTag();
        holder.img.setImageBitmap(videoData.get(position).getBitmap());
        holder.name.setText(videoData.get(position).getName());
        holder.time.setText(videoData.get(position).getDuration());
        String s = String.valueOf(videoData.get(position).getSize());
        holder.size.setText(s.substring(0,s.indexOf(".")+2)+"M");
        return convertView;
    }



    public static class ViewHolder{
        private ImageView img;
        private TextView name;
        private TextView time;
        private TextView size;
        public ViewHolder(View itemView){
            img = (ImageView) itemView.findViewById(R.id.local_video_img);
            name = (TextView) itemView.findViewById(R.id.local_video_name);
            time = (TextView) itemView.findViewById(R.id.local_video_time);
            size = (TextView) itemView.findViewById(R.id.local_video_size);
        }
    }
}
