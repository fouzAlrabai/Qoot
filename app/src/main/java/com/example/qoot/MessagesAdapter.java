package com.example.qoot;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import com.example.qoot.MessageDTO;


public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessagesHolder> {
    static int MY_MESSAGE = 1;
    static int OTHER_MESSAGES = 2;

    String currentUserId;

    List<MessageDTO> messages;
    SimpleDateFormat format = new SimpleDateFormat("hh:mm:ss a, dd-MM-yy");

    MessagesAdapter(String currentUserId){
        this.currentUserId = currentUserId;
        messages = new ArrayList<>();
    }

    @NonNull
    @Override
    public MessagesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View chatBubble;

        if(viewType == MY_MESSAGE){
            chatBubble = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_bubble_me, parent, false);
        }else{
            chatBubble = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_bubble_other_user, parent, false);
        }

        return new MessagesHolder(chatBubble);
    }

    @Override
    public int getItemViewType(int position) {
        // Just as an example, return 0 or 2 depending on position
        // Note that unlike in ListView adapters, types don't have to be contiguous
        if(messages.get(position).getSenderId().equals(currentUserId)){
            return MY_MESSAGE;
        }else{
            return OTHER_MESSAGES;
        }
    }


    @Override
    public void onBindViewHolder(@NonNull MessagesHolder holder, int position) {
        holder.bindView(messages.get(position));
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public void setData(List<MessageDTO> messages) {
        this.messages = messages;
        notifyDataSetChanged();
        /*if(!messages.isEmpty()) {
            this.messages.addAll(messages);
            notifyItemRangeInserted(this.messages.size() - messages.size(), this.messages.size());
        }*/
    }

    class MessagesHolder extends RecyclerView.ViewHolder {
        TextView mMessageTextView;
        TextView mDateSentTextView;
        TextView mSendernameTextView;

        public MessagesHolder(@NonNull View itemView) {
            super(itemView);
            mMessageTextView = itemView.findViewById(R.id.message_tv);
            mDateSentTextView = itemView.findViewById(R.id.dateSent_tv);
            mSendernameTextView = itemView.findViewById(R.id.sender_name_tv);
        }

        public void bindView(MessageDTO message) {
            mSendernameTextView.setText(message.getSenderName());
            mMessageTextView.setText(message.getText());
            if(message.getDateSent() != null) {
                mDateSentTextView.setText(format.format(message.getDateSent()));
            }else{
                mDateSentTextView.setText(format.format(new Date()));
            }
        }
    }

}
