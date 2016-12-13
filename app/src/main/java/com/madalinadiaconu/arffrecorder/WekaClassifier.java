package com.madalinadiaconu.arffrecorder;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;

import weka.classifiers.trees.J48;
import weka.core.Instance;
import weka.core.Instances;

/**
 * Created by Diaconu Madalina on 11.12.16.
 * Util class used for classifying data in real time
 */

public class WekaClassifier {

    private static WekaClassifier instance;
    private J48 tree;
    private Instances trainingData;

    private WekaClassifier() {
        BufferedReader reader;
        try {
            reader = new BufferedReader(
                    new InputStreamReader(
                            App.getAppContext().
                                    getAssets().
                                    open("training_dataset.arff")));
            trainingData = new Instances(reader);
            reader.close();
            if (trainingData.classIndex() == -1)
                trainingData.setClassIndex(trainingData.numAttributes() - 1);
            String[] options = new String[]{"-U"};
            tree = new J48();
            tree.setOptions(options);
            tree.buildClassifier(trainingData);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private J48 loadClassifierFromSharedPrefs() throws FileNotFoundException {
        SharedPreferences sharedPref = App.getAppContext().getSharedPreferences("classifier", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        J48 tree = gson.fromJson(sharedPref.getString("tree",null),J48.class);
        if (tree != null) {
            return tree;
        }
        throw new FileNotFoundException();
    }

    public static WekaClassifier getInstance() {
        if (instance == null) {
            instance = new WekaClassifier();
        }
        return instance;
    }

    public ActivityType classify(FeatureVector featureVector) {
        Instance instance = new Instance(4);
        instance.setValue(0, featureVector.getxMean());
        instance.setValue(1, featureVector.getzMean());
        instance.setValue(2, featureVector.getAbsVar());
        instance.setDataset(trainingData);
        double clLabel = 0;
        try {
            clLabel = tree.classifyInstance(instance);
            instance.setClassValue(clLabel);
            String label = instance.classAttribute().value((int) clLabel);
            return ActivityType.valueOf(label);
        } catch (Exception e) {
            return ActivityType.UNKNOWN;
        }
    }

}
