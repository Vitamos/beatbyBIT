package pt.isel.gomes.beatbybit.util;

import java.io.Serializable;

/**
 * Created by Gomes on 18-05-2015. Simula bitalino
 */
public class BITalino implements Serializable{

    public Frame[] data;


    public BITalino(){
        data = new Frame[100];
        for (int i = 0; i < 100 ; i++){
            data[i]= new Frame();
        }
    }

}
