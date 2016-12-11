package com.madalinadiaconu.arffrecorder;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import weka.classifiers.Classifier;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.REPTree;
import weka.core.Instances;

/**
 * Created by Diaconu Madalina on 11.12.16.
 * Util class used for classifying data in real time
 */

public class WekaClassifier {

    private static WekaClassifier instance;
    private J48 tree;

    private WekaClassifier() {
        try {
            this.tree = loadClassifierFromSharedPrefs();
        } catch (FileNotFoundException ex) {
            File root = Environment.getExternalStorageDirectory();
            File infile = new File(root, "training_dataset.arff");
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new FileReader(infile));
                Instances trainingData = new Instances(reader);
                reader.close();
                if (trainingData.classIndex() == -1)
                    trainingData.setClassIndex(trainingData.numAttributes() - 1);
                String[] options = new String[] { "-U" };
                tree = new J48();
                tree.setOptions(options);
                tree.buildClassifier(trainingData);
            } catch (Exception e) {
                e.printStackTrace();
            }

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
        return ActivityType.SITTING;
    }

}
