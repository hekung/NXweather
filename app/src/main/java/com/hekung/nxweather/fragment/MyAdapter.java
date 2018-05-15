package com.hekung.nxweather.fragment;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hekung.nxweather.R;

import java.util.List;

/**
 * Created by wenzh on 2018/5/10.
 */


public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder>{
    public List<String> datas = null;
    private OnItemClickListener itemClickListener;


    public interface OnItemClickListener{
        void onItemClick(View view, int position);
    }
    public void setOnItemClickListener(OnItemClickListener onItemClickListener ){
        this.itemClickListener=onItemClickListener;
    }
    public MyAdapter(List<String> datas) {
        this.datas = datas;
    }

    //创建新View，被LayoutManager所调用
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.area_item, viewGroup, false);
        ViewHolder vh = new ViewHolder(view,itemClickListener);
        return vh;
    }

    //将数据与界面进行绑定的操作
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        viewHolder.mTextView.setText(datas.get(position));
    }

    //获取数据的数量
    @Override
    public int getItemCount() {
        return datas.size();
    }

    //自定义的ViewHolder，持有每个Item的的所有界面元素
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView mTextView;
        private OnItemClickListener mitemClickListener;

        public ViewHolder(View view,OnItemClickListener itemClickListener) {
            super(view);
            mTextView = (TextView) view.findViewById(R.id.area_item_tv);
            this.mitemClickListener=itemClickListener;
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mitemClickListener != null) {
                mitemClickListener.onItemClick(view,getPosition());
            }
        }
    }

}
