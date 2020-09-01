package edu.own.machinelearning.ClasificacionDigitos;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.nex3z.fingerpaintview.FingerPaintView;

import java.io.IOException;

import edu.own.machinelearning.R;

public class ClasificacionDigitosActivity extends AppCompatActivity {

    private FingerPaintView fpV;
    private TextView textViewPred, textViewProb, textViewTi;
    private Button btnDetectar, btnLimpiar;
    private ClasificadorDigitos clasificadorDigitos;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classification);

        textViewPred = (TextView) findViewById(R.id.textViewPredecir);
        textViewProb = (TextView) findViewById(R.id.textViewProb);
        textViewTi = (TextView) findViewById(R.id.textViewTiempo);

        btnDetectar = (Button) findViewById(R.id.buttonDetectar);
        btnLimpiar = (Button) findViewById(R.id.buttonBorrar);

        fpV = (FingerPaintView) findViewById(R.id.fpvFinger);


        try {
            clasificadorDigitos = new ClasificadorDigitos(this);
        } catch (IOException e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
        }


        btnDetectar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Del FingerPrintView lo exportamos a Bitmap
                Bitmap bitmap = fpV.exportToBitmap(ClasificadorDigitos.IMG_HEIGHT, ClasificadorDigitos.IMG_WIDTH);
                ResultadoDigitos resultadoDigitos = clasificadorDigitos.classify(bitmap);
                textViewProb.setText("Probalidad : " + resultadoDigitos.getProbability());
                textViewTi.setText("Tiempo : " + resultadoDigitos.getTimeCost());
                textViewPred.setText("Predicción : " + resultadoDigitos.getNumber());


            }
        });

        btnLimpiar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fpV.clear();
                textViewProb.setText("Probalidad : ");
                textViewTi.setText("Tiempo : ");
                textViewPred.setText("Predicción : ");
            }
        });
    }


}