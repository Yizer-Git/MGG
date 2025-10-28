package com.just.cn.mgg.ui.address;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.just.cn.mgg.R;
import com.just.cn.mgg.data.model.Address;

import java.util.ArrayList;
import java.util.List;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.ViewHolder> {

    private Context context;
    private List<Address> addressList;
    private OnAddressActionListener listener;
    private boolean isSelectingMode = false; // 是否为选择模式

    public interface OnAddressActionListener {
        void onEditClick(Address address, int position);
        void onItemClick(Address address, int position); // 用于选择模式
    }

    public AddressAdapter(Context context, boolean isSelectingMode) {
        this.context = context;
        this.addressList = new ArrayList<>();
        this.isSelectingMode = isSelectingMode;
    }

    public void setAddressList(List<Address> addressList) {
        this.addressList = (addressList == null) ? new ArrayList<>() : addressList;
        notifyDataSetChanged();
    }

    public void setListener(OnAddressActionListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_address, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int currentPosition = holder.getAdapterPosition();
        if (currentPosition == RecyclerView.NO_POSITION || addressList == null || currentPosition >= addressList.size()) {
            return;
        }
        Address address = addressList.get(currentPosition);

        if (address == null) return;

        holder.tvReceiverNamePhone.setText(address.getReceiverName() + " " + address.getReceiverPhone());
        holder.tvFullAddress.setText(address.getFullAddress());

        if (address.isDefault()) {
            holder.tvDefaultTag.setVisibility(View.VISIBLE);
        } else {
            holder.tvDefaultTag.setVisibility(View.GONE);
        }

        // 编辑按钮点击
        holder.btnEditAddress.setOnClickListener(v -> {
            if (listener != null) {
                int latestPosition = holder.getAdapterPosition();
                if (latestPosition != RecyclerView.NO_POSITION) {
                    listener.onEditClick(addressList.get(latestPosition), latestPosition);
                }
            }
        });

        // 列表项点击（用于选择模式）
        holder.itemView.setOnClickListener(v -> {
            if (isSelectingMode && listener != null) {
                int latestPosition = holder.getAdapterPosition();
                if (latestPosition != RecyclerView.NO_POSITION) {
                    listener.onItemClick(addressList.get(latestPosition), latestPosition);
                }
            }
            // 非选择模式下点击无效果，或可进入详情（如果需要）
        });
    }

    @Override
    public int getItemCount() {
        return addressList == null ? 0 : addressList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvReceiverNamePhone, tvFullAddress, tvDefaultTag;
        ImageButton btnEditAddress;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvReceiverNamePhone = itemView.findViewById(R.id.tvReceiverNamePhone);
            tvFullAddress = itemView.findViewById(R.id.tvFullAddress);
            tvDefaultTag = itemView.findViewById(R.id.tvDefaultTag);
            btnEditAddress = itemView.findViewById(R.id.btnEditAddress);
        }
    }
}
