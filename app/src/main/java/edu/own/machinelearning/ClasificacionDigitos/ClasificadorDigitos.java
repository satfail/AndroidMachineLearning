package edu.own.machinelearning.ClasificacionDigitos;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.os.SystemClock;
import android.util.Log;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;

public class ClasificadorDigitos {
    private static final String MODEL_NAME = "digit.tflite";
    private static final int BATCH_SIZE = 1; // 1 imagen
    public static final int IMG_HEIGHT = 28; //28x28
    public static final int IMG_WIDTH = 28;
    private static final int NUM_CHANNEL = 1; // 1 canal de color(verde en este caso)
    private static final int NUM_CLASSES = 10; // Del 0-9
    private static final String LOG_TAG = ClasificadorDigitos.class.getSimpleName();
    private final Interpreter.Options options = new Interpreter.Options();
    private final Interpreter interpreter;
    private final ByteBuffer imgData;
    private final int[] imgPixels = new int[IMG_HEIGHT*IMG_WIDTH];
    private final float[][] result = new float[1][NUM_CLASSES];

    public ClasificadorDigitos(Activity activity) throws IOException {
        //Le pasamos el la actividad y las opciones
        interpreter = new Interpreter(loadModelFile(activity),options);
        // Tipo de datos para el input con la memoria necesaria
        imgData = ByteBuffer.allocateDirect(4*BATCH_SIZE*IMG_HEIGHT*IMG_WIDTH*NUM_CHANNEL);

        imgData.order(ByteOrder.nativeOrder());

    }

    public ResultadoDigitos classify(Bitmap bitmap) {
        //Conversion
        convertBitmapToByteBuffer(bitmap);
        // Tomamos tiempo
        long startTime = SystemClock.uptimeMillis();

        interpreter.run(imgData, result);

        long endTime = SystemClock.uptimeMillis();

        long timeCost = endTime - startTime;
        //Creamos un registro en el log con los resultados, LOG_TAG define nuestra clase
        Log.v(LOG_TAG, "classify(): result = " + Arrays.toString(result[0])

                + ", timeCost = " + timeCost);

        return new ResultadoDigitos(result[0],timeCost); //Resultado lo unico que hace es el argmax de las
        //probabilidades , pasamos la primera fila (es un array2D)

    }
    //Carga del modelo desde el activity
    private MappedByteBuffer loadModelFile(Activity activity) throws IOException {

        AssetFileDescriptor fileDescriptor = activity.getAssets().openFd(MODEL_NAME);

        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());

        FileChannel fileChannel = inputStream.getChannel();

        long startOffset = fileDescriptor.getStartOffset();

        long declaredLength = fileDescriptor.getDeclaredLength();

        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);

    }


    private void convertBitmapToByteBuffer(Bitmap bitmap) {

        if (imgData == null) {

            return;

        }
        imgData.rewind();//Volvemos al principio
        //Cogemos los pixeles del bitmap
        bitmap.getPixels(imgPixels, 0, bitmap.getWidth(), 0, 0,

                bitmap.getWidth(), bitmap.getHeight());


        //recorremos la Matriz 2D de pixeles y la colocamos en la de 1D para los bytes haciendo
        // la conversión
        int pixel = 0;

        for (int i = 0; i < IMG_WIDTH; ++i) {

            for (int j = 0; j < IMG_HEIGHT; ++j) {

                int value = imgPixels[pixel++];

                imgData.putFloat(convertPixel(value));

            }

        }

    }



    private static float convertPixel(int color) {
        // Conversión del Pixel
        return (255 - (((color >> 16) & 0xFF) * 0.299f

                + ((color >> 8) & 0xFF) * 0.587f

                + (color & 0xFF) * 0.114f)) / 255.0f;

    }


}
