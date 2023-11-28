package it.unipi.mircv.invertedIndex;

public class DocumentEntry {
    private int length;
    private double PageRank;

    public DocumentEntry(int length){// , double PageRank) {
        this.length = length;
        this.PageRank = 0;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public double getPR() {
        return PageRank;
    }

    public void setPR(double PageRank) {
        this.PageRank = PageRank;
    }

    public String toString() {
        return "[length=" + length +
                ", PageRank=" + PageRank +
                "]";
    }
}
