package it.unipi.dii.aide.mircv.model;

import java.util.HashMap;
import java.util.Map;

public class DocumentIndex {

    private static Map<Long, DocumentIndexEntry> documentIndex;

    public DocumentIndex(){
        documentIndex = new HashMap<>();
    }

    public static Map<Long, DocumentIndexEntry> getDocumentIndex() {
        return documentIndex;
    }


    public void addDoc(long docId, DocumentIndexEntry documentIndexEntry){
        documentIndex.put(docId, documentIndexEntry);
    }


//    public void setDocumentLength(Long docId, int length){
//        DocumentEntry entry = documentIndex.get(docId);
//        entry.setLength(length);
//    }

}
