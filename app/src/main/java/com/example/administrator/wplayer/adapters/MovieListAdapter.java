package com.example.administrator.wplayer.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.wplayer.R;
import com.example.administrator.wplayer.models.MediaItem;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * 知其然，而后知其所以然
 * 倔强小指，成名在望
 * 作者： Tomato
 * on 2016/11/2 0002.
 * 功能、作用：
 */

public class MovieListAdapter extends BaseAdapter {
    private Context context;
    private List<MediaItem> list;


    public MovieListAdapter(Context context, List<MediaItem> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        int ret = 0;
        if (list != null){
            ret = list.size();
        }
        return ret;
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.movie_item,parent,false);
            convertView.setTag(new ViewHolder(convertView));
        }
        ViewHolder holder = (ViewHolder) convertView.getTag();
        MediaItem mediaItem = list.get(position);
        Picasso.with(context)
                .load(mediaItem.getImageUrl())
                .config(Bitmap.Config.ARGB_4444)
                .into(holder.mImageView);
        holder.txtTitle.setText(mediaItem.getName());
        holder.txtSummary.setText(mediaItem.getDesc());

        return convertView;
    }

    public static class ViewHolder {
        private SurfaceView mSurfaceView;
        private ImageView mImageView;
        private TextView txtTitle;
        private TextView txtSummary;


        public ViewHolder(View itemView) {
            mSurfaceView = (SurfaceView) itemView.findViewById(R.id.suf_play);
            mImageView = (ImageView) itemView.findViewById(R.id.img_cover);
            txtTitle = (TextView) itemView.findViewById(R.id.txt_title);
            txtSummary = (TextView) itemView.findViewById(R.id.txt_summary);
        }
    }
}
