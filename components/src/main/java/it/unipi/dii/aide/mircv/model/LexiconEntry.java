package it.unipi.dii.aide.mircv.model;

public class LexiconEntry {

    private int termFrequency;
    private int documentFrequency; // num of docs in which the term appears
    private double inverseDocumentFrequency;



    public LexiconEntry(int termFrequency,int documentFrequency){
        this.termFrequency = termFrequency;
        this.documentFrequency = documentFrequency;
        this.inverseDocumentFrequency = 0;
        //if (termFrequency == 0 && documentFrequency == 0)
        //    this.inverseDocumentFrequency = 0;
        //else
            //alla fine del processing IDF = log(Num tot documenti / num doc in cui è presente il term)
    }

    public int getTermFrequency() {
        return termFrequency;
    }

    public void setTermFrequency(int termFrequency) {
        this.termFrequency = termFrequency;
    }

    public int getDocumentFrequency() {
        return documentFrequency;
    }

    public void setDocumentFrequency(int documentFrequency) {
        this.documentFrequency = documentFrequency;
    }

    public double getInverseDocumentFrequency() {
        return inverseDocumentFrequency;
    }

    public void setInverseDocumentFrequency(double inverseDocumentFrequency) {
        this.inverseDocumentFrequency = inverseDocumentFrequency;
    }

}
