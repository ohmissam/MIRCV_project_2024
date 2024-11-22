package it.unipi.dii.aide.mircv.model;

public class DocumentEntry {
    //Docno of the document
    private final String docNo;

    //Length of the document
    private final int docLength;

    public String getDocNo() {
        return docNo;
    }

    public int getDocLength() {
        return docLength;
    }

    public DocumentEntry(String docNo, int docLength) {
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
}
