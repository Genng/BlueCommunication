package com.snailf.ga.bluechat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class BTDeviceAdapter extends BaseAdapter {


    private List<BTItem> mListItem=new ArrayList<BTItem>();;
    private Context mcontext=null;
    private LayoutInflater mInflater=null;

    public BTDeviceAdapter(Context context){
        this.mcontext=context;
        //	this.mListItem=mListItem;
        this.mInflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    public void clearData(){
        mListItem.clear();
    }

    public void addDataModel(List<BTItem> itemList){
        if(itemList==null || itemList.size()==0)
            return;
        mListItem.addAll(itemList);
        notifyDataSetChanged();
    }
    public void addDataModel(BTItem item){
        mListItem.add(item);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {

        return mListItem.size();
    }

    @Override
    public Object getItem(int position) {

        return mListItem.get(position);
    }

    @Override
    public long getItemId(int position) {

        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder=null;

        if(convertView==null){
            holder=new ViewHolder();
            convertView = mInflater.inflate(R.layout.device_item_row, null);
            holder.tv=(TextView)convertView.findViewById(R.id.itemText);
            convertView.setTag(holder);
        }else{
            holder=(ViewHolder)convertView.getTag();
        }


        holder.tv.setText(mListItem.get(position).getBuletoothName());
        return convertView;
    }


    class ViewHolder{
        TextView tv;
    }

}