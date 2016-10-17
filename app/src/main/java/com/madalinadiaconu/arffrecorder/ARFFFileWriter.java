package com.madalinadiaconu.arffrecorder;

import com.sromku.simple.storage.SimpleStorage;
import com.sromku.simple.storage.Storage;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.greenrobot.event.EventBus;

/**
 * Created by Diaconu Madalina on 15.10.2016.
 * Class in charge or writing the ARFF file to external storage
 */
public class ARFFFileWriter {

    private static final String DIRECTORY_NAME = "ACCELEROMETER_DATACCELEROMETER_DATAA";
    private static final String FILE_NAME = "AccelerometerData";
    private static final String FILE_EXTENSION = ".arff";

    private static ARFFFileWriter instance;
    private static String fileNameWithTimeStamp;
    private static Storage storage;

    private ARFFFileWriter(){

    }

    public static ARFFFileWriter getInstance(){
        if (instance == null) {
            instance = new ARFFFileWriter();
        }
        return instance;
    }

    public void startRecording() {
        EventBus.getDefault().register(getInstance());
        createFile();
        writeHeader();
    }

    public void stopRecording() {
        EventBus.getDefault().unregister(getInstance());
    }

    private void createFile() {
        storage = SimpleStorage.getExternalStorage();
        storage.createDirectory(DIRECTORY_NAME);
        fileNameWithTimeStamp = FILE_NAME + System.currentTimeMillis() + FILE_EXTENSION;
        storage.createFile(DIRECTORY_NAME, fileNameWithTimeStamp, "");
    }

    public void writeHeader() {
        addNameAndDate();
        addBlankLine();
        addRelation(WekaConstants.RELATION_TYPE_SYSDEV);
        addBlankLine();
        addAttribute("timestamp", WekaConstants.ATTRIBUTE_TYPE_NUMERIC);
        addAttribute("accelerationx", WekaConstants.ATTRIBUTE_TYPE_NUMERIC);
        addAttribute("accelerationy", WekaConstants.ATTRIBUTE_TYPE_NUMERIC);
        addAttribute("accelerationz", WekaConstants.ATTRIBUTE_TYPE_NUMERIC);
        addBlankLine();
        addDataTag();
    }

    private void addNameAndDate() {
        storage.appendFile(DIRECTORY_NAME, fileNameWithTimeStamp, "%Name: Diaconu Madalina");
        storage.appendFile(DIRECTORY_NAME, fileNameWithTimeStamp, "%Date: "+ (new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH).format(new Date())));
    }

    private void addRelation(String relationType) {
        storage.appendFile(DIRECTORY_NAME, fileNameWithTimeStamp, String.format("%s %s", WekaConstants.RELATION, relationType));
    }

    private void addAttribute(String attributeName, String attributeType) {
        storage.appendFile(DIRECTORY_NAME, fileNameWithTimeStamp, String.format("%s %s %s", WekaConstants.ATTRIBUTE, attributeName, attributeType));
    }

    private void addBlankLine() {
        storage.appendFile(DIRECTORY_NAME, fileNameWithTimeStamp, "");
    }

    private void addDataTag() {
        storage.appendFile(DIRECTORY_NAME, fileNameWithTimeStamp, WekaConstants.DATA);
    }

    private void writeValues(AccelerometerInfo accelerometerInfo) {
        storage.appendFile(DIRECTORY_NAME,
                fileNameWithTimeStamp,
                String.format(
                        "%d, %f, %f, %f",
                        accelerometerInfo.getTimestamp(),
                        accelerometerInfo.getX(),
                        accelerometerInfo.getY(),
                        accelerometerInfo.getZ()));
    }

    public void onEvent(AccelerometerInfo event){
        writeValues(event);
    }
}
