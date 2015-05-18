package pt.isel.gomes.beatbybit.util;


import java.io.File;
import java.io.FileOutputStream;
import java.io.Serializable;

public class Engine implements Serializable{

    private String macAddress;
    private String[] sampleRates = {"10","100","1000"};
    private String sampleRate = sampleRates[1];

    public Engine(){

    }

    public String connect(){
       return "Hello World";
    }

    public void setMac(String mac){
        System.out.println("Nao implementado");
    }
    public void open(){
        System.out.println("Nao implementado");
    }

    public void close(){
        System.out.println("Nao implementado");
    }

    public void createFile(String dados){
        System.out.println("Nao implementado");
    }
    public void setupCloud(String user, String pass){
        System.out.println("Nao implementado");
    }

    public void uploadFile(File file){
        System.out.println("Nao implementado");
    }
    public void writeToFile(String file, String data) {
        try{

            FileOutputStream out = new FileOutputStream(new File(file));
            out.write(data.getBytes());
            out.close();

        } catch (Exception e) { }


    }



    public void testCon(){
        System.out.println("Nao implementado");
    }

    public void setSampleRate(int choice){
        System.out.println("Nao implementado");
    }


    public static void main(String[] args){
        Engine a = new Engine();
        a.connect();
    }
}
