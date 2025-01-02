package it.unipi.dii.aide.mircv;

import it.unipi.dii.aide.mircv.model.PostingList;
import it.unipi.dii.aide.mircv.model.Posting;
import org.junit.jupiter.api.Test;
import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.*;

public class PostingListTest {

    @Test
    void postingIteratorTest() {
        // Create postings
        Posting posting1 = new Posting(1, 2); // docId = 1, frequency = 2
        Posting posting2 = new Posting(3, 5); // docId = 3, frequency = 5
        Posting posting3 = new Posting(5, 1); // docId = 5, frequency = 1

        // Initialize PostingList and add postings
        PostingList postingList = new PostingList();
        postingList.addPosting(posting1);
        postingList.addPosting(posting2);
        postingList.addPosting(posting3);
        System.out.println("Posting list: " + postingList);


        // Validate that postings are correctly added
        assertEquals(3, postingList.getPostingList().size(), "The size of the posting list should be 3.");
        assertEquals(posting1, postingList.getPostingList().get(0), "First posting should match posting1.");
        assertEquals(posting2, postingList.getPostingList().get(1), "Second posting should match posting2.");
        assertEquals(posting3, postingList.getPostingList().get(2), "Third posting should match posting3.");

        // Test PostingListIterator
        postingList.getPostingListIterator().setPostingIterator(postingList.getPostingList().iterator());
        Iterator<Posting> iterator = postingList.getPostingListIterator().getPostingIterator();
        assertNotNull(iterator, "Iterator should not be null.");
        assertTrue(iterator.hasNext(), "Iterator should have next element.");

        // Iterate through postings and validate
        assertEquals(posting1, iterator.next(), "First iterator element should be posting1.");
        assertEquals(posting2, iterator.next(), "Second iterator element should be posting2.");
        assertEquals(posting3, iterator.next(), "Third iterator element should be posting3.");
        assertFalse(iterator.hasNext(), "Iterator should not have any more elements.");

        // Test clearing the posting list
        postingList.closeList();
        assertEquals(0, postingList.getPostingList().size(), "Posting list should be empty after clear.");

        System.out.println("Test passed: " + postingList);
    }


}
