package it.unipi.dii.aide.mircv;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import it.unipi.dii.aide.mircv.model.InvertedIndex;
import it.unipi.dii.aide.mircv.model.PostingList;
import it.unipi.dii.aide.mircv.model.Posting;


public class InvertedIndexTest {

    @Test
    void constructorTest() {
        long docId = 1;
        int frequency = 2;
        Posting posting = new Posting(docId,frequency);

        PostingList postingList = new PostingList(posting);
        System.out.println(postingList);
    }
}
