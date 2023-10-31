package it.unipi.mircv;

public class DocumentEntry {
    private int length;
    private double PR;

    public DocumentEntry(int length, double PR) {
        this.length = length;
        this.PR = PR;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public double getPR() {
        return PR;
    }

    public void setPR(double PR) {
        this.PR = PR;
    }
}
