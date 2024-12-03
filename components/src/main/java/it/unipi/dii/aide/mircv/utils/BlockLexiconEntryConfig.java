package it.unipi.dii.aide.mircv.utils;

public class BlockLexiconEntryConfig {

    // Length in bytes of the term field
    public final static int TERM_LENGTH = 48;

    // Length in bytes of the offsetDocId field
    public final static int OFFSET_DOCIDS_LENGTH = 8;

    // Length in bytes of the frequency length field
    public final static int OFFSET_FREQUENCIES_LENGTH = 8;

    // Length in bytes of the postingListLength field
    public final static int POSTING_LIST_LENGTH = 4;


    public final static int LEXICON_BLOCK_ENTRY_SIZE = TERM_LENGTH
            + OFFSET_DOCIDS_LENGTH
            + OFFSET_FREQUENCIES_LENGTH
            + POSTING_LIST_LENGTH;   //68

}

