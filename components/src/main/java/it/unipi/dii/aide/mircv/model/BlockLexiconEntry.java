package it.unipi.dii.aide.mircv.model;

/**
 * Class that holds the information about a lexicon entry of a single block.
 */
public class BlockLexiconEntry {
    private long offsetDocId;
    private long offsetFrequency;
    private int postingListLength;


    public BlockLexiconEntry() {
        this.offsetDocId = 0;
        this.offsetFrequency = 0;
        this.postingListLength = 0;
    }

    public BlockLexiconEntry(long offsetDocId, long offsetFrequency, int postingListLength) {
        this.offsetDocId = offsetDocId;
        this.offsetFrequency = offsetFrequency;
        this.postingListLength = postingListLength;
    }

    public void set(int offsetDocId, int offsetFrequency, int postingListLength) {
        this.setOffsetDocId(offsetDocId);
        this.setOffsetFrequency(offsetFrequency);
        this.setPostingListLength(postingListLength);
    }

    public int getPostingListLength() {
        return postingListLength;
    }

    public void setPostingListLength(int postingListLength) {
        this.postingListLength = postingListLength;
    }

    public long getOffsetDocId() {
        return offsetDocId;
    }

    public void setOffsetDocId(long offsetDocId) {
        this.offsetDocId = offsetDocId;
    }

    public long getOffsetFrequency() {
        return offsetFrequency;
    }

    public void setOffsetFrequency(long offsetFrequency) {
        this.offsetFrequency = offsetFrequency;
    }


    @Override
    public String toString() {
        return "LexiconEntry{" +
                "offsetDocId=" + offsetDocId +
                ", offsetFrequency=" + offsetFrequency +
                ", postingListLength=" + postingListLength +
                '}';
    }
}
