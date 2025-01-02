package it.unipi.dii.aide.mircv;

import it.unipi.dii.aide.mircv.model.DocumentIndexEntry;
import org.junit.jupiter.api.Test;
import it.unipi.dii.aide.mircv.model.DocumentIndex;

public class DocumentIndexTest {
    @Test
    void constructorTest() {
        DocumentIndex documentIndex = new DocumentIndex();
    }

    @Test
    void addInformationTest(){
        DocumentIndex documentIndex = new DocumentIndex();
        String docNo = "0";
        int docLenght = 20;
        DocumentIndexEntry documentIndexEntry = new DocumentIndexEntry(docNo,docLenght);
        long docId =  Long.parseLong(docNo) +1;
        documentIndex.addDoc(docId, documentIndexEntry);
        System.out.println(documentIndex);
    }

}
