package edu.own.machinelearning.Regresion;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.AssetFileDescriptor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

import edu.own.machinelearning.R;

public class LinearRegressionActivity extends AppCompatActivity {
    private Spinner dropdown;
    private Button btnPredecir;
    private EditText editTextID, editTextAd,editTextMar;
    private TextView editTextResult;
    private Interpreter interpreter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liner_regression);

        try {
            interpreter = new Interpreter(loadModelFile());
        } catch (IOException e) {
            e.printStackTrace();
        }


        btnPredecir = (Button) findViewById(R.id.button);
        editTextAd = (EditText) findViewById((R.id.editTextAdm));
        editTextID = (EditText) findViewById((R.id.editTextID));
        editTextMar = (EditText) findViewById((R.id.editTextMarketing));
        editTextResult = (TextView) findViewById((R.id.textViewResult));

        dropdown = findViewById(R.id.spinner1);
        //create a list of items for the spinner.
        String[] items = new String[]{"Florida", "New York", "California"};
        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
        //There are multiple variations of this, but this is the basic variant.
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        //set the spinners adapter to the previously created one.
        dropdown.setAdapter(adapter);
        btnPredecir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                float [][] f = new float[1][5];

                f[0][2] = Float.parseFloat(editTextID.getText().toString());
                f[0][3] = Float.parseFloat(editTextAd.getText().toString());
                f[0][4] = Float.parseFloat(editTextMar.getText().toString());

                switch (dropdown.getSelectedItemPosition()){
                    case 0:
                        f[0][0] = 1;
                        f[0][1] = 0;
                        break;
                    case 1:
                        f[0][0] = 0;
                        f[0][1] = 1;
                        break;
                    case 2:
                        f[0][0] = 0;
                        f[0][1] = 0;
                        break;

                    default:
                        f[0][0] = 1;
                        f[0][1] = 0;
                        break;
                }

                editTextResult.setText("Beneficios : " + doInference(f));
            }


        });

    }

    public float doInference(float[][] input){
        float[][] output = new float[1][1];

        interpreter.run(input,output);

        return output[0][0];
    }

    private MappedByteBuffer loadModelFile() throws IOException
    {
        AssetFileDescriptor assetFileDescriptor = this.getAssets().openFd("startUps.tflite");
        FileInputStream fileInputStream = new FileInputStream(assetFileDescriptor.getFileDescriptor());
        FileChannel fileChannel = fileInputStream.getChannel();
        long startOffset = assetFileDescriptor.getStartOffset();
        long length = assetFileDescriptor.getLength();
        return  fileChannel.map(FileChannel.MapMode.READ_ONLY,startOffset,length);
    }
}