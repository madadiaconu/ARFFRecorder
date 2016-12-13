package com.madalinadiaconu.arffrecorder.util;

import com.madalinadiaconu.arffrecorder.App;
import com.madalinadiaconu.arffrecorder.model.ActivityType;
import com.madalinadiaconu.arffrecorder.model.FeatureVector;

import java.io.BufferedReader;
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
        double clLabel;
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
