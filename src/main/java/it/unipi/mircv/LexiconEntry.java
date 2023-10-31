package it.unipi.mircv;

public class LexiconEntry {

    private int termFrequency; // TF
    private int documentFrequency; // DF
    private double inverseDocumentFrequency; // IDF
    //private List<Integer> postingListPointer;

    //costruttore
    public LexiconEntry(int termFrequency,int documentFrequency, double inverseDocumentFrequency ){
        this.termFrequency = termFrequency;
        this.documentFrequency = documentFrequency;
        //this.inverseDocumentFrequency = inverseDocumentFrequency;
        //metodo per inizializzare il pointer alla psotinList?
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

    /*public List<Integer> getPostingListPointers() {
        return postingListPointer;
    }

    public void setPostingListPointers(List<Integer> postingListPointer) {
        this.postingListPointer = postingListPointer;
    }*/

    @Override
    public String toString() {
        return "termFrequency=" + termFrequency +
                ", documentFrequency=" + documentFrequency +
                ", inverseDocumentFrequency=" + inverseDocumentFrequency +
              //  ", postingListPointer=" + postingListPointer +
                '}';
    }
}
