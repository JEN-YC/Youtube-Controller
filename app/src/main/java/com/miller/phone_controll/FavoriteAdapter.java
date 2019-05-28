package com.miller.phone_controll;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.List;

class FavoriteAdapter extends BaseAdapter {
    private List<VideoPojo> list;
    private Context mcontext;
    private LayoutInflater inflater;

    public FavoriteAdapter(Context context, List<VideoPojo> mlist) {
        this.list = mlist;
        this.mcontext = context;
        this.inflater = LayoutInflater.from(mcontext);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.favorite_list_row, null);

            holder.thumbnail = convertView
                    .findViewById(R.id.favorite_thumbnail);
            holder.title = convertView
                    .findViewById(R.id.favorite_title);
            holder.remove = convertView
                    .findViewById(R.id.cancel);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final VideoPojo searchResult = list.get(position);

        Picasso.get()
                .load(searchResult.getThumbnailURL()).into(holder.thumbnail);
        holder.title.setText(searchResult.getTitle());
        holder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Connected_page.favorite_database.delete(searchResult.getId());
                Toast.makeText(mcontext,"刪除成功",Toast.LENGTH_SHORT).show();
                Connected_page.favorite_list.remove(searchResult);
                Connected_page.favoriteAdapter.notifyDataSetChanged();
            }
        });
        return convertView;
    }

    public class ViewHolder {
        ImageView thumbnail;
        TextView title;
        Button remove;
    }

}