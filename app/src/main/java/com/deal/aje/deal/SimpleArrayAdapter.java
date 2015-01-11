package com.deal.aje.deal;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by aje on 1/12/15.
 */
public class SimpleArrayAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final String[] item_arr;
    private final String[] desc_arr;
    private final List<byte[]> item_pic;


    public SimpleArrayAdapter(Context context, String[] item_arr, String[] desc_arr, List<byte[]> img) {
        super(context, R.layout.listrow_item, item_arr);
        this.context = context;
        this.item_arr = item_arr;
        this.desc_arr = desc_arr;
        this.item_pic = img;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.listrow_item, parent, false);
        TextView textView1 = (TextView) rowView.findViewById(R.id.firstLine);
        TextView textView2 = (TextView) rowView.findViewById(R.id.secondLine);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);

        if(position < item_arr.length) {
            textView1.setText(item_arr[position]);
            textView2.setText(desc_arr[position]);
            Log.d(Constants.TAG, "position:" + position + "itemsize:" + item_arr.length +" Pic size"+item_pic.size()+" desc size: "+desc_arr.length);
            if (position<item_pic.size() && item_pic.get(position) != null) {
                int x = item_pic.get(position).length;
                Bitmap bm = BitmapFactory.decodeByteArray(item_pic.get(position), 0, x);
                imageView.setImageBitmap(bm);
            }
        }
        return rowView;
    }

}
