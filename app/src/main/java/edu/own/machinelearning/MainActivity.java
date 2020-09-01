package edu.own.machinelearning;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Spinner;

import edu.own.machinelearning.ClasificacionDigitos.ClasificacionDigitosActivity;
import edu.own.machinelearning.ClasificaciónPerrosGatos.ClasificacionGatosPerrosActivity;
import edu.own.machinelearning.Regresion.LinearRegressionActivity;

public class MainActivity extends AppCompatActivity {
    private ImageButton btnRegresion,btnClasificacion, btnClasificacionDos;
    private Spinner dropdown;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //get the spinner from the xml.
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnRegresion = (ImageButton) findViewById(R.id.imageButton);
        btnClasificacion = (ImageButton) findViewById(R.id.imageButtonClas);
        btnClasificacionDos = (ImageButton) findViewById(R.id.imageButtonClasDos);
        btnRegresion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, LinearRegressionActivity.class);
                startActivity(intent);
            }
        });

        btnClasificacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ClasificacionDigitosActivity.class);
                startActivity(intent);
            }
        });

        btnClasificacionDos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ClasificacionGatosPerrosActivity.class);
                startActivity(intent);
            }
        });
    }
}