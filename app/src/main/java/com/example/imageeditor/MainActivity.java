package com.example.imageeditor;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PaintDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.ByteArrayOutputStream;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    ImageButton b_cam,b_gal;
    Button b_grayscale,b_insert,b_clear;
    Bitmap b_ng;
    Uri uri;
    ImageView imageView;
    Bitmap bitmap_photo;
    EditText editText;
    TextView textView;
    Paint paint,paint1;
    Canvas canvas;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        b_cam=findViewById(R.id.camera);
        b_gal=findViewById(R.id.gallery);
        b_grayscale=findViewById(R.id.grayscale);
        b_insert=findViewById(R.id.text);
        imageView=findViewById(R.id.imageView);
        editText=findViewById(R.id.editTextTextPersonName);
        b_clear=findViewById(R.id.clear);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.parseColor("#c8d3e6"));
        }
        b_cam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_camera=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent_camera,1);

            }
        });
        b_gal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(intent,3);

            }
        });
        b_grayscale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 paint=new Paint();
                ColorMatrix colorMatrix=new ColorMatrix();
                colorMatrix.setSaturation(0);
                paint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
                canvas.drawBitmap(bitmap_photo,0,0,paint);
                imageView.setImageBitmap(b_ng);
            }
        });
        b_insert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editText.getText().toString().trim().length()==0)
                {
                    Toast.makeText(MainActivity.this, "Please Enter Something to display", Toast.LENGTH_SHORT).show();
                }
                else
                {
                     paint1=new Paint();
                    paint1.setColor(Color.YELLOW);
                    paint1.setTextSize(50);
                    canvas.drawText(editText.getText().toString().trim(),canvas.getWidth()/3,30,paint1);
                    imageView.setImageBitmap(b_ng);
                }
            }
        });
        b_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setText("");
                 paint1=new Paint();
                paint1.setColor(Color.YELLOW);
                canvas.drawColor(Color.WHITE);
                canvas.drawBitmap(bitmap_photo,0,0,paint);
            }
        });
        findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri1=getImageUri(getApplicationContext(),b_ng);
                Log.d(TAG, "onClick: "+uri1);
                Intent intent=new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                intent.setData(uri1);
                MainActivity.this.sendBroadcast(intent);
            }
        });
        findViewById(R.id.share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri1=getImageUri(getApplicationContext(),b_ng);
                Intent intent=new Intent(Intent.ACTION_SEND);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_STREAM,uri1);
                startActivity(Intent.createChooser(intent,"share"));
            }
        });
    }
    public Uri getImageUri(Context context, Bitmap inImage) {
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "sent_image", null);
        return Uri.parse(path);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,  Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK&&requestCode==3)
        {
            uri=data.getData();
            Picasso.get().load(uri).into(new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    bitmap_photo=bitmap;
                    imageView.setImageBitmap(bitmap_photo);
                    b_ng=Bitmap.createBitmap(bitmap_photo.getWidth(),bitmap_photo.getHeight(),bitmap_photo.getConfig());
                    canvas=new Canvas(b_ng);
                    canvas.drawBitmap(bitmap_photo,0,0,null);
                    b_grayscale.setVisibility(View.VISIBLE);

                }

                @Override
                public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            });
        }
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
             bitmap_photo = (Bitmap) extras.get("data");
            b_ng=Bitmap.createBitmap(bitmap_photo.getWidth(),bitmap_photo.getHeight(),bitmap_photo.getConfig());
            canvas=new Canvas(b_ng);
            canvas.drawBitmap(bitmap_photo,0,0,null);
            imageView.setImageBitmap(bitmap_photo);
        }
    }
}