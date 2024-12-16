package it.unipi.dii.aide.mircv.model;
import java.util.Iterator;
import it.unipi.dii.aide.mircv.model.PostingList;
import it.unipi.dii.aide.mircv.model.Posting;
import it.unipi.dii.aide.mircv.model.SkipBlock;
import static it.unipi.dii.aide.mircv.utils.Config.IS_DEBUG_MODE;
import java.util.Collections;


public class PostingListIterator {
    private Iterator<Posting> postingIterator;
    private Iterator<SkipBlock> skipBlockIterator;
    private SkipBlock currentSkipBlock;
    private long currentDocId;
    private int currentFrequency;
    private boolean noMorePostings = false;

    public PostingListIterator() {
    }

    public PostingListIterator(Iterator<Posting> postingIterator, Iterator<SkipBlock> skipBlockIterator) {
        this.postingIterator = postingIterator;
        this.skipBlockIterator = skipBlockIterator;
        this.noMorePostings = false;

        // Inizializza il primo skip block
        if (skipBlockIterator.hasNext()) {
            this.currentSkipBlock = skipBlockIterator.next();
        }
    }

    //Inizializza iteratore vuoto per gli skip block, usato in loadPostingList di PostingList
    public PostingListIterator(Iterator<Posting> postingIterator) {
        this(postingIterator, Collections.emptyIterator());
    }

    public Iterator<SkipBlock> getSkipBlockIterator() {
        return skipBlockIterator;
    }

    public Iterator<Posting> getPostingIterator() {
        return postingIterator;
    }

    public void setPostingIterator(Iterator<Posting> postingIterator) {
        this.postingIterator = postingIterator;
    }

    public void setSkipBlockIterator(Iterator<SkipBlock> skipBlockIterator) {
        this.skipBlockIterator = skipBlockIterator;
    }

    public void setCurrentDocId(long currentDocId) {
        this.currentDocId = currentDocId;
    }

    public void setCurrentFrequency(int currentFrequency) {
        this.currentFrequency = currentFrequency;
    }

    public Posting next() {
        if (!postingIterator.hasNext()) {
            if (skipBlockIterator.hasNext()) {
                currentSkipBlock = skipBlockIterator.next();
                return null;
            } else {
                noMorePostings = true;
                return null;
            }
        }

        Posting posting = postingIterator.next();
        currentDocId = posting.getDocId();
        currentFrequency = posting.getFrequency();
        return posting;
    }

    public void nextSkipBlock() {
        if (skipBlockIterator.hasNext()) {
            currentSkipBlock = skipBlockIterator.next();
        }
    }

    public boolean hasNext() {
        return postingIterator.hasNext() || skipBlockIterator.hasNext();
    }

    public boolean isNoMorePostings() {
        return noMorePostings;
    }


    // Helper function to set the flag when there are no more postings
    public void setNoMorePostings() {
        noMorePostings = true;
    }


    public SkipBlock getCurrentSkipBlock() {
        return currentSkipBlock;
    }

    public long getCurrentDocId() {
        return currentDocId;
    }

    public int getCurrentFrequency() {
        return currentFrequency;
    }
}
