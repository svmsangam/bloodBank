package Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import Model.User;
import de.hdodenhof.circleimageview.CircleImageView;
import learnandroid.academy.bloodbank.R;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    private Context context;
    private List<User> userList;

    public UserAdapter(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_display,
                parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final User user = userList.get(position);

        holder.userType.setText(user.getType());
        if (user.getType().equals("donor")){
            holder.emailNow.setVisibility(View.VISIBLE);
        }
        holder.userName.setText(user.getName());
        holder.userEmail.setText(user.getEmail());
        holder.userPhone.setText(user.getPhone());
        holder.userIdNumber.setText(user.getIdNumber());
        holder.userBloodgroup.setText(user.getBloodgroup());
        if (user.getProfilePictureUrl()!=null) {
            Glide.with(context).load(user.getProfilePictureUrl()).into(holder.userProfileImage);
        }else {
            holder.userProfileImage.setImageResource(R.drawable.profile_image);
        }
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public CircleImageView userProfileImage;
        public TextView userName,userEmail,userType,userPhone,userIdNumber,userBloodgroup;
        public Button emailNow;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            userProfileImage = itemView.findViewById(R.id.userProfileImage);
            userName = itemView.findViewById(R.id.userName);
            userEmail = itemView.findViewById(R.id.userEmail);
            userType = itemView.findViewById(R.id.userType);
            userPhone = itemView.findViewById(R.id.userPhone);
            userIdNumber = itemView.findViewById(R.id.userIdNumber);
            userBloodgroup = itemView.findViewById(R.id.userBloodgroup);
            emailNow = itemView.findViewById(R.id.emailNow);
        }
    }
}
