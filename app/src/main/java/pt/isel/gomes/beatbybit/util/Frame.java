package pt.isel.gomes.beatbybit.util;

import java.io.Serializable;


public class Frame implements Serializable {
    public final int[] digital;
    public final int[] analog;

    public Frame() {
        digital = new int[4];
        analog = new int[6];
    }
}
