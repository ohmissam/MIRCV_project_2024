package it.unipi.dii.aide.mircv.model;

public class MergedLexiconEntry {
    private final String term;
    private final long offsetDocId;
    private final long offsetFrequency;
    private final int postingListLength;
    private final double inverseDocumentFrequency;
    private final int docIdsBytesLength;
    private final int frequenciesBytesLength;
    private final int numberOfSkipBlocks;
    private final long offsetSkipBlock;
    private final int tfidfTermUpperBound;
    private final int bm25TermUpperBound;


    public MergedLexiconEntry(String term, long offsetDocId, long offsetFrequency, double inverseDocumentFrequency,
                              int docIdsBytesLength, int frequenciesBytesLength, int postingListLength,
                              long offsetSkipBlock, int numberOfSkipBlocks, int tfidfTermUpperBound, int bm25TermUpperBound) {
        this.term = term;
        this.offsetDocId = offsetDocId;
        this.offsetFrequency = offsetFrequency;
        this.inverseDocumentFrequency = inverseDocumentFrequency;
        this.docIdsBytesLength = docIdsBytesLength;
        this.frequenciesBytesLength = frequenciesBytesLength;
        this.postingListLength = postingListLength;
        this.numberOfSkipBlocks = numberOfSkipBlocks;
        this.offsetSkipBlock = offsetSkipBlock;
        this.tfidfTermUpperBound = tfidfTermUpperBound;
        this.bm25TermUpperBound = bm25TermUpperBound;
    }

    public String getTerm() {
        return term;
    }

    public long getOffsetDocId() {
        return offsetDocId;
    }

    public long getOffsetFrequency() {
        return offsetFrequency;
    }

    public int getPostingListLength() {
        return postingListLength;
    }

    public double getInverseDocumentFrequency() {
        return inverseDocumentFrequency;
    }

    public int getDocIdsBytesLength() {
        return docIdsBytesLength;
    }

    public int getFrequenciesBytesLength() {
        return frequenciesBytesLength;
    }

    public int getNumberOfSkipBlocks() {
        return numberOfSkipBlocks;
    }

    public long getOffsetSkipBlock() {
        return offsetSkipBlock;
    }

    public int getTfidfTermUpperBound() {
        return tfidfTermUpperBound;
    }

    public int getBm25TermUpperBound() {
        return bm25TermUpperBound;
    }

    @Override
    public String toString() {
        return "LexiconEntry {" +
                "term='" + term + '\'' +
                ", offsetDocId=" + offsetDocId +
                ", offsetFrequency=" + offsetFrequency +
                ", postingListLength=" + postingListLength +
                ", inverseDocumentFrequency=" + inverseDocumentFrequency +
                ", docIdsBytesLength=" + docIdsBytesLength +
                ", frequenciesBytesLength=" + frequenciesBytesLength +
                ", numberOfSkipBlocks=" + numberOfSkipBlocks +
                ", offsetSkipBlock=" + offsetSkipBlock +
                ", tfidfTermUpperBound=" + tfidfTermUpperBound +
                ", bm25TermUpperBound=" + bm25TermUpperBound +
                '}';
    }

}
