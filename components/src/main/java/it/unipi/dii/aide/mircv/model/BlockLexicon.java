package it.unipi.dii.aide.mircv.model;

import it.unipi.dii.aide.mircv.utils.Utils;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

/**
 * Class that holds the information about a lexicon of a single block.
 */
import static it.unipi.dii.aide.mircv.utils.BlockLexiconEntryConfig.*;
import static it.unipi.dii.aide.mircv.utils.BlockLexiconEntryConfig.OFFSET_DOCIDS_LENGTH;
import static it.unipi.dii.aide.mircv.utils.BlockLexiconEntryConfig.OFFSET_FREQUENCIES_LENGTH;
import static it.unipi.dii.aide.mircv.utils.BlockLexiconEntryConfig.POSTING_LIST_LENGTH;
import static it.unipi.dii.aide.mircv.utils.BlockLexiconEntryConfig.TERM_LENGTH;
import static it.unipi.dii.aide.mircv.utils.MergedLexiconEntryConfig.*;

public class BlockLexicon {

    /*String -> term*/
    private  HashMap<String, BlockLexiconEntry> lexicon;

    public BlockLexicon() {
        lexicon = new HashMap<>();
    }

    public  HashMap<String, BlockLexiconEntry> getLexicon() {
        return lexicon;
    }

    public void setLexicon(HashMap<String, BlockLexiconEntry> lexicon) {
        this.lexicon = lexicon;
    }

    // Method to clear the lexicon
    public void clear() {
        lexicon.clear();  // Clears the internal map
    }
    public void writeToFile(RandomAccessFile lexiconFile, LexiconEntry termInfo){
        //Fill with whitespaces to keep the length standard
        String tmp = Utils.leftpad(termInfo.getTerm(), TERM_LENGTH);

        byte[] term = ByteBuffer.allocate(TERM_LENGTH).put(tmp.getBytes()).array();
        byte[] offsetDocId = ByteBuffer.allocate(OFFSET_DOCIDS_LENGTH).putLong(termInfo.getOffsetDocId()).array();
        byte[] offsetFrequency = ByteBuffer.allocate(OFFSET_FREQUENCIES_LENGTH).putLong(termInfo.getOffsetFrequency()).array();
        byte[] bytesDocId = ByteBuffer.allocate(BYTES_DOCID_LENGTH).putInt(termInfo.getDocIdsBytesLength()).array();
        byte[] bytesFrequency = ByteBuffer.allocate(BYTES_FREQUENCY_LENGTH).putInt(termInfo.getFrequenciesBytesLength()).array();
        byte[] postingListLength = ByteBuffer.allocate(POSTING_LIST_LENGTH).putInt(termInfo.getPostingListLength()).array();
        byte[] idf = ByteBuffer.allocate(IDF_LENGTH).putDouble(termInfo.getInverseDocumentFrequency()).array();
        byte[] offsetSkipBlocks = ByteBuffer.allocate(OFFSET_SKIPBLOCKS_LENGTH).putLong(termInfo.getOffsetSkipBlock()).array();
        byte[] numberOfSkipBlocks = ByteBuffer.allocate(NUMBER_OF_SKIPBLOCKS_LENGTH).putInt(termInfo.getNumberOfSkipBlocks()).array();
        byte[] tfidfTermUpperBound = ByteBuffer.allocate(MAXSCORE_LENGTH).putInt(termInfo.getTfidfTermUpperBound()).array();
        byte[] bm25TermUpperBound = ByteBuffer.allocate(MAXSCORE_LENGTH).putInt(termInfo.getBm25TermUpperBound()).array();

        try {
            lexiconFile.write(term);
            lexiconFile.write(offsetDocId);
            lexiconFile.write(offsetFrequency);
            lexiconFile.write(idf);
            lexiconFile.write(bytesDocId);
            lexiconFile.write(bytesFrequency);
            lexiconFile.write(postingListLength);
            lexiconFile.write(offsetSkipBlocks);
            lexiconFile.write(numberOfSkipBlocks);
            lexiconFile.write(tfidfTermUpperBound);
            lexiconFile.write(bm25TermUpperBound);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Lexicon Details:\n");
        sb.append("Total terms: ").append(lexicon.size()).append("\n");

        int previewLimit = 100; // Limit the number of entries to preview
        int count = 0;

        for (Map.Entry<String, BlockLexiconEntry> entry : lexicon.entrySet()) {
            if (count >= previewLimit) {
                sb.append("... and ").append(lexicon.size() - previewLimit).append(" more entries.\n");
                break;
            }
            sb.append("  Term: ").append(entry.getKey())
                    .append(" -> ").append(entry.getValue()).append("\n");
            count++;
        }

        return sb.toString();
    }

}