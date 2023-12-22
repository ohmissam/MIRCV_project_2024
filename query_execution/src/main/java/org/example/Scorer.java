package org.example;

import java.util.HashMap;

public class Scorer {
    public static double computeTFIDF(Integer postingFreq, double idf) {

        return idf * (1 + Math.log10(postingFreq));
    }
}

