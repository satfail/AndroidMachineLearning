package edu.own.machinelearning.ClasificaciónPerrosGatos;

import android.annotation.SuppressLint;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;

import androidx.annotation.NonNull;

import org.tensorflow.lite.Interpreter;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;


// Es un clasificador de imagenes bastante genérico
public class ClasificadorPerrosGatos {
    private Interpreter interpreter;
    private List<String> labelList;
    private int INPUT_SIZE ;
    private int PIXEL_SIZE = 3;
    private int IMAGE_MEAN = 0;
    private float IMAGE_STD = 255.0f;
    private float MAX_RESULTS = 3;
    private float THRESHOLD = 0.4f;
    ClasificadorPerrosGatos(AssetManager assetManager, String modelPath, String labelPath, int inputSize) throws IOException {
        INPUT_SIZE = inputSize;// Lo pasamos desde el activity dependiendo del size de la imagen a utilizar
        Interpreter.Options options = new Interpreter.Options();//Opciones para el Interpretador
        options.setNumThreads(5); // número de hilos
        options.setUseNNAPI(true); // Usar la API NeuralNetwork
        //Creamos el interpreter, le pasamos el método que carga el modelo con los assets y el path
        // y las opciones que hemos seleccionado anteriormente
        interpreter = new Interpreter(loadModelFile(assetManager, modelPath), options);
        // Cargamos las lables que queremos predecir
        labelList = loadLabelList(assetManager, labelPath);
    }


    class Recognition{
        String id= "";
        String title= "";//Gato o perro
        float confidence= 0F; // Probabilidad

        public Recognition(String i, String s, float confidence) {
            id= i;
            title = s;
            this.confidence = confidence;
        }

        @NonNull
        @Override
        public String toString() {
            return "Animal = "+title+", Confianza = "+confidence;
        }
    }



    private MappedByteBuffer loadModelFile(AssetManager assetManager, String MODEL_FILE) throws IOException {
        AssetFileDescriptor fileDescriptor = assetManager.openFd(MODEL_FILE);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }


    private List<String> loadLabelList(AssetManager assetManager, String labelPath) throws IOException {
        List<String> labelList = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(assetManager.open(labelPath)));
        String line;
        while ((line = reader.readLine()) != null) {
            labelList.add(line);
        }
        reader.close();
        return labelList;
    }

    /** Método para reconocer la imagen, escalamos la imagen y llamamos al interpreter
     * mandando el resultado vacío, retornamos el resultado ordenado
     */
    List<Recognition> recognizeImage(Bitmap bitmap) {
        //Creamos el escalado, si utilizamos otro tipo de imágenes de entrada para otros modelos
        // cambiamos las constantes
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, INPUT_SIZE, INPUT_SIZE, false);
        ByteBuffer byteBuffer = convertBitmapToByteBuffer(scaledBitmap);
        float [][]result = new float[1][labelList.size()];
        interpreter.run(byteBuffer, result);
        return getSortedResultFloat(result);
    }

    //Bitmap to ByteBuffer 
    private ByteBuffer convertBitmapToByteBuffer(Bitmap bitmap) {
        ByteBuffer byteBuffer;
        byteBuffer = ByteBuffer.allocateDirect(4  * INPUT_SIZE * INPUT_SIZE * PIXEL_SIZE);


        byteBuffer.order(ByteOrder.nativeOrder());
        int[] intValues = new int[INPUT_SIZE * INPUT_SIZE];
        bitmap.getPixels(intValues, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        int pixel = 0;
        for (int i = 0; i < INPUT_SIZE; ++i) {
            for (int j = 0; j < INPUT_SIZE; ++j) {
                final int val = intValues[pixel++];

                byteBuffer.putFloat((((val >> 16) & 0xFF)-IMAGE_MEAN)/IMAGE_STD);
                byteBuffer.putFloat((((val >> 8) & 0xFF)-IMAGE_MEAN)/IMAGE_STD);
                byteBuffer.putFloat((((val) & 0xFF)-IMAGE_MEAN)/IMAGE_STD);

            }
        }
        return byteBuffer;
    }

    @SuppressLint("DefaultLocale")
    private List<Recognition> getSortedResultFloat(float[][] labelProbArray) {
        //Hacemos una cola en la que comparamos el nivel de confianza de los dos objetos coalindates
        // en el array
        PriorityQueue<Recognition> pq =
                new PriorityQueue<>(
                        (int) MAX_RESULTS,
                        new Comparator<Recognition>() {
                            @Override
                            public int compare(Recognition lhs, Recognition rhs) {
                                return Float.compare(rhs.confidence, lhs.confidence);
                            }
                        });
        // Añadimos el objeto recognition a la cola, si esta fuera de los lables -> unknown
        for (int i = 0; i < labelList.size(); ++i) {
            float confidence = labelProbArray[0][i];
            if (confidence > THRESHOLD) {
                pq.add(new Recognition(""+ i,
                        labelList.size() > i ? labelList.get(i) : "unknown",
                        confidence));
            }
        }
        //Creamos el array de con los lables y la confianza de la cola con prioridad
        final ArrayList<Recognition> recognitions = new ArrayList<>();
        int recognitionsSize = (int) Math.min(pq.size(), MAX_RESULTS);
        for (int i = 0; i < recognitionsSize; ++i) {
            recognitions.add(pq.poll());
        }

        return recognitions;
    }


}
