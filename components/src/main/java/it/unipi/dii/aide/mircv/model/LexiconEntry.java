package it.unipi.dii.aide.mircv.model;

import it.unipi.dii.aide.mircv.utils.Utils;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.Set;

public class LexiconEntry {

    //Length in bytes of the term field
    public final static int TERM_LENGTH = 48;

    //Length in bytes of the offsetDocId field
    public final static int OFFSET_DOCIDS_LENGTH = 8;

    //Length in bytes of the frequency length field
    public final static int OFFSET_FREQUENCIES_LENGTH = 8;

    //Length in bytes of the postingListLength field
    public final static int POSTING_LIST_LENGTH = 4;

    private long offsetDocId;
    private long offsetFrequency;
    private long offsetSkipBlock;
    private int postingListLength;
    private int termFrequency;
    private int documentFrequency; // num of docs in which the term appears
    private double inverseDocumentFrequency;

    public LexiconEntry() {
        this.offsetDocId = 0;
        this.offsetFrequency = 0;
        this.postingListLength = 0;
    }

    /*
    public LexiconEntry(int termFrequency, int documentFrequency) {
        this.termFrequency = termFrequency;
        this.documentFrequency = documentFrequency;
        this.inverseDocumentFrequency = 0;
        //if (termFrequency == 0 && documentFrequency == 0)
        //    this.inverseDocumentFrequency = 0;
        //else
        //alla fine del processing IDF = log(Num tot documenti / num doc in cui è presente il term)
    }
     */

    public LexiconEntry(long offsetDocId, long offsetFrequency, long offsetSkipBlock, int postingListLength) {
        this.offsetDocId = offsetDocId;
        this.offsetFrequency = offsetFrequency;
        this.offsetSkipBlock = offsetSkipBlock;
        this.postingListLength = postingListLength;
    }

    public void set(int offsetDocId, int offsetFrequency, int postingListLength) {
        this.setOffsetDocId(offsetDocId);
        this.setOffsetFrequency(offsetFrequency);
        this.setPostingListLength(postingListLength);
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

    public long getOffsetSkipBlock() {
        return offsetSkipBlock;
    }

    public void setOffsetSkipBlock(long offsetSkipBlock) {
        this.offsetSkipBlock = offsetSkipBlock;
    }

    /*
            IF token already exists in the dictionary then increase the term frequency
                BUT NOT document frequency (done after while-loop in preProcessing)
            ELSE add the term to the lexicon
            */
    public void writeToFile(RandomAccessFile lexiconFile, String key, LexiconEntry termInfo){

        //Fill with whitespaces to keep the length standard
        String tmp = Utils.leftpad(key, TERM_LENGTH);

        byte[] term = ByteBuffer.allocate(TERM_LENGTH).put(tmp.getBytes()).array();
        byte[] offsetDocId = ByteBuffer.allocate(OFFSET_DOCIDS_LENGTH).putLong(termInfo.getOffsetDocId()).array();
        byte[] offsetFrequency = ByteBuffer.allocate(OFFSET_FREQUENCIES_LENGTH).putLong(termInfo.getOffsetFrequency()).array();
        byte[] postingListLength = ByteBuffer.allocate(POSTING_LIST_LENGTH).putInt(termInfo.getPostingListLength()).array();

        try {
            lexiconFile.write(term);
            lexiconFile.write(offsetDocId);
            lexiconFile.write(offsetFrequency);
            lexiconFile.write(postingListLength);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString() {
        return "LexiconEntry{" +
                //"offsetDocId=" + offsetDocId +
                //", offsetFrequency=" + offsetFrequency +
                //", offsetSkipBlock=" + offsetSkipBlock +
                ", postingListLength=" + postingListLength +
                ", termFrequency=" + termFrequency +
               // ", documentFrequency=" + documentFrequency +
               //", inverseDocumentFrequency=" + inverseDocumentFrequency +
                '}';
    }
}
