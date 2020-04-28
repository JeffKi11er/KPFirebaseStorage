package com.example.khoaphamfinaltestfirebasestorage;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.khoaphamfinaltestfirebasestorage.adapter.ImageAdapter;
import com.example.khoaphamfinaltestfirebasestorage.item.ItemImage;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity{
    private List<ItemImage>itemImages;
    private ImageAdapter adapter;
    private ImageView imgResource;
    private Button btnSaveImage;
    private EditText editName;
    private Uri mUri;
    private static int REQUEST_CODE =101;
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    final StorageReference reference = storage.getReferenceFromUrl("gs://khoaphamstorage.appspot.com");
    private StorageTask uploadTask;
    private ProgressBar progressBar;
    private DatabaseReference mData;
    private RecyclerView rclImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mData = FirebaseDatabase.getInstance().getReference();
        init();
        imgResource.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent,REQUEST_CODE);
            }
        });
        btnSaveImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                Calendar calendar = Calendar.getInstance();
                final StorageReference nameRef = reference.
                        child("image"+calendar.getTimeInMillis()+ ".png");
                // Get the data from an ImageView as bytes
                imgResource.setDrawingCacheEnabled(true);
                imgResource.buildDrawingCache();
                Bitmap bitmap = ((BitmapDrawable) imgResource.getDrawable()).getBitmap();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                byte[] data = baos.toByteArray();

                UploadTask uploadTask = nameRef.putBytes(data);
                Task<Uri>urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if(!task.isSuccessful()){
                            throw task.getException();
                        }
                        return nameRef.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if(task.isSuccessful()){
                            progressBar.setVisibility(View.GONE);
                            Uri downloadUri = task.getResult();
                            Log.d("AAAA", "onComplete: Url: "+ downloadUri.toString());
                            ItemImage itemImage = new ItemImage(editName.getText().toString(),String.valueOf(downloadUri));
                            mData.child("folderImage").push().setValue(itemImage, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                    if (databaseError == null){
                                        Toast.makeText(MainActivity.this,"Successfully saved database"
                                                ,Toast.LENGTH_LONG).show();
                                    }else {
                                        Toast.makeText(MainActivity.this,"Fail to save in database",
                                                Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        }else {
                            Toast.makeText(MainActivity.this,"fail to getDownloadUrl",Toast.LENGTH_LONG).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this,"fail to upload",Toast.LENGTH_LONG).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Toast.makeText(MainActivity.this,"Successfully upload",Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }
    private void init() {
        imgResource = (ImageView)findViewById(R.id.img_rcl);
        btnSaveImage = (Button)findViewById(R.id.btn_save);
        editName = (EditText)findViewById(R.id.edt_name);
        progressBar = (ProgressBar)findViewById(R.id.progress_bar);
        rclImage = (RecyclerView)findViewById(R.id.rcl);
        itemImages = new ArrayList<>();
        adapter = new ImageAdapter(MainActivity.this,itemImages);
        rclImage.setAdapter(adapter);
        loadData();
    }
    private void loadData(){
        mData.child("folderImage").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                ItemImage image = (ItemImage) dataSnapshot.getValue(ItemImage.class);
                itemImages.add(new ItemImage(image.getImageName(),image.getLinkName()));
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_CODE && resultCode==RESULT_OK &&data.getData()!=null){
            mUri = data.getData();
//            Glide.with(MainActivity.this).load(mUri).into(imgResource);
           try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), mUri);
                imgResource.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
