package it.unipi.dii.aide.mircv.model;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Lexicon {


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

    /*String -> term*/
    private  Map<String, LexiconEntry> dictionary;

    public  Map<String, LexiconEntry> getDictionary() {
        return dictionary;
    }

    public Lexicon() {
        dictionary = new HashMap<>();
    }

    public Lexicon(long offsetDocId, long offsetFrequency, long offsetSkipBlock, int postingListLength) {
        this.offsetDocId = offsetDocId;
        this.offsetFrequency = offsetFrequency;
        this.offsetSkipBlock = offsetSkipBlock;
        this.postingListLength = postingListLength;
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
    public void addTerm(String term) {
        if(dictionary.containsKey(term)) {
            LexiconEntry entry = dictionary.get(term);
            int tf = entry.getTermFrequency();
            entry.setTermFrequency(tf + 1);
        }
        else {
            dictionary.put(term, new LexiconEntry(1, 0));
        }
    }

    public Set<String> getLexiconTerms(){
        return dictionary.keySet();
    }

    public void setDocumentFrequency(InvertedIndex invertedIndex, String term){
        LexiconEntry entry = dictionary.get(term);
        entry.setDocumentFrequency(invertedIndex.getPostingListLength(term));
    }

    public void setDictionary(Map<String, LexiconEntry> dictionary) {
        this.dictionary = dictionary;
    }

    public void writeToFile(RandomAccessFile lexiconFile, String outputPath,String key, LexiconEntry lexiconEntry,Lexicon lexicon){

        //Fill with whitespaces to keep the length standard
        String tmp = String.format("%" + TERM_LENGTH + "." + TERM_LENGTH + "s", outputPath);

        byte[] term = ByteBuffer.allocate(TERM_LENGTH).put(tmp.getBytes()).array();
        byte[] offsetDocId = ByteBuffer.allocate(OFFSET_DOCIDS_LENGTH).putLong(lexicon.getOffsetDocId()).array();
        byte[] offsetFrequency = ByteBuffer.allocate(OFFSET_FREQUENCIES_LENGTH).putLong(lexicon.getOffsetFrequency()).array();
        byte[] postingListLength = ByteBuffer.allocate(POSTING_LIST_LENGTH).putInt(lexicon.getPostingListLength()).array();

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
        StringBuilder result = new StringBuilder();

        for (Map.Entry<String, LexiconEntry> entry : dictionary.entrySet()) {
            String term = entry.getKey();
            LexiconEntry lexiconEntry = entry.getValue();

            result.append(term).append("    ");
            result.append(lexiconEntry.getTermFrequency()).append(", ");
            result.append(lexiconEntry.getDocumentFrequency()).append(", ");
            result.append(lexiconEntry.getInverseDocumentFrequency()).append("\n");
        }

        return result.toString();
    }

}