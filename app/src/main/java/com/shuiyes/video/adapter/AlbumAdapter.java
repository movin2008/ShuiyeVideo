package com.shuiyes.video.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.shuiyes.video.R;
import com.shuiyes.video.bean.Album;
import com.shuiyes.video.bean.ListVideo;
import com.shuiyes.video.constants.ResourceDef;
import com.shuiyes.video.util.ImageLoader;
import com.shuiyes.video.util.PlayUtils;
import com.shuiyes.video.youku.YoukuUtils;
import com.shuiyes.video.widget.NumberView;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagView;
import com.zhy.view.flowlayout.TagFlowLayout;

public class AlbumAdapter extends BaseAdapter {

    private ImageLoader mImageLoader = new ImageLoader();
    private List<Album> mAlbums = new ArrayList<Album>();

    public void listAlbums(List<Album> albums) {
        mAlbums.clear();
        mAlbums.addAll(albums);
        this.notifyDataSetChanged();
    }

    private Context context;
    private Handler handler;


    public AlbumAdapter(Context context, Handler handler) {
        this.context = context;
        this.handler = handler;
    }

    @Override
    public int getCount() {
        return mAlbums.size();
    }

    @Override
    public Album getItem(int position) {
        return mAlbums.get(position);
    }

    @Override
    public long getItemId(int arg0) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view;
        ViewHolder viewHolder;
        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.itemview_search_video, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.img = (ImageView) view.findViewById(R.id.album_image);
            viewHolder.title = (TextView) view.findViewById(R.id.album_title);
            viewHolder.summary = (TextView) view.findViewById(R.id.album_summary);
            viewHolder.album = (TagFlowLayout) view.findViewById(R.id.album_list);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }

        final Album album = mAlbums.get(position);
        view.setId(ResourceDef.ID_SEARCH_VIDEO + position);
        view.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                PlayUtils.play(context, album);
            }
        });

        viewHolder.title.setText(album.getTitle());
        viewHolder.summary.setText(album.getSummary());

        viewHolder.img.setTag(album.getImgurl());
        viewHolder.img.setImageResource(R.drawable.youku);
        Bitmap bitmap = mImageLoader.getBitmap(album.getImgurl(), handler);
        if (bitmap != null) {
            viewHolder.img.setImageBitmap(bitmap);
        }

        viewHolder.album.setAdapter(new TagAdapter<ListVideo>(album.getListVideos()) {
            @Override
            public TagView getView(FlowLayout parent, int position, final ListVideo t) {
                NumberView view = new NumberView(context, t);
                view.setSize(view.measureWidth(), 100);

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        PlayUtils.play(context, t.getUrl(), t.getTitle());
                    }
                });

                return view;
            }
        });

        return view;
    }

    class ViewHolder {
        ImageView img;
        TextView title;
        TextView summary;
        TagFlowLayout album;
    }

}
