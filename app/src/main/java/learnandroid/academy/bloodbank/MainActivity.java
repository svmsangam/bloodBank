package learnandroid.academy.bloodbank;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import Adapter.UserAdapter;
import Model.User;
import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private CircleImageView nav_profile_image;
    private TextView nav_fullname, nav_email, nav_user_type, nav_user_bloodgroup;
    private DatabaseReference databaseReference;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private List<User> userList;
    private UserAdapter userAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.nav_navView);
        toolbar = findViewById(R.id.nav_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Blood Donation Nepal");
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(MainActivity.this,drawerLayout,
                toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        progressBar = findViewById(R.id.progressBar);
        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

        userList = new ArrayList<>();
        userAdapter = new UserAdapter(MainActivity.this,userList);
        recyclerView.setAdapter(userAdapter);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String type = snapshot.child("type").getValue().toString();

                if (type.equals("donor")){
                    readRecipients();
                }else {
                    readDonors();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        nav_profile_image = navigationView.getHeaderView(0).findViewById(R.id.nav_user_image);
        nav_fullname = navigationView.getHeaderView(0).findViewById(R.id.nav_user_name);
        nav_email = navigationView.getHeaderView(0).findViewById(R.id.nav_user_email);
        nav_user_type = navigationView.getHeaderView(0).findViewById(R.id.nav_user_type);
        nav_user_bloodgroup = navigationView.getHeaderView(0).findViewById(R.id.nav_user_bloodgroup);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(
          FirebaseAuth.getInstance().getCurrentUser().getUid()
        );
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String name = snapshot.child("name").getValue().toString();
                    nav_fullname.setText(name);

                    String email = snapshot.child("email").getValue().toString();
                    nav_email.setText(email);
                    String userType = snapshot.child("type").getValue().toString();
                    nav_user_type.setText(userType);
                    String bloodGroup = snapshot.child("bloodgroup").getValue().toString();
                    nav_user_bloodgroup.setText(bloodGroup);
                    if (snapshot.hasChild("profilePictureUrl")) {
                        String imageUrl = snapshot.child("profilePictureUrl").getValue().toString();
                        Glide.with(getApplicationContext()).load(imageUrl).into(nav_profile_image);
                    }else {
                        nav_profile_image.setImageResource(R.drawable.profile_image);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readDonors() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users");
        Query query = ref.orderByChild("type").equalTo("donor");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User user = dataSnapshot.getValue(User.class);
                    userList.add(user);
                }
                userAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);

                if (userList.isEmpty()){
                    Toast.makeText(MainActivity.this, "No such user exists",
                            Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readRecipients() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users");
        Query query = ref.orderByChild("type").equalTo("recipient");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    User user = dataSnapshot.getValue(User.class);

                    userList.add(user);
                }
                userAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);

                if (userList.isEmpty()){
                    Toast.makeText(MainActivity.this, "No such user exists",
                            Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.nav_profile:
                Intent intent = new Intent(MainActivity.this,ProfileActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_logout:
                FirebaseAuth.getInstance().signOut();
                Intent intent1 = new Intent(MainActivity.this,LoginActivity.class);
                startActivity(intent1);
                break;
            case R.id.nav_a_positive:
                Intent intent2 = new Intent(MainActivity.this,CategorySelectedActivity.class);
                intent2.putExtra("group","A+");
                startActivity(intent2);
                break;
            case R.id.nav_a_negative:
                Intent intent3 = new Intent(MainActivity.this,CategorySelectedActivity.class);
                intent3.putExtra("group","A-");
                startActivity(intent3);
                break;
            case R.id.nav_b_positive:
                Intent intent4 = new Intent(MainActivity.this,CategorySelectedActivity.class);
                intent4.putExtra("group","B+");
                startActivity(intent4);
                break;
            case R.id.nav_b_negative:
                Intent intent5 = new Intent(MainActivity.this,CategorySelectedActivity.class);
                intent5.putExtra("group","B-");
                startActivity(intent5);
                break;
            case R.id.nav_o_positive:
                Intent intent6 = new Intent(MainActivity.this,CategorySelectedActivity.class);
                intent6.putExtra("group","O+");
                startActivity(intent6);
                break;
            case R.id.nav_o_negative:
                Intent intent7 = new Intent(MainActivity.this,CategorySelectedActivity.class);
                intent7.putExtra("group","O-");
                startActivity(intent7);
                break;
            case R.id.nav_ab_positive:
                Intent intent8 = new Intent(MainActivity.this,CategorySelectedActivity.class);
                intent8.putExtra("group","AB+");
                startActivity(intent8);
                break;
            case R.id.nav_ab_negative:
                Intent intent9 = new Intent(MainActivity.this,CategorySelectedActivity.class);
                intent9.putExtra("group","AB-");
                startActivity(intent9);
                break;
            case R.id.nav_compatibleWithMe:
                Intent intent10 = new Intent(MainActivity.this,CategorySelectedActivity.class);
                intent10.putExtra("group","Compatible with me");
                startActivity(intent10);
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}