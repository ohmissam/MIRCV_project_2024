package it.unipi.dii.aide.mircv.compressor;

import it.unipi.dii.aide.mircv.model.DocumentIndexEntry;
import it.unipi.dii.aide.mircv.model.SkipBlock;
import it.unipi.dii.aide.mircv.model.Statistics;
import it.unipi.dii.aide.mircv.model.Tuple;

import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;


import static it.unipi.dii.aide.mircv.utils.Utils.splitsLog128;
import static it.unipi.dii.aide.mircv.merger.IndexMerger.B;
import static it.unipi.dii.aide.mircv.merger.IndexMerger.K1;

public class Compressor {
    /**
     * Compress the given number using the Variable Byte Encoding.
     * @param number Number to be compressed
     * @return Array of bytes containing the code of the number
     */
    public static byte[] variableByteEncodeNumber(long number){

        //If the number is 0, we return directly the code in VB of 0, otherwise the algorithm doesn't work
        //In particular the log doesn't exist at 0, log(x) exists for x > 0
        if(number == 0){
            return new byte[]{(byte) 0x80};//In practice this case shouldn't happen, since a frequency is always > 0
        }

        //Retrieve the number of splits required to encode the number
        int numberOfBytes = splitsLog128(number);

        //Array to hold the encoded bytes
        byte[] bytes = new byte[numberOfBytes];

        //Write the number representation in big-endian order from the MSByte to the LSByte
        for(int i = numberOfBytes - 1; i >= 0; i--){

            //Prepend of the reminder of the division by 128 (retrieve the 7 LSB)
            byte b = (byte) (number % 128);
            bytes[i] = b;

            //Shift right the number by 7 position
            number /= 128;
        }

        //Set the control bit of the last byte to 1, to indicate that it is the last byte
        bytes[numberOfBytes - 1] += 128;

        //Return the encoded number
        return bytes;
    }

    public static byte[] variableByteEncodeDocId(ArrayList<Long> numbers, ArrayList<SkipBlock> skipBlocks){

        //Dimension of each skip block
        int skipBlocksLength = (int) Math.floor(Math.sqrt(numbers.size()));

        //Array to hold the bytes of the encoded numbers
        ArrayList<Byte> bytes = new ArrayList<>();

        //Counter for the number traversed currently, used to know if the cursor is in the position of a skip
        int counter = 0;

        //Accumulator to keep track of the starting byte of the current skip block (relative to its posting list offset)
        int offsetSkipBlocks = 0;

        //Accumulator to store the length of the current skip block
        int numberOfBytes = 0;

        //For each number in the list
        for (Long number : numbers) {

            //Increase the counter for the number of numbers processed
            counter++;

            //Encode each number and add it to the end of the array
            for(byte b : variableByteEncodeNumber(number)){
                bytes.add(b);

                //Increment the skip blocks length in bytes
                numberOfBytes++;
            }

            //If we're at a skip position, we create a new skip block
            if(counter%skipBlocksLength == 0 || counter == numbers.size()){

                //We pass the current starting offset, the length of the encoded block and the current number that is
                // for sure the greater seen until now
                skipBlocks.add(new SkipBlock(offsetSkipBlocks,numberOfBytes, number));

                //Set the starting offset for the next skip block
                offsetSkipBlocks += numberOfBytes;

                //Reset the variables for the next iteration
                numberOfBytes = 0;

            }

        }

        //Array used to convert the arrayList into a byte array
        byte[] result = new byte[bytes.size()];

        //For each byte in the arrayList put it in the array of bytes
        for(int i = 0; i < bytes.size(); i++){
            result[i] = bytes.get(i);
        }

        //Return the array of bytes
        return result;
    }


