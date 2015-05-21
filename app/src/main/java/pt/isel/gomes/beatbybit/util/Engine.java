package pt.isel.gomes.beatbybit.util;


import java.io.File;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Engine implements Serializable {

    private String macAddress;
    private final String[] sampleRates = {"10", "100", "1000"};
    private String sampleRate = sampleRates[1];
    private final BITalino bit;

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


    public String[][] analogString() {
        Frame[] data = open();
        String[][] analogs = new String[data.length][data[0].analog.length];
        for (int i = 0; i < data.length; i++) {
            int[] analog = data[i].analog;
            for (int j = 0; j < analog.length; j++) {
                analogs[i][j] = String.valueOf(analog[j]);
            }

        }
        return analogs;
    }

    public void close() {
        System.out.println("Nao implementado");
    }

    public void createFile(Frame[] dados) {
        System.out.println("Nao implementado");
    }

    public void uploadFile(File file) {
        System.out.println("Nao implementado");
    }

    public void writeToFile(String file, String data) {
        try {

            FileOutputStream out = new FileOutputStream(new File(file));
            out.write(data.getBytes());
            out.close();

        } catch (Exception e) {
        }


    }


    public void testCon() {
        System.out.println("Nao implementado");
    }

    public void setSampleRate(int choice) {
        System.out.println("Nao implementado");
    }


}
