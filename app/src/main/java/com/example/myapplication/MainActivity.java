package com.example.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.FileProvider;
import androidx.core.os.LocaleListCompat;

import java.io.File;
import java.io.FileOutputStream;

public class MainActivity extends AppCompatActivity {

    private ImageView animalImage;
    private TextView animalDesc;

    private String currentShareText = "";
    private int currentImageResId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        animalImage = findViewById(R.id.animalImage);
        animalDesc  = findViewById(R.id.animalDesc);

        Button btnLion     = findViewById(R.id.btnLion);
        Button btnCat      = findViewById(R.id.btnCat);
        Button btnDog      = findViewById(R.id.btnDog);
        Button btnElephant = findViewById(R.id.btnElephant);
        Button btnTiger    = findViewById(R.id.btnTiger);

        Button btnHebrew  = findViewById(R.id.btnHebrew);
        Button btnEnglish = findViewById(R.id.btnEnglish);

        Button btnShare   = findViewById(R.id.btnShare);

        // שפה
        btnHebrew.setOnClickListener(v ->
                AppCompatDelegate.setApplicationLocales(
                        LocaleListCompat.forLanguageTags("he")
                )
        );

        btnEnglish.setOnClickListener(v ->
                AppCompatDelegate.setApplicationLocales(
                        LocaleListCompat.forLanguageTags("en")
                )
        );

        // חיות
        btnLion.setOnClickListener(v -> showAnimal(R.drawable.lion, R.string.lion_desc));
        btnCat.setOnClickListener(v -> showAnimal(R.drawable.cat, R.string.cat_desc));
        btnDog.setOnClickListener(v -> showAnimal(R.drawable.dog, R.string.dog_desc));
        btnElephant.setOnClickListener(v -> showAnimal(R.drawable.elephant, R.string.elephant_desc));
        btnTiger.setOnClickListener(v -> showAnimal(R.drawable.tiger, R.string.tiger_desc));

        // שיתוף תמונה + טקסט
        btnShare.setOnClickListener(v -> {
            if (currentShareText == null || currentShareText.trim().isEmpty()) {
                currentShareText = animalDesc.getText().toString();
            }

            if (currentImageResId == 0) return;

            Uri imageUri = getImageUriFromDrawable(currentImageResId);
            if (imageUri == null) return;

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("image/*");
            shareIntent.putExtra(Intent.EXTRA_TEXT, currentShareText);
            shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            startActivity(Intent.createChooser(
                    shareIntent,
                    getString(R.string.share_chooser)
            ));
        });

        // ברירת מחדל - יראה אריה בהתחלה
        showAnimal(R.drawable.lion, R.string.lion_desc);
    }

    private void showAnimal(int imageResId, int descResId) {
        animalImage.setImageResource(imageResId);
        animalDesc.setText(descResId);

        currentShareText = getString(descResId);
        currentImageResId = imageResId; // <-- פה בדיוק זה צריך להיות
    }

    private Uri getImageUriFromDrawable(int drawableResId) {
        try {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), drawableResId);

            File cachePath = new File(getCacheDir(), "images");
            if (!cachePath.exists()) cachePath.mkdirs();

            File file = new File(cachePath, "shared_image.png");
            FileOutputStream stream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            stream.close();

            // שים לב: צריך שה-authority יתאים ל-AndroidManifest שלך
            return FileProvider.getUriForFile(
                    this,
                    getPackageName() + ".fileprovider",
                    file
            );
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
