package learnandroid.academy.bloodbank;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private CircleImageView profile_profileImage;
    private TextView profile_name, profile_email,
            profile_id_number,profile_phone, profile_bloodgroup;
    private Button backBtn;
    DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profile_profileImage = findViewById(R.id.profile_profileImage);
        profile_name = findViewById(R.id.profile_user_name);
        profile_email = findViewById(R.id.profile_user_email);
        profile_id_number = findViewById(R.id.profile_user_id_number);
        profile_phone = findViewById(R.id.profile_user_phone);
        profile_bloodgroup = findViewById(R.id.profile_user_bloodgroup);
        backBtn = findViewById(R.id.profile_backBtn);

        toolbar = findViewById(R.id.nav_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        databaseReference = FirebaseDatabase.getInstance().getReference().child("users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()
        );
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    profile_name.setText(snapshot.child("name").getValue().toString());
                    profile_email.setText(snapshot.child("email").getValue().toString());
                    profile_id_number.setText(snapshot.child("idNumber").getValue().toString());
                    profile_phone.setText(snapshot.child("phone").getValue().toString());
                    profile_bloodgroup.setText(snapshot.child("bloodgroup").getValue().toString());
                    if (snapshot.hasChild("profilePictureUrl")) {
                        String imageUrl = snapshot.child("profilePictureUrl").getValue().toString();
                        Glide.with(getApplicationContext()).load(imageUrl).into(profile_profileImage);
                    }else {
                        profile_profileImage.setImageResource(R.drawable.profile_image);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return  true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}