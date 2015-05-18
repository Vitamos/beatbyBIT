package pt.isel.gomes.beatbybit.util;

import java.io.Serializable;

/**
 * Created by Gomes on 18-05-2015. Simula frame bitalino
 */
public class Frame implements Serializable{
    public int[] analog;
    public int[] digital;

    public Frame(){
        analog = new int[2];
        digital = new int[6];
    }
}
