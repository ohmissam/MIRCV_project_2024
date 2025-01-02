package it.unipi.mircv.scorer;

import java.util.Iterator;
import java.util.PriorityQueue;
import it.unipi.dii.aide.mircv.model.Tuple;


/**
 * This class is an extension of the PriorityQueue class. It has as element Tuples consisting in a (docId, score) pair,
 * where the score is used to determine the priority of the docId in such a way that the docId will be ordered in a
 * decreasing order of score. It handles also the threshold score used for pruning purposes (MaxScore).
 */
public class RankedDocs extends PriorityQueue<Tuple<Long, Double>> {

    //Threshold to enter among the top K documents
    private double threshold;

    //Top K documents
    private final int K;

    /**
     * Construct a priority queue to track the top K documents
     * @param K number of desired elements at the top of the queue
     */
    public RankedDocs(int K){

        //Order in descending order of score the top K documents
        super((o1, o2) -> o2.getSecond().compareTo(o1.getSecond()));

        //At the beginning the threshold is 0
        this.threshold = 0;

        this.K = K;
    }

    /**
     * @param longDoubleTuple element to be added to the list
     * @return the boolean result of the add operation
     */
    @Override
    public boolean add(Tuple<Long, Double> longDoubleTuple) {

        boolean result = super.add(longDoubleTuple);

        //if the list has at least K elements, the threshold must be updated
        if(result && this.size() >= K){

            Iterator<Tuple<Long, Double>> iterator = iterator();
            int counter = 0;

            //traverse, in descending order, the list until the K-1th element is reached
            while(iterator.hasNext()){
                iterator.next();
                counter++;
                if(counter == K - 1){
                    break;
                }
            }

            //updates the threshold with the value of the Kth element
            this.threshold = iterator.next().getSecond();

        }
        return result;
    }

    /**
     * @return return the current threshold
     */
    public double getThreshold() {
        return threshold;
    }
}
