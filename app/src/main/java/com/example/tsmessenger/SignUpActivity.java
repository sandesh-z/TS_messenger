package com.example.tsmessenger;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Dialog;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tsmessenger.databinding.ActivitySignUpBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;


public class SignUpActivity extends AppCompatActivity {
    ActivitySignUpBinding binding;
    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseStorage storage;
    Uri selectedImageUri;
    String imageUri;
    Uri cameraImageUri;
    private static final int CAMERA_PERMISSION_CODE = 1;
    ActivityResultLauncher<Intent> pickGalleryPictureLauncher;
    ActivityResultLauncher<Uri> takePictureLauncher;
    TextView okay_text, cancel_text;
    boolean isFromGallery = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();
        cameraImageUri = createUri();
        registerPictureLauncher();
        registerGalleryLauncher();

        setOnclickListeners();
    }

    private Uri createUri() {
        File imageFile = new File(getApplicationContext().getFilesDir(), "camera_photo.jpg");
        return FileProvider.getUriForFile(getApplicationContext(), "com.example.tsmessenger.fileProvider", imageFile);
    }

    private void registerPictureLauncher() {
        takePictureLauncher = registerForActivityResult(
                new ActivityResultContracts.TakePicture(), result -> {
                    if (result) {
                        binding.addProfile.setImageURI(null);
                        binding.addProfile.setImageURI(cameraImageUri);

                    }
                }
        );
    }

    private void registerGalleryLauncher() {
        pickGalleryPictureLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result != null) {
                Intent data = result.getData();
                ClipData.Item item = data.getClipData().getItemAt(0);
                selectedImageUri = item.getUri();
                binding.addProfile.setImageURI(selectedImageUri);

            }
        });
    }

    private void checkCameraPermissionAndOpenCamera() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);

        } else {
            takePictureLauncher.launch(cameraImageUri);
        }
    }

    private void setOnclickListeners() {
        binding.loginTextBtn.setOnClickListener(v -> {
            finish();
        });
        binding.addProfile.setOnClickListener(v -> {
            Dialog dialog = new Dialog(SignUpActivity.this);
            dialog.setContentView(R.layout.photo_chooser);
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.setCancelable(false);


            okay_text = dialog.findViewById(R.id.take_photo);
            cancel_text = dialog.findViewById(R.id.choose_from_gallery);

            okay_text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isFromGallery = false;
                    dialog.dismiss();
                    checkCameraPermissionAndOpenCamera();
                }
            });

            cancel_text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isFromGallery = true;
                    dialog.dismiss();
                    Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    pickGalleryPictureLauncher.launch(galleryIntent);
                }
            });

            dialog.show();


        });
        binding.btnSignUp.setOnClickListener(v -> {
            String enteredUsername = binding.usernameText.getText().toString();
            String enteredEmail = binding.emailTextSignUp.getText().toString();
            String enteredPassword = binding.passwordText.getText().toString();
            String confirmPassword = binding.confirmPasswordText.getText().toString();
            if (TextUtils.isEmpty(enteredUsername)) {
                Toast.makeText(this, "Username can not be empty", Toast.LENGTH_SHORT).show();

            } else if (TextUtils.isEmpty(enteredEmail)) {
                Toast.makeText(this, "Email can not be empty", Toast.LENGTH_SHORT).show();
            } else if (enteredPassword.isEmpty() || enteredPassword.length() < 8) {
                Toast.makeText(this, "Password must be at least 8 digit", Toast.LENGTH_SHORT).show();
            } else if (!confirmPassword.equals(enteredPassword)) {
                Toast.makeText(this, "Password do not match.", Toast.LENGTH_SHORT).show();
            } else if (!enteredEmail.matches("^(.+)@(\\S+)$")) {
                Toast.makeText(this, "Enter valid email", Toast.LENGTH_SHORT).show();

            } else {
                auth.createUserWithEmailAndPassword(enteredEmail, enteredPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (cameraImageUri != null && !isFromGallery) {
                            selectedImageUri = cameraImageUri;
                        }
                        if (task.isSuccessful()) {
                            String id = task.getResult().getUser().getUid();
                            DatabaseReference databaseReference = database.getReference().child("user").child(id);
                            StorageReference storageReference = storage.getReference().child("Upload").child(id);
                            StorageReference imageRef = storageReference.child("images/" + selectedImageUri.getLastPathSegment());
                            imageRef.putFile(selectedImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                imageUri = uri.toString();
                                                User user = new User(id, enteredUsername, enteredEmail, enteredPassword, "hi", "", imageUri);
                                                databaseReference.setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {

                                                            Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                                                            startActivity(intent);
                                                            finish();

                                                        } else {
                                                            Toast.makeText(SignUpActivity.this, "Error in creating the user", Toast.LENGTH_SHORT).show();
                                                        }

                                                    }
                                                });
                                            }
                                        });
                                    }
                                }
                            });

                        } else {
                            Toast.makeText(SignUpActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                takePictureLauncher.launch(cameraImageUri);
            } else {
                Toast.makeText(this, "Camera permission denied, please allow permission to take picture", Toast.LENGTH_SHORT).show();
            }
        }
    }


}