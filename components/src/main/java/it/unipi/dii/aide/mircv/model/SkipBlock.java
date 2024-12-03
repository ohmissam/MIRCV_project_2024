package it.unipi.dii.aide.mircv.model;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

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

    public SkipBlock(long startDocidOffset, int skipBlockDocidLength, long startFreqOffset, int skipBlockFreqLength, long maxDocid) {
        this.startDocidOffset = startDocidOffset;
        this.skipBlockDocidLength = skipBlockDocidLength;
        this.startFreqOffset = startFreqOffset;
        this.skipBlockFreqLength = skipBlockFreqLength;
        this.maxDocid = maxDocid;
    }

    public void setFreqInfo(long startFreqOffset, int skipBlockFreqLength) {
        this.startFreqOffset = startFreqOffset;
        this.skipBlockFreqLength = skipBlockFreqLength;
    }

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

