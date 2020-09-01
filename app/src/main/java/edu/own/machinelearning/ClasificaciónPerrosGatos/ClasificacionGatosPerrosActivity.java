package edu.own.machinelearning.ClasificaciónPerrosGatos;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import edu.own.machinelearning.R;

public class ClasificacionGatosPerrosActivity extends AppCompatActivity {

    private Button btnUpload, btnPred;
    private ImageView imageViewPhoto;
    private static final int IMG_PICKED = 101;
    private Uri imgUri;
    private Bitmap bitmap;
    private int mInputSize = 224;
    private String mModelPath = "cat_vs_dog.tflite";
    private String mLabelPath = "labels.txt";
    private  ClasificadorPerrosGatos classifier;
    private Toast myToast;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clasificacion_dos);


        btnPred = (Button) findViewById(R.id.buttonPred);
        btnUpload = (Button) findViewById(R.id.buttonUpload);
        imageViewPhoto = (ImageView) findViewById(R.id.imageViewPhoto);

        try {
            initClassifier();
        } catch (IOException e) {
            e.printStackTrace();
        }

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGalery();
            }
        });

        btnPred.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(imageViewPhoto.getDrawable() != null){
                    List<ClasificadorPerrosGatos.Recognition> result = classifier.recognizeImage(bitmap);
                    myToast.makeText(ClasificacionGatosPerrosActivity.this,
                            result.get(0).toString(), Toast.LENGTH_SHORT).show();

                }else{

                    myToast.makeText(ClasificacionGatosPerrosActivity.this,
                            "Introduzca una imagen", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initClassifier() throws IOException {
        //Le pasamos los assets, el path del modelo, el path de los lables y size de las imágenes
        classifier = new ClasificadorPerrosGatos(getAssets(), mModelPath, mLabelPath, mInputSize);
    }
    private void openGalery(){
        //Creamos el intent para cargar la imagen, esperamos un resultado por lo tanto creamos
        // una constante para saber que se refiere que hemos cogido la imagen
        // (Tendría más sentido si tuvieramos más resultados que recoger en onActivityResult
        Intent intentGalery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(intentGalery,IMG_PICKED);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == IMG_PICKED){
            imgUri = data.getData();
            //imageViewPhoto.setImageURI(imgUri);
            try {
                 bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imgUri);
                 imageViewPhoto.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}