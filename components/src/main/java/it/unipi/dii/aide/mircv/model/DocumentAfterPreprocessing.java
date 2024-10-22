package it.unipi.dii.aide.mircv.model;

import java.util.Arrays;

public class DocumentAfterPreprocessing {

    private long docId;             // Unique identifier for the document
    private String docNo;           // Document number
    private String[] terms;         // Array of terms after preprocessing
    private int documentLength;      // Length of the terms array

    // Constructor that initializes with document number and terms
    public DocumentAfterPreprocessing( String docNo, String[] terms) {
        this.docNo = docNo;
        this.terms = terms;
        this.documentLength = terms.length;
    }

    // Constructor that initializes with document ID, document number, and terms
    public DocumentAfterPreprocessing(long docId, String docNo, String[] terms) {
        this.docId = docId;
        this.docNo = docNo;
        this.terms = terms;
        this.documentLength = terms.length;
    }

    // Getter for document ID
    public long getDocId() {
        return docId;
    }

    // Setter for document ID
    public void setDocId(long docId) {
        this.docId = docId;
    }

    // Getter for document number
    public String getDocNo() {
        return docNo;
    }

    // Getter for terms array
    public String[] getTerms() {
        return terms;
    }

    // Getter for document length
    public int getDocumentLength() {
        return documentLength;
    }

    // Override toString method to provide a string representation of the object
    @Override
    public String toString() {
        return "DocumentAfterPreprocessing{" +
                "docId=" + docId +
                ", docNo='" + docNo + '\'' +
                ", documentLength=" + documentLength +
                ", terms=" + Arrays.toString(terms) +
                '}';
    }
}
