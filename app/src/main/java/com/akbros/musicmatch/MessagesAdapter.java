package com.akbros.musicmatch;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MessagesAdapter extends BaseAdapter {

    private Context mContext;
    private List<Message> messages;

    public MessagesAdapter(Context context,List<Message> lm) {
        mContext = context;
        messages = lm;
    }

    public void addMessage(int type,String message){
        Message m = new Message(message,type);
        messages.add(m);
        Log.d("SOUNDCLOUD",message);
        notifyDataSetChanged();
    }

    public void clearChat(){
        messages.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return messages.size();
    }

    @Override
    public Message getItem(int position) {
        return messages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Message msg = getItem(position);

        Log.d("SONDCLOUD","msg :: "+msg.getMessage()+" , "+msg.type);

        ViewHolder holder;
//        if (convertView == null) {
            if(msg.type==Message.TYPE_YOU){
                convertView = LayoutInflater.from(mContext).inflate(R.layout.message_list_row_you, parent, false);
                holder = new ViewHolder();
                holder.message = (TextView) convertView.findViewById(R.id.message_you);
                holder.message.setText(msg.getMessage());
                convertView.setTag(holder);
            }else if(msg.type==Message.TYPE_STRANGER){
                convertView = LayoutInflater.from(mContext).inflate(R.layout.message_list_row_stranger, parent, false);
                holder = new ViewHolder();
                holder.message = (TextView) convertView.findViewById(R.id.message_stranger);
                holder.message.setText(msg.getMessage());
                convertView.setTag(holder);
            }else if(msg.type== Message.TYPE_COMMENT){
                Log.d("SOUNDCLOUD","Comment view inflating");
                convertView = LayoutInflater.from(mContext).inflate(R.layout.message_list_row_comment, parent, false);
                holder = new ViewHolder();
                holder.message = (TextView) convertView.findViewById(R.id.message_comment);
                holder.message.setText(msg.getMessage());
                convertView.setTag(holder);
            }
//        } else {
//            holder = (ViewHolder) convertView.getTag();
//        }

        return convertView;
    }

    static class ViewHolder {
        TextView message;
    }

}