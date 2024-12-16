package it.unipi.dii.aide.mircv.utils;

public class MergedLexiconEntryConfig {
    public final static int TERM_LENGTH = BlockLexiconEntryConfig.TERM_LENGTH; // =48
    public final static int OFFSET_DOCIDS_LENGTH = BlockLexiconEntryConfig.OFFSET_DOCIDS_LENGTH; // =8
    public final static int OFFSET_FREQUENCIES_LENGTH = BlockLexiconEntryConfig.OFFSET_FREQUENCIES_LENGTH; // =8
    public final static int POSTING_LIST_LENGTH = BlockLexiconEntryConfig.POSTING_LIST_LENGTH; // =4

    // Length in bytes of the frequency field
    public final static int BYTES_FREQUENCY_LENGTH = 4;

    // Length in bytes of the docId field
    public final static int BYTES_DOCID_LENGTH = 4;

    // Length in bytes of the IDF field
    public final static int IDF_LENGTH = 8;

    // Length in bytes of the skip blocks offset field
    public final static int OFFSET_SKIPBLOCKS_LENGTH = 8;

    // Length in bytes of the number of skip blocks field
    public final static int NUMBER_OF_SKIPBLOCKS_LENGTH = 4;

    // Length in bytes of the max score field
    public final static int MAXSCORE_LENGTH = 4;

    public final static int MERGED_LEXICON_ENTRY_LENGTH = TERM_LENGTH
            + OFFSET_DOCIDS_LENGTH
            + OFFSET_FREQUENCIES_LENGTH
            + POSTING_LIST_LENGTH
            + BYTES_FREQUENCY_LENGTH
            + BYTES_DOCID_LENGTH
            + IDF_LENGTH
            + OFFSET_SKIPBLOCKS_LENGTH
            + NUMBER_OF_SKIPBLOCKS_LENGTH
            + MAXSCORE_LENGTH
            + MAXSCORE_LENGTH;
}
