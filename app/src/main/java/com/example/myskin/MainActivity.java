package com.example.myskin;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;

//import com.example.myskin.ml.Model4TfCnn;

//import com.example.myskin.ml.ModelResnet;

import com.example.myskin.ml.ModelResnetaugmentedV2;
//import com.example.myskin.ml.ModelV3CnnTfAugmented10d60acc;
import com.example.myskin.ml.ModelV7VggTfAugmented10d60acc;
//import com.example.myskin.ml.Modelresnetaugmentedv2All;
//import com.example.myskin.ml.Modelresnetaugmentedv2All12d;
//import com.example.myskin.ml.Modelresnetaugmentedv2AllMini;
import com.example.myskin.ml.Modelresnetaugmentedv6Augmented10dv2;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
//import com.example.myskin.ml.Skinteach;
//import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

//import androidx.navigation.NavController;
//import androidx.navigation.Navigation;
//import androidx.navigation.ui.AppBarConfiguration;
//import androidx.navigation.ui.NavigationUI;
//import org.tensorflow.lite.Interpreter;
//
//import com.example.myskin.databinding.ActivityMainBinding;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.Manifest;
import android.widget.Toast;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "location";
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    int imageSize = 224;
    private TextView title, content,content2, symptomsText, causesText, treatmentText, descriptionText, linkText, symptomsLabel, causesLabel, treatmentLabel, descriptionLabel, linkLabel;
    String symptoms, causes,treatments,information,link,uid;
    ImageView openGallery, picture;
    Button takePic, openGallerys;
    String algo,name,confi;
    FirebaseDatabase db;
    DatabaseReference reference;
    FirebaseAuth auth;
    FirebaseUser user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        algo = intent.getStringExtra("ALGO");
        Toast.makeText(this, "You are using " + algo, Toast.LENGTH_SHORT).show();
        setContentView(R.layout.activity_main);
        ActionBar actionBar = getSupportActionBar();

        // showing the back button in action bar
        actionBar.setDisplayHomeAsUpEnabled(true);
        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
        uid=user.getUid();
        content = findViewById(R.id.result);
        content2 = findViewById(R.id.confidence);
        symptomsText = findViewById(R.id.symptomsText);
        causesText = findViewById(R.id.causesText);
        treatmentText = findViewById(R.id.treatmentText);
        descriptionText = findViewById(R.id.descriptionText);
        linkText = findViewById(R.id.linkText);
        openGallerys = findViewById(R.id.button2);
        symptomsLabel = findViewById(R.id.symptomslabel);
        causesLabel = findViewById(R.id.causeslabel);
        treatmentLabel = findViewById(R.id.treatmentlabel);
        descriptionLabel = findViewById(R.id.descriptionlabel);
        linkLabel = findViewById(R.id.linklabel);


        picture = findViewById(R.id.imageView);
        takePic = findViewById(R.id.button);
        openGallery = findViewById(R.id.imageView);
        // Start camera preview

        takePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, 3);
                } else {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, 100);
                }
            }
        });
        openGallerys.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cameraIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(cameraIntent, 1);
            }
        });
        openGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cameraIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(cameraIntent, 1);
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {

            Uri uri = data.getData();
            try {
                Bitmap image = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                // Log.d(TAG, String.valueOf(bitmap));
//                ImageView imageView = (ImageView) findViewById(R.id.imageView);
//                imageView.setImageBitmap(bitmap);
//                image = Bitmap.createScaledBitmap(image, imageSize, imageSize, false);
//                classifyImage(image);
                int dimension = Math.min(image.getWidth(), image.getHeight());
                image = ThumbnailUtils.extractThumbnail(image, dimension, dimension);
                picture.setImageBitmap(image);
                image = Bitmap.createScaledBitmap(image, imageSize, imageSize, false);
                classifyImage(image, algo);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (requestCode == 3 && resultCode == RESULT_OK && data != null) {
            Bitmap image = (Bitmap) data.getExtras().get("data");
            int dimension = Math.min(image.getWidth(), image.getHeight());
            image = ThumbnailUtils.extractThumbnail(image, dimension, dimension);
            picture.setImageBitmap(image);
            image = Bitmap.createScaledBitmap(image, imageSize, imageSize, false);
            classifyImage(image, algo);
        } else {
            Toast.makeText(this, "Select an Image :(", Toast.LENGTH_SHORT).show();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//
//        if (requestCode==1 && resultCode==RESULT_OK){
//            Uri imageUri = data.getData();
//            try {
//                Bitmap image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
//                int dimension = Math.min(image.getWidth(), image.getHeight());
//                image = ThumbnailUtils.extractThumbnail(image, dimension, dimension);
//                picture.setImageBitmap(image);
//                image = Bitmap.createScaledBitmap(image, imageSize, imageSize, false);
//                classifyImage(image);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        super.onActivityResult(requestCode, resultCode, data);
//    }


    private void classifyImage(Bitmap image, String algo) {
        Modelresnetaugmentedv6Augmented10dv2.Outputs outputCNN = null;
        ModelResnetaugmentedV2.Outputs outputResnet = null;
        ModelV7VggTfAugmented10d60acc.Outputs outputVgg = null;
        Modelresnetaugmentedv6Augmented10dv2 modelCNN = null;
        ModelResnetaugmentedV2 modelResnet = null;
        ModelV7VggTfAugmented10d60acc modelVGG = null;



        try {
//            Model4TfCnn model = Model4TfCnn.newInstance(getApplicationContext());//personal ccn 8 dieseases
//            Skinteach model = Skinteach.newInstance(getApplicationContext());//8 dieseases teachable machine
//            ModelResnet model = ModelResnet.newInstance(getApplicationContext());//8 diseases resnet50v2 model overfitting model
//             main ModelResnetaugmentedV2 model = ModelResnetaugmentedV2.newInstance(getApplicationContext());//8 diseases resnet50v2 model perfect model
//              Modelresnetaugmentedv2All model = Modelresnetaugmentedv2All.newInstance(getApplicationContext());//28 diseases resnet50 5000 samples each model perfect model.
//              Modelresnetaugmentedv2AllMini model = Modelresnetaugmentedv2AllMini.newInstance(getApplicationContext());//28 diseases resnet50 2000 samples each model overfitting model.
//            Modelresnetaugmentedv2All12d model = Modelresnetaugmentedv2All12d.newInstance(getApplicationContext());//12 diseases resnet50 5000 samples each model perfect model.
            //            ModelV3CnnTfAugmented10d60acc model=ModelV3CnnTfAugmented10d60acc.newInstance(getApplicationContext());//final model cnn


            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.FLOAT32);
//            TensorBuffer inputFeature0=TensorBuffer.createFixedSize(new int[]{1,64,64,3}, DataType.FLOAT32);
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * imageSize * imageSize * 3);
            byteBuffer.order(ByteOrder.nativeOrder());
            int[] intValue = new int[imageSize * imageSize];
            image.getPixels(intValue, 0, image.getWidth(), 0, 0, image.getWidth(), image.getHeight());
            int pixel = 0;
            for (int i = 0; i < imageSize; i++) {
                for (int j = 0; j < imageSize; j++) {
                    int val = intValue[pixel++];
                    byteBuffer.putFloat(((val >> 16) & 0xFF) * (1.f / 255.f));
                    byteBuffer.putFloat(((val >> 8) & 0xFF) * (1.f / 255.f));
                    byteBuffer.putFloat(((val & 0xFF) * (1.f / 255.f)));

                }
            }
            inputFeature0.loadBuffer(byteBuffer);
//            Model4TfCnn.Outputs outputs=model.process(inputFeature0);
//            Skinteach.Outputs outputs=model.process(inputFeature0);
//            ModelResnet.Outputs outputs=model.process(inputFeature0);

//            Modelresnetaugmentedv2All.Outputs outputs=model.process((inputFeature0));
//            Modelresnetaugmentedv2AllMini.Outputs outputs=model.process((inputFeature0));
//            Modelresnetaugmentedv2All12d.Outputs outputs=model.process((inputFeature0));


//              ModelV3CnnTfAugmented10d60acc.Outputs outputs=model.process((inputFeature0));


            if (algo.equals("CNN")) {
                modelCNN = Modelresnetaugmentedv6Augmented10dv2.newInstance(getApplicationContext());
                outputCNN = modelCNN.process(inputFeature0);
            } else if (algo.equals("Resnet50")) {
                modelResnet = ModelResnetaugmentedV2.newInstance(getApplicationContext());
                outputResnet = modelResnet.process(inputFeature0);
            } else {
                modelVGG = ModelV7VggTfAugmented10d60acc.newInstance(getApplicationContext());
                outputVgg = modelVGG.process(inputFeature0);
            }

            TensorBuffer outFeatures0 = null;
            if (algo.equals("CNN")) {
                outFeatures0 = outputCNN.getOutputFeature0AsTensorBuffer();
            } else if (algo.equals("Resnet50")) {
                outFeatures0 = outputResnet.getOutputFeature0AsTensorBuffer();
            } else {
                outFeatures0 = outputVgg.getOutputFeature0AsTensorBuffer();
            }


            float[] confidence = outFeatures0.getFloatArray();
            Log.i("confidence", Arrays.toString(confidence));


            int maxPos = 0;
            float maxConfidence = 0;
            for (int i = 0; i < confidence.length; i++) {
                if (confidence[i] > maxConfidence) {
                    Log.i("confidence", String.valueOf((confidence[i])));
                    maxConfidence = confidence[i];
                    maxPos = i;

                }

            }
            Log.i("confidence", "maxConfidence: " + maxConfidence + " maxPos: " + maxPos);
            String[] classes = null;
            if (algo.equals("Resnet50")) {
                classes = new String[]{"Actinic keratoses", "Basal cell carcinoma", "Benign keratosis-like lesions", "Dermatofibroma", "healthy", "Melanoma", "Melanocytic nevi", "Vascular lesions"};
            } else {
                classes = new String[]{"Acne and Rosacea Photos", "Actinic keratoses", "Basal cell carcinoma", "Benign keratosis-like lesions", "Dermatofibroma", "Eczema Photos", "Healthy", "Melanoma", "Melanocytic nevi", "Vascular lesions"};
            }

//            {"Acne and Rosacea Photos", "akiec", "bcc", "bkl", "df", "Eczema Photos", "healthy", "mel", "nv", "vasc"}
            String[] data = fetchDisease(classes[maxPos]);

            content.setText(classes[maxPos]);
            name=data[0];
            symptoms = data[1];
            causes = data[2];
            treatments = data[3];
            information = data[4];
            link=data[5];
            confi=String.valueOf(maxConfidence * 100);


            content2.setText(String.valueOf(maxConfidence * 100));
            symptomsLabel.setText("Symptoms:");
            causesLabel.setText("Causes:");
            treatmentLabel.setText("Treatments:");
            descriptionLabel.setText("Description:");
            linkLabel.setText("Link:");
            symptomsText.setText(symptoms);
            causesText.setText(causes);
            treatmentText.setText(treatments);
            descriptionText.setText(information);
            linkText.setText(link);
//            content2.setText("symptoms"+data[1]+"\ncauses"+data[2]+"\ntreatments"+data[3]+"\ninformation"+data[4]);



          PredictionDetails pred=new PredictionDetails(name,confi,symptoms,causes,treatments,link,uid,information);
          db=FirebaseDatabase.getInstance();
          reference=db.getReference("Predictions");
          reference.push().setValue(pred).addOnCompleteListener(new OnCompleteListener<Void>() {
              @Override
              public void onComplete(@NonNull Task<Void> task) {
                  Toast.makeText(MainActivity.this, "Data added", Toast.LENGTH_SHORT).show();
              }
          });
        } catch (IOException e) {

        }
    }

    private String[] fetchDisease(String disease_name) throws IOException {
        String[] data = {};
        try {
            InputStream inputStream = getAssets().open("diseases_info.csv");
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                data = line.split(",");
                if (data[0].equals(disease_name)) {
                    break;
                }
            }
            return data;
        } catch (IOException e) {
            e.printStackTrace();
        }


        return data;

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem logoutItem = menu.findItem(R.id.logout);
        logoutItem.setIconTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.white)));
        MenuItem infoItem = menu.findItem(R.id.info);
        infoItem.setIconTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.white)));

        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.logout) {
            FirebaseAuth.getInstance().signOut();
            Intent intent=new Intent(getApplicationContext(),LoginPage.class);
            startActivity(intent);
            finish();
            return true;
        }
        if (id == R.id.info) {
            Intent intent=new Intent(getApplicationContext(), AboutPage.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
