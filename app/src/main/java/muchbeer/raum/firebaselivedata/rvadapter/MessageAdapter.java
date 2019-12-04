package muchbeer.raum.firebaselivedata.rvadapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import muchbeer.raum.firebaselivedata.R;
import muchbeer.raum.firebaselivedata.databinding.MessageItemBinding;
import muchbeer.raum.firebaselivedata.model.Message;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {


    private final static String TAG = MessageAdapter.class.getSimpleName();
    private List<? extends Message> mMessageList ;

   public void setMessageList(final List<? extends Message> messageList){
        mMessageList = messageList;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        MessageItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.message_item,
                 parent,
        false);

        return new MessageViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = mMessageList.get(position);
        holder.binding.setMessage(mMessageList.get(position));
        //holder.binding.name.setText(message.getUserName());
        if(message.getPhotoUrl() != null && !message.getPhotoUrl().isEmpty())
            Glide.with(holder.binding.photoImageView.getContext())
                    .load(message.getPhotoUrl())
                    .thumbnail(0.01f)
                    .into(holder.binding.photoImageView);
        holder.binding.executePendingBindings();

    }

    @Override
    public int getItemCount() {
        return mMessageList == null ? 0 : mMessageList.size();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {

        private final MessageItemBinding binding;

        public MessageViewHolder(@NonNull MessageItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }
    }
}
