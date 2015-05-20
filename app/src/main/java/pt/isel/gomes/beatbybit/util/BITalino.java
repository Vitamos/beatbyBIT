package pt.isel.gomes.beatbybit.util;

import java.io.Serializable;

/**
 * Created by Gomes on 18-05-2015. Simula bitalino
 */
public class BITalino implements Serializable{

    public Frame[] data(int samples){
        Frame[] data = new Frame[samples];
        for (int i = 0; i < samples ; i++){
            data[i]= new Frame();
        }
        return data;
    }


    public BITalino(){

    }


}
