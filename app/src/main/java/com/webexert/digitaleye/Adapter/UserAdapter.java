package com.webexert.digitaleye.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.webexert.digitaleye.Helper.AddressManager;
import com.webexert.digitaleye.Model.UserDataModel;
import com.webexert.digitaleye.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    Context context;
    ArrayList<UserDataModel> data;
    UserInterface userInterface;

    public UserAdapter(Context context, ArrayList<UserDataModel> data) {
        this.context = context;
        this.data = data;
    }

    public void setOnClickListener(UserInterface userInterface){
        this.userInterface = userInterface;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.adapter_user,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Glide.with(context)
                .asBitmap()
                .load(new AddressManager().getImageAddress()+data.get(position).getImage())
                .into(holder.civUserImage);

        holder.tvUserName.setText(data.get(position).getFullName());

        holder.itemView.setOnClickListener(v -> {
            userInterface.OnItemClickListener(v,position);
        });

        holder.ivDeleteUser.setOnClickListener(v -> {
            userInterface.OnDeleteClickListener(v,position);
        });

        holder.ivEditUser.setOnClickListener(v -> {
            userInterface.OnEditClickListener(v,position);
        });

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        CircleImageView civUserImage;
        TextView tvUserName;
        ImageView ivDeleteUser,ivEditUser;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.civUserImage = itemView.findViewById(R.id.civUserImage);
            this.tvUserName = itemView.findViewById(R.id.tvUserName);
            this.ivDeleteUser = itemView.findViewById(R.id.ivDeleteUser);
            this.ivEditUser = itemView.findViewById(R.id.ivEditUser);
        }
    }

    public interface UserInterface{

        void OnItemClickListener(View view,int index);

        void OnDeleteClickListener(View view,int index);

        void OnEditClickListener(View view,int index);

    }

}
