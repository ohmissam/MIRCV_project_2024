package it.unipi.dii.aide.mircv.model;


/**
 * Class that holds the information about a skip block.
 */
public class SkipBlock {

    //starting offset of the respective doc id block in the docids.txt file.
    long startDocidOffset;

    //Length in number of postings if not compressed or in byte if compressed
    int skipBlockDocidLength;

    //starting offset of the respective freq block in the frequencies.txt file.
    long startFreqOffset;

    //Length in number of postings if not compressed or in byte if compressed
    int skipBlockFreqLength;

    //maximum doc id in the block represented by this skipBlock.
    long maxDocid;


    /**
     * Constructor for the SkipBlock class.
     * @param startDocidOffset starting offset of the respective doc id block in the docids.txt file.
     * @param maxDocid maximum doc id in the block represented by this skipBlock.
     */
    public SkipBlock(long startDocidOffset, int skipBlockDocidLength, long maxDocid) {
        this.startDocidOffset = startDocidOffset;
        this.skipBlockDocidLength = skipBlockDocidLength;
        this.skipBlockFreqLength = -1;
        this.startFreqOffset = -1;
        this.maxDocid = maxDocid;
    }

    /**
     * Constructor for the SkipBlock class.
     * @param startDocidOffset the starting offset for document IDs in the block.
     * @param skipBlockDocidLength the length of the document ID segment in the block.
     * @param startFreqOffset the starting offset for term frequencies in the block.
     * @param skipBlockFreqLength the length of the frequency segment in the block.
     * @param maxDocid the maximum document ID within this skip block.
     */
    public SkipBlock(long startDocidOffset, int skipBlockDocidLength, long startFreqOffset, int skipBlockFreqLength, long maxDocid) {
        this.startDocidOffset = startDocidOffset;
        this.skipBlockDocidLength = skipBlockDocidLength;
        this.startFreqOffset = startFreqOffset;
        this.skipBlockFreqLength = skipBlockFreqLength;
        this.maxDocid = maxDocid;
    }

    //getter method
    public long getStartDocidOffset() {
        return startDocidOffset;
    }

    public int getSkipBlockDocidLength() {
        return skipBlockDocidLength;
    }

    public long getStartFreqOffset() {
        return startFreqOffset;
    }

    public int getSkipBlockFreqLength() {
        return skipBlockFreqLength;
    }

    public long getMaxDocid() {
        return maxDocid;
    }

    @Override
    public String toString() {
        return "startDocidOffset=" + startDocidOffset +
                "\nskipBlockDocidLength=" + skipBlockDocidLength +
                "\nstartFreqOffset=" + startFreqOffset +
                "\nskipBlockFreqLength=" + skipBlockFreqLength +
                "\nmaxDocid=" + maxDocid + "\n";
    }
}

