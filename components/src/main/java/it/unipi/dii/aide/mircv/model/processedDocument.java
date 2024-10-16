package it.unipi.dii.aide.mircv.model;

/**
 Class representing a processed document after preProcessing
 */
public class processedDocument {

    public String  docNo;

    public String[] tokens;

    public processedDocument() {}

    public processedDocument(String docNo, String[] tokens) {
        this.docNo = docNo;
        this.tokens = tokens;
    }

    public String getDocNo() {
        return docNo;
    }

    public void setDocNo(String docNo) {
        this.docNo = docNo;
    }

    public String[] getTokens() {
        return tokens;
    }

    public void setTokens(String[] tokens) {
        this.tokens = tokens;
    }

    /**
     * Checks if the document contains textual content
     * @return boolean value defining the absence of textual content
     */
    public boolean isEmpty() {
        return getTokens().length == 0;
    }



}

