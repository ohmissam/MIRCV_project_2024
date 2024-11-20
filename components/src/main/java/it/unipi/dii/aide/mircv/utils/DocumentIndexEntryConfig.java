package it.unipi.dii.aide.mircv.utils;

public class DocumentIndexEntryConfig {
    //Length in bytes of the docId
    public static int DOCID_LENGTH = 8;

    //Length in bytes of the docno field
    public static int DOCNO_LENGTH = 48;

    //Length in bytes of the docLength field
    public static int DOCLENGTH_LENGTH = 4;

    //long + string[48] + int
    public static int DOCUMENT_INDEX_ENTRY_LENGTH = DOCID_LENGTH + DOCNO_LENGTH + DOCLENGTH_LENGTH;
}
