package com.hen.tiktak;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.List;

public class GridViewAdapter extends BaseAdapter {

    List<Bitmap> lstUri;
    Context context;


    public GridViewAdapter(List<Bitmap> lstUri, Context context) {
        this.lstUri = lstUri;
        this.context = context;
    }



    @Override
    public int getCount() {
        return lstUri.size();
    }

    @Override
    public Object getItem(int i) {
        return lstUri.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.image_item, viewGroup, false);
        }
        ImageView imgView= (ImageView) view.findViewById(R.id.imgGrid);
      //  TextView txtView= (TextView) convertView.findViewById(R.id.txtView);

        imgView.setImageBitmap(lstUri.get(i));

     //   imgView.setImageResource(mThumbIds[position]);
    //    txtView.setText(mThumbNames [position]);
        return view ;
    }

    }

