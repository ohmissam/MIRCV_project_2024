package it.unipi.mircv;

import java.util.HashMap;
import java.util.Map;

public class DocumentIndex {

    private static Map<String, DocumentEntry> documentIndex;

    public DocumentIndex(){
        documentIndex = new HashMap<>();
    }

    public void addDoc(String docId){
        documentIndex.put(docId, new DocumentEntry(0)); //PageRank doesn't set in the initial costructor
    }

    public void setDocumentLength(String docId, int length){
        DocumentEntry entry = documentIndex.get(docId);
        entry.setLength(length);
    }

    public String toString() {
        StringBuilder result = new StringBuilder();

        for (Map.Entry<String, DocumentEntry> entry : documentIndex.entrySet()) {
            String term = entry.getKey();
            DocumentEntry documentEntry = entry.getValue();

            result.append(term).append("    ");
            result.append(documentEntry.getLength()).append(", ");
            result.append(documentEntry.getPR()).append("\n");
        }

        return result.toString();
    }
}
