package it.unipi.dii.aide.mircv.model;

import java.io.IOException;
import java.io.RandomAccessFile;

public class DocumentIndexEntry {
    //Docno of the document
    private final String docNo;

    //Length of the document
    private final int docLength;
    //Length in bytes of the docno field
    public static int DOCNO_LENGTH = 48;

    //Length in bytes of the docLength field
    public static int DOCLENGTH_LENGTH = 4;

    //Length in bytes of the docId
    public static int DOCID_LENGTH = 8;

    public static int DOCUMENT_INDEX_ENTRY_LENGTH = DOCID_LENGTH + DOCNO_LENGTH + DOCLENGTH_LENGTH;

    public String getDocNo() {
        return docNo;
    }

    public int getDocLength() {
        return docLength;
    }

    public DocumentIndexEntry(String docNo, int docLength) {
        this.docNo = docNo;
        this.docLength = docLength;
    }

    @Override
    public String toString() {
        return "DocumentEntry{" +
                "docNo='" + docNo + '\'' +
                ", docLength=" + docLength +
                '}';
    }
    public static int getDocLenFromDisk(RandomAccessFile documentIndexFile, long docId){

        //Accumulator for the current offset in the file
        long offset = (docId - 1)*DOCUMENT_INDEX_ENTRY_LENGTH + DOCID_LENGTH + DOCNO_LENGTH;

        try {
            //Move to the correct offset
            documentIndexFile.seek(offset);

            //Read the length of the document, 4 bytes starting from the offset
            return documentIndexFile.readInt();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
