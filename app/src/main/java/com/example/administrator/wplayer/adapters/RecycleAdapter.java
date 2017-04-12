package com.example.administrator.wplayer.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.wplayer.R;
import com.example.administrator.wplayer.models.LocalVideoData;
import com.example.administrator.wplayer.models.MediaItem;

import java.util.Formatter;
import java.util.List;

/**
 * 知其然，而后知其所以然
 * 倔强小指，成名在望
 * 作者： Tomato
 * on 2016/10/20 0020.
 * com.example.administrator.wplayer.adapters
 * 功能、作用：RecycleView Adapter    需要自己定义监听，接口
 */
public class RecycleAdapter extends RecyclerView.Adapter implements View.OnClickListener {
    private Context context;
    private List<MediaItem> videoData;
    private RecyclerView recyclerView;
    private OnChildVideoClickListener listener;

    public void setChildClickListener(OnChildVideoClickListener listener) {
        this.listener = listener;
    }

    public RecycleAdapter(Context context, List<MediaItem> videoData) {
        this.context = context;
        this.videoData = videoData;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        this.recyclerView = null;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView img;
        private TextView name;
        private TextView time;
        private TextView size;
        public ViewHolder(View itemView) {
            super(itemView);
            img = (ImageView) itemView.findViewById(R.id.local_video_img);
            name = (TextView) itemView.findViewById(R.id.local_video_name);
            time = (TextView) itemView.findViewById(R.id.local_video_time);
            size = (TextView) itemView.findViewById(R.id.local_video_size);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.local_video_item_list, parent, false);
        view.setOnClickListener(this);
        return new ViewHolder(view);
    }

    @Override
    public void onClick(View v) {
        int position = recyclerView.getChildAdapterPosition(v);
        listener.onChildClickListener(recyclerView,v,position,videoData.get(position));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.img.setImageBitmap(videoData.get(position).getBitmap());
        viewHolder.name.setText(videoData.get(position).getName());
        viewHolder.time.setText(String.valueOf(videoData.get(position).getDuration()));
        viewHolder.size.setText(android.text.format.Formatter.formatFileSize(context,videoData.
                get(position).getSize()));
    }

    @Override
    public int getItemCount() {
        int ret = 0;
        if (videoData != null){
            ret = videoData.size();
        }
        return ret;
    }

    //监听接口
    public interface OnChildVideoClickListener{
        void  onChildClickListener(RecyclerView recyclerView,View itemView,int position,MediaItem videoData);
    }
}
