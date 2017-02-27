package com.chomp.wifistorymachine.adapter;


import android.util.SparseArray;
import android.view.View;

/**
 * Created by ronglinbo on 2016/8/23.
 */
public class ViewHolder {

    @SuppressWarnings("unchecked")
    public static <T extends View> T getView(View convertView, int id) {
        SparseArray<View> holder = (SparseArray<View>) convertView.getTag();
        if( holder == null ) {
            holder = new SparseArray<View>();
            convertView.setTag(holder);
        }

        View view = holder.get(id);
        if( view == null ) {
            view = convertView.findViewById(id);
            holder.put(id, view);
        }
        return (T)view;
    }
}
