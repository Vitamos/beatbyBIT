package pt.isel.gomes.beatbybit.util;


import android.database.Cursor;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Engine implements Serializable {

    private final String[] sampleRates = {"10", "100", "1000"};
    private String sampleRate = sampleRates[1];
    private final BITalino bit;
    private String macAddress;

    public Engine() {
        bit = new BITalino();
    }

    public String connect() {
        return macAddress + " @ " + sampleRate + " Hz";
    }

    public boolean setMac(String mac) {
        Pattern p = Pattern.compile("^([0-9A-Fa-f]{2}[:-]){5}[0-9A-Fa-f]{2}$");
        System.out.println(mac);
        Matcher m = p.matcher(mac);
        boolean result = m.find();
        if (result)
            macAddress = mac;
        return result;
    }

/*
    public static void main(String[] args){
        Engine e = new Engine();

    }*/

    public Frame[] open() {
        return bit.data(5);
    }


  /*  public String[][] analogString() {
        Frame[] data = open();
        String[][] analogs = new String[data.length][data[0].analog.length];
        for (int i = 0; i < data.length; i++) {
            int[] analog = data[i].analog;
            for (int j = 0; j < analog.length; j++) {
                analogs[i][j] = String.valueOf(analog[j]);
            }

        }
        return analogs;
    }*/

    public void close() {
        System.out.println("Nao implementado");
    }

    public String[] createFile(String file, Cursor cursor) {
        String[] values = new String[cursor.getCount()];
        Log.i("TESTPROVIDER", String.valueOf(cursor.getCount()));
        Log.i("TESTPROVIDER", String.valueOf(cursor.getColumnCount()));
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            String line = "";
            for (int j = 0; j < cursor.getColumnCount(); j++) {
                line += cursor.getColumnName(j) + " : " + cursor.getString(j) + " ";
                writeToFile(file,line);
            }
            values[i] = line;
        }
        cursor.close();
        return values;
    }

    public void uploadFile(File file) {
        System.out.println("Nao implementado");
    }

    public void writeToFile(String file, String data) {
        File root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        File dir = new File(root,"beat");
        if(!dir.exists()){
            dir.mkdirs();
        }
        File f = new File(dir+File.pathSeparator+file);
        try {
            FileOutputStream out = new FileOutputStream(f);
            out.write(data.getBytes());
            out.close();

        } catch (Exception e) {
        }


    }


    public void testCon() {
        System.out.println("Nao implementado");
    }

    public void setSampleRate(int choice) {
        this.sampleRate = sampleRates[choice];
    }


}
