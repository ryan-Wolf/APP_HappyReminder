package com.example.app_happyreminder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class F_registrar extends AppCompatActivity {
    private ImageView foto;
    private Button abrirCamara;

    private static final int REQUEST_PERMISSION_CAMERA = 100;
    private static final int REQUEST_IMAGE_CAMERA = 100;

    String currentPhotoPath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_f_registrar);

        foto = (ImageView)findViewById(R.id.img_foto);
        abrirCamara = (Button)findViewById(R.id.btnCamara);

        abrirCamara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if (ActivityCompat.checkSelfPermission(F_registrar.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
                        irCamara();
                    } else {
                        ActivityCompat.requestPermissions(F_registrar.this, new String[]{Manifest.permission.CAMERA}, REQUEST_PERMISSION_CAMERA);
                    }
                }else {
                    irCamara();
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_IMAGE_CAMERA){
            if (permissions.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                irCamara();
            }else {
                Toast.makeText(this, "Necesita activar los permisos para la camara", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_IMAGE_CAMERA){
            if (resultCode == Activity.RESULT_OK){
                /*Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                foto.setImageBitmap(bitmap);
                Log.i("TAG", "Result=>" + bitmap);*/
                foto.setImageURI(Uri.parse(currentPhotoPath));
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void irCamara(){
        Intent camaraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (camaraIntent.resolveActivity(getPackageManager())!=null){
            //startActivityForResult(camaraIntent, REQUEST_IMAGE_CAMERA);
            File archivoFoto = null;
            try {
                archivoFoto = createFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (archivoFoto != null){
                Uri photoUri = FileProvider.getUriForFile(
                        this,
                        "com.example.app_happyreminder",
                        archivoFoto
                );
                camaraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(camaraIntent, REQUEST_IMAGE_CAMERA);
            }
        }
    }

    private File createFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HH-mm-ss", Locale.getDefault()).format(new Date());
        String imgFileName = "IMG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imgFileName,
                ".jpg",
                storageDir
        );
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }
}