    public static byte[] variableByteEncodeFreq(ArrayList<Integer> frequencies, ArrayList<SkipBlock> skipBlocks, ArrayList<Long> docIds, Tuple<Double,Double> maxscores , RandomAccessFile documentIndex, Statistics statistics){

        //Dimension of each skip block
        int skipBlocksLength = (int) Math.floor(Math.sqrt(frequencies.size()));

        //Array to hold the bytes of the encoded numbers
        ArrayList<Byte> bytes = new ArrayList<>();

        //Counter for the number traversed currently, used to know if the cursor is in the position of a skip
        int counter = 0;

        //Accumulator to keep track of the starting byte of the current skip block (relative to its posting list offset)
        int offsetSkipBlocks = 0;

        //Accumulator to store the length of the current skip block
        int numberOfBytes = 0;

        Iterator<SkipBlock> skipBlocksIterator = skipBlocks.iterator();

        int maxFreq = 0;

        //To store the bm25 score for the current doc id
        double tf_currentBm25;

        //To store the max score for bm25
        double tf_maxScoreBm25 = 0;

        //For each number in the list
        for (Integer freq : frequencies) {

            //Retrieve the maximum to compute the TFIDF term upper bound
            if(freq > maxFreq){
                maxFreq = freq;
            }

            //Compute the bm25 scoring for the current document
            tf_currentBm25 = freq/ (K1 * ((1-B) + B * ( (double) DocumentIndexEntry.getDocLenFromDisk(documentIndex, docIds.get(counter)) / statistics.getAvdl()) + freq));

            //If the current max score for bm25 is greater than the previous score, update it
            if(tf_currentBm25 > tf_maxScoreBm25){
                tf_maxScoreBm25 = tf_currentBm25;
            }

            //Increase the counter for the number of numbers processed
            counter++;

            //Encode each number and add it to the end of the array
            for(byte b : variableByteEncodeNumber(freq)){
                bytes.add(b);

                //Increment the skip blocks length in bytes
                numberOfBytes++;
            }

            //If we're at a skip position, then we complete the information about the skip previously created
            if(counter%skipBlocksLength == 0 || counter == frequencies.size()){

                //We pass the current starting offset and the current number that is for sure the greater seen until now
                if(skipBlocksIterator.hasNext()){

                    //Set the starting offset of the skip block and the length of the encoded frequencies
                    skipBlocksIterator.next().setFreqInfo(offsetSkipBlocks, numberOfBytes);
                }
                //Set the starting offset for the next skip block
                offsetSkipBlocks += numberOfBytes;

                //Reset the variables for the next iteration
                numberOfBytes = 0;

            }
        }

        //Set the max score parameters
        maxscores.setFirst((double) maxFreq);
        maxscores.setSecond(tf_maxScoreBm25);

        //Array used to convert the arrayList into a byte array
        byte[] result = new byte[bytes.size()];

        //For each byte in the arrayList put it in the array of bytes
        for(int i = 0; i < bytes.size(); i++){
            result[i] = bytes.get(i);
        }

        //Return the array of bytes
        return result;
    }

    public static ArrayList<Long> variableByteDecodeLong(byte[] bytes){

        //Array to hold the decoded numbers
        ArrayList<Long> numbers = new ArrayList<>();

        //Accumulator for the current decoded number
        long number = 0;

        //For each byte in the array
        for (byte aByte : bytes) {

            //We use the mask 0x80 = 1000 0000, to check if the MSB of the byte is 1
            if ((aByte & 0x80) == 0x00) {
                //The MSB is 0, then we're not at the end of the sequence of bytes of the code
                number = number * 128 + aByte;
            } else {
                //The MSB is 1, then we're at the end

                //Add to the accumulator number*128 + the integer value in aByte discarding the 1 in the MSB
                number = number * 128 + (aByte &  0x7F);

                //Add the decoded number to the list of numbers
                numbers.add(number);

                //Reset the accumulator
                number = 0;
            }
        }

        //Return the list of numbers
        return numbers;
    }

    public static ArrayList<Integer> variableByteDecode(byte[] bytes){

        //Array to hold the decoded numbers
        ArrayList<Integer> numbers = new ArrayList<>();

        //Accumulator for the current decoded number
        int number = 0;

        //For each byte in the array
        for (byte aByte : bytes) {

            //We use the mask 0x80 = 1000 0000, to check if the MSB of the byte is 1
            if ((aByte & 0x80) == 0x00) {
                //The MSB is 0, then we're not at the end of the sequence of bytes of the code
                number = number * 128 + aByte;
            } else {
                //The MSB is 1, then we're at the end

                //Add to the accumulator number*128 + the integer value in aByte discarding the 1 in the MSB
                number = number * 128 + (aByte &  0x7F);

                //Add the decoded number to the list of numbers
                numbers.add(number);

                //Reset the accumulator
                number = 0;
            }
        }

        //Return the list of numbers
        return numbers;
    }
}
