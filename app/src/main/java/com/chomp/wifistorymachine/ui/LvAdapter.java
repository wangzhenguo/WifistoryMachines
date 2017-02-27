package com.chomp.wifistorymachine.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chomp.wifistorymachine.R;
import com.chomp.wifistorymachine.constants.Constant;
import com.chomp.wifistorymachine.dao.AudiobooksSort;
import com.chomp.wifistorymachine.util.Toaster;

import java.util.ArrayList;

import static com.chomp.wifistorymachine.R.id.tvName;


public class LvAdapter extends BaseAdapter implements View.OnClickListener {

    private ArrayList<AudiobooksSort> lvItemBeanList;

// 布局加载器
    private LayoutInflater mInflater;
    // 上下文
    private Context context;

// 布局缓存对象

    private ViewHolder holder;

    private SharedPreferences preferences;
    boolean isLoggedIn = true;
    String PINCODE_ID;
//记录当前展开项的索引

    private int expandPosition = -1;

    public LvAdapter(ArrayList<AudiobooksSort> lvItemBeanList, Context context) {
        super();
        this.lvItemBeanList = lvItemBeanList;
        this.context = context;
        mInflater = LayoutInflater.from(context);


        preferences = context.getSharedPreferences(Constant.SHARED_KARROBOT, Context.MODE_PRIVATE);
        PINCODE_ID = preferences.getString(Constant.EXTRA_PINCODE_ID, null);
        if (PINCODE_ID == null || ("0").equals(PINCODE_ID) || PINCODE_ID.equals("")) {
            isLoggedIn = false;
        }

    }

    @Override

    public int getCount() {
        return null == lvItemBeanList ? 0 : lvItemBeanList.size();

    }

    @Override
    public Object getItem(int position) {
        return lvItemBeanList.get(position);

    }

    @Override

    public long getItemId(int position) {

        return position;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (null == convertView) {

            convertView = mInflater.inflate(R.layout.activity_story_sort_item, null);

            holder = new ViewHolder();

            holder.img_icon = (ImageView) convertView.findViewById(R.id.img_icon);
            holder.tv1 = (TextView) convertView.findViewById(tvName);


            holder.img_arrow1 = (ImageView) convertView.findViewById(R.id.img_arrow1);

            holder.Linear_arrow1= (LinearLayout) convertView.findViewById(R.id.Linear_arrow1);
            holder.btn_audition_item= (RelativeLayout) convertView.findViewById(R.id.btn_audition_item);
            holder.btn_OnDemand_item= (RelativeLayout) convertView.findViewById(R.id.btn_OnDemand_item);
            holder.btn_download_item= (RelativeLayout) convertView.findViewById(R.id.btn_download_item);
            holder.btn_like_item= (RelativeLayout) convertView.findViewById(R.id.btn_like_item);



            convertView.setTag(holder);

        } else {

            holder = (ViewHolder) convertView.getTag();



        }

        AudiobooksSort lvItemBean = lvItemBeanList.get(position);
        holder.tv1.setText(lvItemBean.getFiletitle());

        if (null != lvItemBean) {

            holder.img_arrow1.setOnClickListener(new OnLvItemClickListener(position));


            holder.btn_audition_item.setOnClickListener(this);
            holder.btn_OnDemand_item.setOnClickListener(this);
            holder.btn_download_item.setOnClickListener(this);
            holder.btn_like_item.setOnClickListener(this);



//如果点击的是当前项，则将其展开，否则将其隐藏

            if (expandPosition == position) {

                holder.Linear_arrow1.setVisibility(View.VISIBLE);

            } else {

                holder.Linear_arrow1.setVisibility(View.GONE);

            }
        }
        return convertView;
    }

    class OnLvItemClickListener implements View.OnClickListener {
        private int position;
        public OnLvItemClickListener(int position) {

            super();

            this.position = position;

        }

        @Override
        public void onClick(View v) {

//如果当前项为展开，则将其置为-1，目的是为了让其隐藏，如果当前项为隐藏，则将当前位置设置给全局变量，让其展开，这也就是借助于中间变量实现布局的展开与隐藏

            if(expandPosition == position){

                expandPosition = -1;

            }else{

                expandPosition = position;

            }

            notifyDataSetChanged();

        }
    }


    class ViewHolder {

        ImageView img_icon;
        TextView tv1;

        ImageView img_arrow1;

        LinearLayout Linear_arrow1;

        RelativeLayout btn_audition_item;
        RelativeLayout btn_OnDemand_item;
        RelativeLayout btn_download_item;
        RelativeLayout btn_like_item;
    }

    @Override
    public void onClick(View v) {
         switch (v.getId()){
             case R.id.btn_audition_item:

                 if (isLoggedIn) {
                    // play(lvItemBeanList.get(position).getFileid().toString(), lvItemBeanList.get(position).getUrl(), PINCODE_ID);
                   //  Play_Name = lvItemBeanList.get(position).getFiletitle();
                 } else {
                     Toaster.showLongToast(context, context.getString(R.string.un_binding_fail));
                 }
                 break;
             case R.id.btn_OnDemand_item:

                 break;
             case R.id.btn_download_item:

                 break;
             case R.id.btn_like_item:

                 break;
         }
    }
}