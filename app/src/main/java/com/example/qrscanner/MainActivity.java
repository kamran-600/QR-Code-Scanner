package com.example.qrscanner;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.qrscanner.databinding.ActivityMainBinding;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.Reader;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanQR();
            }
        });

        binding.gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               launcher.launch("image/*");
            }
        });

    }

    //using Camera till line no 84
    private void scanQR() {
        ScanOptions options = new ScanOptions();
        options.setBeepEnabled(true);
        options.setPrompt("Scan QR Code");
        options.setCaptureActivity(QRResultActivity.class);
        resultLauncher.launch(options);
    }

    ActivityResultLauncher<ScanOptions> resultLauncher = registerForActivityResult(new ScanContract(), result -> {

        if (result.getContents() != null) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Result");
            builder.setMessage(result.getContents());
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            }).show();

        }
    });

//using Gallery till end
    ActivityResultLauncher<String> launcher = registerForActivityResult(new ActivityResultContracts.GetContent(), result -> {
        try {
            final InputStream inputStream = getContentResolver().openInputStream(result);
            final Bitmap selectedImage = BitmapFactory.decodeStream(inputStream);

            try {

                int width = selectedImage.getWidth();
                int height = selectedImage.getHeight();
                int[] pixels = new int[width * height];
                selectedImage.getPixels(pixels, 0, width, 0, 0, width, height);

                LuminanceSource source = new RGBLuminanceSource(width, height, pixels);
                BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(source));

                MultiFormatReader reader = new MultiFormatReader();

                String contents = reader.decode(binaryBitmap).getText();
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Result");
                builder.setMessage(contents);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).show();

//if it does not found QR Code in the image it will be automatically closed.

            } catch (Exception e) {
                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }


        } catch (FileNotFoundException e) {
            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    });
    //using Gallery till end
/*
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100) {
            try {
                assert data != null;
                final Uri uri = data.getData();
                final InputStream inputStream = getContentResolver().openInputStream(uri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(inputStream);

                try {
                    String contents;
                    int width = selectedImage.getWidth();
                    int height = selectedImage.getHeight();
                    int[] pixels = new int[width * height];
                    selectedImage.getPixels(pixels, 0, width, 0, 0, width, height);

                    LuminanceSource source = new RGBLuminanceSource(width, height, pixels);
                    BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(source));

                    MultiFormatReader reader = new MultiFormatReader();
                    Result result = reader.decode(binaryBitmap);
                    contents = result.getText();
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Result");
                    builder.setMessage(contents);
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    }).show();



                } catch (Exception e) {
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }


            } catch (FileNotFoundException e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

 */
}