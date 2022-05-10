package learnandroid.academy.bloodbank;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class DonorRegisterActivity extends AppCompatActivity {
    private TextView backToLogin,imageError,bloodGroupError;
    private CircleImageView profile_image;
    private TextInputEditText registerName,registerIdNumber,
            registerPhone,registerEmail,registerPassword;
    private Spinner selectBloodGroup;
    private Button donorRegister;
    private Uri resultUri;
    private ProgressDialog loader;
    private FirebaseAuth mAuth;
    private DatabaseReference userDatabaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donor_register);

        backToLogin = findViewById(R.id.donorAlreadyRegistered);
        backToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(DonorRegisterActivity.this,LoginActivity.class);
                startActivity(i);
            }
        });
        //Initializing variables
        profile_image = findViewById(R.id.profile_image);
        registerName = findViewById(R.id.registerName);
        registerPhone = findViewById(R.id.registerPhone);
        registerIdNumber = findViewById(R.id.registerIdNumber);
        registerEmail = findViewById(R.id.registerEmail);
        registerPassword =findViewById(R.id.registerPassword);
        selectBloodGroup = findViewById(R.id.registerBloodGroup);
        donorRegister = findViewById(R.id.registerDonor);
        imageError = findViewById(R.id.imageError);
        bloodGroupError = findViewById(R.id.bloodGroupError);
        loader = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();

        //Getting profile image from user's gallery
        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/");
                startActivityForResult(intent,1);
            }
        });
        //Register Donor implementation
        donorRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String name = registerName.getText().toString().trim();
                final String email = registerEmail.getText().toString().trim();
                final String idNumber = registerIdNumber.getText().toString().trim();
                final String phone = registerPhone.getText().toString().trim();
                final String password = registerPassword.getText().toString().trim();
                final String bloodGroup = selectBloodGroup.getSelectedItem().toString();
                final String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
                final String phonePattern = "^[+]?[0-9]{10,13}$";

                //Validating inputs

                if (TextUtils.isEmpty(name)){
                    registerName.setError("Name is required");
                }
                if (TextUtils.isEmpty(email)){
                    registerEmail.setError("Email is required");
                }
                if (TextUtils.isEmpty(idNumber)){
                    registerIdNumber.setError("ID is required");
                }
                if (TextUtils.isEmpty(phone)){
                    registerPhone.setError("Phone number is required");
                }
                if (TextUtils.isEmpty(password)){
                    registerPassword.setError("Password is required");
                }
                if (bloodGroup.equals("Select your blood group")){
                    Toast.makeText(DonorRegisterActivity.this, "Select blood group", Toast.LENGTH_SHORT).show();
                }
                if (resultUri == null){
                    Toast.makeText(DonorRegisterActivity.this, "You must select and image", Toast.LENGTH_SHORT).show();
                }
                else if (!email.matches(emailPattern)){
                    registerEmail.setError("Incorrect email format");
                }
                else if (!phone.matches(phonePattern)){
                    registerPhone.setError("Incorrect number format");
                }else {
                    loader.setMessage("Registering Donor...");
                    loader.setCanceledOnTouchOutside(false);
                    loader.show();

                    //Firebase operations
                    mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()){
                                String error = task.getException().toString();
                                Toast.makeText(DonorRegisterActivity.this, "Error "+error, Toast.LENGTH_SHORT).show();
                            }else {
                                String currentUserId = mAuth.getCurrentUser().getUid();
                                //Storing in database
                                userDatabaseReference = FirebaseDatabase.getInstance().getReference()
                                        .child("users").child(currentUserId);
                                HashMap userInfo = new HashMap();
                                userInfo.put("id",currentUserId);
                                userInfo.put("name",name);
                                userInfo.put("phone",phone);
                                userInfo.put("email",email);
                                userInfo.put("password",password);
                                userInfo.put("idNumber",idNumber);
                                userInfo.put("bloodgroup",bloodGroup);
                                userInfo.put("type","donor");
                                userInfo.put("search","donor"+bloodGroup);

                                userDatabaseReference.updateChildren(userInfo).
                                        addOnCompleteListener(new OnCompleteListener() {
                                    @Override
                                    public void onComplete(@NonNull Task task) {
                                        if (task.isSuccessful()){
                                            Toast.makeText(DonorRegisterActivity.this,
                                                    "Donor Registered Successfully", Toast.LENGTH_SHORT).show();
                                        }else {
                                            Toast.makeText(DonorRegisterActivity.this,
                                                    task.getException().toString(), Toast.LENGTH_SHORT).show();
                                        }
                                        finish();
                                    }
                                });
                                if(resultUri!=null){
                                    final StorageReference filepath = FirebaseStorage.getInstance()
                                            .getReference().child("profile_image").child(currentUserId);
                                    Bitmap bitmap = null;
                                    try{
                                        bitmap = MediaStore.Images.Media.
                                                getBitmap(getApplication().getContentResolver(),resultUri);
                                    }catch(IOException e){
                                        e.printStackTrace();
                                    }
                                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                                    bitmap.compress(Bitmap.CompressFormat.JPEG,20,byteArrayOutputStream);
                                    byte[] data = byteArrayOutputStream.toByteArray();
                                    UploadTask uploadTask = filepath.putBytes(data);

                                    //Upload failure checking
                                    uploadTask.addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(DonorRegisterActivity.this,
                                                    "Image Upload failed. Please try Again!", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            if (taskSnapshot.getMetadata()!=null &&
                                                    taskSnapshot.getMetadata().getReference()!=null){
                                                Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                                                result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                        String imageUrl = uri.toString();
                                                        Map newIMageMp = new HashMap();
                                                        newIMageMp.put("profilePictureUrl",imageUrl);
                                                        userDatabaseReference.updateChildren(newIMageMp).addOnCompleteListener(new OnCompleteListener() {
                                                            @Override
                                                            public void onComplete(@NonNull Task task) {
                                                                if (task.isSuccessful()){
                                                                    Toast.makeText(DonorRegisterActivity.this, "Image uploaded " +
                                                                            "successfully", Toast.LENGTH_SHORT).show();
                                                                }else {
                                                                    Toast.makeText(DonorRegisterActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });
                                                    }
                                                });
                                            }
                                        }
                                    });
                                    Intent intent = new Intent(DonorRegisterActivity.this,LoginActivity.class);
                                    startActivity(intent);
                                    finish();
                                    loader.dismiss();
                                }
                            }
                        }
                    });
                }
            }
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==1 && resultCode==RESULT_OK && data!=null){
            resultUri = data.getData();
            profile_image.setImageURI(resultUri);
        }
    }
}

