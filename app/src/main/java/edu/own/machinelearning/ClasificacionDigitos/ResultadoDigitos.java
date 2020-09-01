package edu.own.machinelearning.ClasificacionDigitos;

public class ResultadoDigitos {

    private final int mNumber;
    private final float mProbability;
    private final long mTimeCost;
    //Pasamos el array de probabilidades y el tiempo
    public ResultadoDigitos(float[] probs, long timeCost) {
        mNumber = argmax(probs); //Pilla el max del array de las prob de cada número
        mProbability = probs[mNumber]; //Probabilidad del ese numero
        mTimeCost = timeCost; // Tiempo
    }

    public int getNumber() {
        return mNumber;
    }

    public float getProbability() {
        return mProbability;
    }

    public long getTimeCost() {
        return mTimeCost;
    }

    private static int argmax(float[] probs) { //Función max
        int maxIdx = -1;
        float maxProb = 0.0f;
        for (int i = 0; i < probs.length; i++) {
            if (probs[i] > maxProb) {
                maxProb = probs[i];
                maxIdx = i;
            }
        }
        return maxIdx;
    }
}
