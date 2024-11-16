package it.unipi.dii.aide.mircv.utils;

public class LexiconEntryConfig {

    // Length in bytes of the term field
    public final static int TERM_LENGTH = 48;

    // Length in bytes of the offsetDocId field
    public final static int OFFSET_DOCIDS_LENGTH = 8;

    // Length in bytes of the frequency length field
    public final static int OFFSET_FREQUENCIES_LENGTH = 8;

    // Length in bytes of the docId field
    public final static int BYTES_DOCID_LENGTH = 4;

    // Length in bytes of the frequency field
    public final static int BYTES_FREQUENCY_LENGTH = 4;

    // Length in bytes of the postingListLength field
    public final static int POSTING_LIST_LENGTH = 4;

    // Length in bytes of the IDF field
    public final static int IDF_LENGTH = 8;

    // Length in bytes of the skip blocks offset field
    public final static int OFFSET_SKIPBLOCKS_LENGTH = 8;

    // Length in bytes of the number of skip blocks field
    public final static int NUMBER_OF_SKIPBLOCKS_LENGTH = 4;

    // Length in bytes of the max score field
    public final static int MAXSCORE_LENGTH = 4;

    // Total length in bytes of the term information structure
    public final static int TERM_INFO_LENGTH = TERM_LENGTH
            + OFFSET_DOCIDS_LENGTH
            + OFFSET_SKIPBLOCKS_LENGTH
            + NUMBER_OF_SKIPBLOCKS_LENGTH
            + OFFSET_FREQUENCIES_LENGTH
            + BYTES_DOCID_LENGTH
            + BYTES_FREQUENCY_LENGTH
            + POSTING_LIST_LENGTH
            + IDF_LENGTH
            + MAXSCORE_LENGTH
            + MAXSCORE_LENGTH;
}

