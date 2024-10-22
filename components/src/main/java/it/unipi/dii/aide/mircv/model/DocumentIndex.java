package it.unipi.dii.aide.mircv.model;

import java.util.HashMap;
import java.util.Map;

public class DocumentIndex {

    private static Map<Long, DocumentEntry> documentIndex;

    public DocumentIndex(){
        documentIndex = new HashMap<>();
    }

    public void addDoc(Long docId){
        documentIndex.put(docId, new DocumentEntry(0)); //PageRank doesn't set in the initial costructor
    }

    public void setDocumentLength(Long docId, int length){
        DocumentEntry entry = documentIndex.get(docId);
        entry.setLength(length);
    }

    public String toString() {
        StringBuilder result = new StringBuilder();

        for (Map.Entry<Long, DocumentEntry> entry : documentIndex.entrySet()) {
            Long term = entry.getKey();
            DocumentEntry documentEntry = entry.getValue();

            result.append(term).append("    ");
            result.append(documentEntry.getLength()).append(", ");
            result.append(documentEntry.getPR()).append("\n");
        }

        return result.toString();
    }
}
