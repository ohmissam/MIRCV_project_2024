package it.unipi.dii.aide.mircv.model;

import java.util.HashMap;
import java.util.Map;

public class DocumentIndex {

    private static Map<Long, DocumentEntry> documentIndex;

    public DocumentIndex(){
        documentIndex = new HashMap<>();
    }

    public static Map<Long, DocumentEntry> getDocumentIndex() {
        return documentIndex;
    }


    public void addDoc(long docId, DocumentEntry documentEntry){
        documentIndex.put(docId, documentEntry);
    }


//    public void setDocumentLength(Long docId, int length){
//        DocumentEntry entry = documentIndex.get(docId);
//        entry.setLength(length);
//    }

}
