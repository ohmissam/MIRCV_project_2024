package it.unipi.dii.aide.mircv.merger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import static it.unipi.dii.aide.mircv.compressor.Compressor.variableByteEncodeDocId;
import static it.unipi.dii.aide.mircv.compressor.Compressor.variableByteEncodeFreq;
import static it.unipi.dii.aide.mircv.utils.Config.*;
import static it.unipi.dii.aide.mircv.utils.BlockLexiconEntryConfig.*;
import it.unipi.dii.aide.mircv.model.*;
import it.unipi.dii.aide.mircv.utils.FileReaderUtility;
import it.unipi.dii.aide.mircv.utils.FileWriterUtility;
import it.unipi.dii.aide.mircv.utils.SkipBlockConfig;


public class IndexMerger {
    public static final double K1 = 1.5;
    public static final double B = 0.75;

    /**
     * This method merges the inverted index and the lexicon blocks into one single file.
     */
    public static void merge() {

        System.out.println("[MERGER] Merging lexicon blocks and inverted index blocks...");

        //Retrieve the time at the beginning of the computation
        long begin = System.nanoTime();

        //Retrieve the blocks statistics
        Statistics statistics = Statistics.readStatistics();

        int NUMBER_OF_BLOCKS = statistics.getNumberOfBlocks();

        //Arrays of random access files, for docIds, frequencies and lexicon blocks
        RandomAccessFile[] randomAccessFileDocIds = initializeRandomAccessFiles(DOCIDS_BLOCK_PATH, NUMBER_OF_BLOCKS, "DocIds Block");
        RandomAccessFile[] randomAccessFilesFrequencies = initializeRandomAccessFiles(FREQUENCIES_BLOCK_PATH, NUMBER_OF_BLOCKS, "Frequencies Block");
        RandomAccessFile[] randomAccessFilesLexicon = initializeRandomAccessFiles(LEXICON_BLOCK_PATH, NUMBER_OF_BLOCKS, "Lexicon Block");

        //Create a stream for the lexicon file, the docids file and the frequencies file for merged results
        RandomAccessFile lexiconFile = openOrCreateFile(LEXICON_FILE_PATH, "rw");
        RandomAccessFile docIdsFile = openOrCreateFile(INVINDEX_DOC_IDS_FILE_PATH, "rw");
        RandomAccessFile frequenciesFile = openOrCreateFile(INVINDEX_FREQUENCIES_FILE_PATH, "rw");
        RandomAccessFile skipBlocksFile = openOrCreateFile(SKIP_BLOCKS_FILE_PATH, "rw");
        RandomAccessFile documentIndex = openOrCreateFile(DOCINDEX_FILE_PATH, "r");


        //Accumulators to hold the current offset
        long docIdsOffset = 0;
        long frequenciesOffset = 0;
        long skipBlocksOffset = 0;
        byte[] docIdsCompressed;
        byte[] frequenciesCompressed;
        //Array of the current offset reached in each lexicon block
        int[] lexicon_offsets = new int[NUMBER_OF_BLOCKS];


        //Array of boolean, each i-th entry is true, if the i-th block has reached the end of the lexicon block file
        boolean[] endOfBlock = new boolean[NUMBER_OF_BLOCKS];

        //Set each offset equal to 0, the starting offset of each lexicon block
        //Set each boolean equal to false, at the beginning no block has reached the end
        for (int block_index = 0; block_index < NUMBER_OF_BLOCKS; block_index++) {
            lexicon_offsets[block_index] = 0;
            endOfBlock[block_index] = false;
        }

        //String to keep the min term among all the current terms in each lexicon block, it is used to determine the
        // term of which the posting lists must be merged
        String minTerm = null;


        // An array where each element is a Map.Entry<String, LexiconEntry>.
        // Each entry maps a term (String) to its corresponding LexiconEntry object.
        // This array is used to store the current lexicon entry for each block file being processed during the merging phase.
        Map.Entry<String, BlockLexiconEntry>[] curTermArray = new Map.Entry[NUMBER_OF_BLOCKS];

        //Contains the list of all the blocks containing the current min term
        LinkedList<Integer> blocksWithMinTerm = new LinkedList<>();

        //Array to store the docIds and frequencies of the posting list of the current min term in the current block
        ArrayList<Long> docIds = new ArrayList<>();
        ArrayList<Integer> frequencies = new ArrayList<>();

        //Array to store the information about the skipBlocks
        ArrayList<SkipBlock> skipBlocks = new ArrayList<>();

        //Read the first term of each lexicon block
        for (int i = 0; i < curTermArray.length; i++) {
            curTermArray[i] = FileReaderUtility.readNextBlockLexiconEntry(randomAccessFilesLexicon[i],lexicon_offsets[i]);
            if(curTermArray[i] == null) {
                endOfBlock[i] = true;
            }
            //Update the offset to the offset of the next file to be read
            lexicon_offsets[i] += LEXICON_BLOCK_ENTRY_SIZE;
        }

        long numberOfTermsElaborated = 1;
        //Iterate over all the lexicon blocks, until the end of the lexicon block file is reached for each block
        while(!endOfAllFiles(endOfBlock, NUMBER_OF_BLOCKS)) {
            numberOfTermsElaborated++;
            if(numberOfTermsElaborated%25000 == 0){
                System.out.println("[MERGER] Processing time: " + (System.nanoTime() - begin)/1000000000+ "s. Processed " + numberOfTermsElaborated + " terms");
            }

            //System.out.println("[MERGER] Search the current min term in the lexicon block files");

            //For each block read the next term
            for(int block_index = 0; block_index < NUMBER_OF_BLOCKS; block_index++) {

                //Avoid to read from the block if the end of the block is reached
                if(endOfBlock[block_index]) {
                    continue;
                }

                //If the current term is the lexicographically smaller than the min term, then update the min term.
                if (minTerm == null || curTermArray[block_index].getKey().compareTo(minTerm) < 0) {
                    minTerm = curTermArray[block_index].getKey();
                    blocksWithMinTerm.clear();
                    blocksWithMinTerm.add(block_index);
                } else if (curTermArray[block_index].getKey().compareTo(minTerm) == 0) {
                    blocksWithMinTerm.add(block_index);
                }

            }//At this point we have the current min term.

            //Check if we've reached the and of the merge.
            if(endOfAllFiles(endOfBlock, NUMBER_OF_BLOCKS)) {
                System.out.println("END OF ALL FILES");
                break;
            }

//            System.out.println("----------- TERM: " + minTerm + " -----------");
//            System.out.println("contained in block" + blocksWithMinTerm);

            //Merge the posting lists of the current min term in the blocks containing the term
            for (Integer blockWithMinTerm : blocksWithMinTerm) {
                BlockLexiconEntry curLexiconEntry = curTermArray[blockWithMinTerm].getValue();

                //Append the current term docIds to the docIds accumulator
                docIds.addAll(FileReaderUtility.readPostingListDocIds(randomAccessFileDocIds[blockWithMinTerm], curLexiconEntry.getOffsetDocId(), curLexiconEntry.getPostingListLength()));

                //System.out.println("Current docIds: " + docIds);

                //Append the current term frequencies to the frequencies accumulator
                frequencies.addAll(FileReaderUtility.readPostingListFrequencies(randomAccessFilesFrequencies[blockWithMinTerm], curLexiconEntry.getOffsetFrequency(), curLexiconEntry.getPostingListLength()));

                //System.out.println("Current term frequencies: " + frequencies);

                //Read the lexicon entry from the current block and move the pointer of the file to the next term
                curTermArray[blockWithMinTerm] = FileReaderUtility.readNextBlockLexiconEntry(randomAccessFilesLexicon[blockWithMinTerm], lexicon_offsets[blockWithMinTerm]);

                //Check if the end of the block is reached or a problem during the reading occurred
                if(curTermArray[blockWithMinTerm] == null) {
                    if(IS_DEBUG_MODE) {
                        System.out.println("[DEBUG MODE] Block " + blockWithMinTerm + " has reached the end of the file");
                    }

                    endOfBlock[blockWithMinTerm] = true;
                    continue;
                }

                //Increment the offset of the current block to the starting offset of the next term
                lexicon_offsets[blockWithMinTerm] +=
                        TERM_LENGTH +
                        OFFSET_DOCIDS_LENGTH +
                        OFFSET_FREQUENCIES_LENGTH +
                        POSTING_LIST_LENGTH;
            }
            //Maximum term frequency
            int maxFreq = 0;

            //Maximum tf for bm25
            double tf_maxScoreBm25 = 0;

            if(ENABLE_COMPRESSION){
                System.out.println("[DEBUG] Start COMPRESSION");


                Tuple<Double, Double> maxscoreTuple = new Tuple<>(0.0,0.0);

                //Compress the list of docIds using VBE and create the list of skip blocks for the list of docids
                docIdsCompressed = variableByteEncodeDocId(docIds, skipBlocks);

                //Compress the list of frequencies using VBE and update the frequencies information in the skip blocks
                frequenciesCompressed = variableByteEncodeFreq(frequencies, skipBlocks, docIds, maxscoreTuple, documentIndex, statistics);

                //Write the docIds and frequencies of the current term in the respective files
                try {
                    docIdsFile.write(docIdsCompressed);
                    frequenciesFile.write(frequenciesCompressed);
                } catch (IOException e) {
                    System.err.println("[MERGER] File not found: " + e.getMessage());
                    throw new RuntimeException(e);
                }

                //Compute idf
                double idf = Math.log(statistics.getNumberOfDocuments()/ (double)docIds.size())/Math.log(2);

                //Compute the tfidf term upper bound
                int tfidfTermUpperBound = (int) Math.ceil((1 + Math.log(maxscoreTuple.getFirst()) / Math.log(2))*idf);

                //Compute the bm25 term upper bound
                int bm25TermUpperBound = (int) Math.ceil(maxscoreTuple.getSecond()*idf);

                BlockLexicon blockLexicon=new BlockLexicon();
                BlockLexiconEntry blockLexiconEntry=new BlockLexiconEntry(docIdsOffset,frequenciesOffset,docIds.size());
                HashMap<String,BlockLexiconEntry> hashMap=new HashMap<>();
                hashMap.put(minTerm,blockLexiconEntry);
                blockLexicon.setLexicon(hashMap);
/*
                lexiconEntry = new TermInfo(
                        minTerm,                     //Term
                        docIdsOffset,                //offset in the docids file in which the docids list starts
                        frequenciesOffset,           //offset in the frequencies file in which the frequencies list starts
                        idf,                         //idf
                        docIdsCompressed.length,     //length in bytes of the compressed docids list
                        frequenciesCompressed.length,//length in bytes of the compressed frequencies list
                        docIds.size(),               //Length of the posting list of the current term
                        skipBlocksOffset,            //Offset of the SkipBlocks in the SkipBlocks file
                        skipBlocks.size(),           //number of SkipBlocks
                        tfidfTermUpperBound,         //term upper bound for the tfidf
                        bm25TermUpperBound           //term upper bound for the bm25
                );

                //For DEBUG
                if(debug && j%25000 == 0) {
                    System.out.println("[DEBUG] Current lexicon entry: " + lexiconEntry);
                    System.out.println("[DEBUG] Number of skipBlocks created: " + skipBlocks.size());
                }
*/
                blockLexicon.writeToFile(lexiconFile, lexiconEntry);

                docIdsOffset += docIdsCompressed.length;
                frequenciesOffset += frequenciesCompressed.length;

                System.out.println("[DEBUG] END COMPRESSION");

            }
            else{ //no compression
                try {
                    //Write the docIds and frequencies of the current term in the respective files
                    //Dimension of each skip block
                    int skipBlocksLength = (int) Math.floor(Math.sqrt(docIds.size()));

                    //Number of postings
                    int skipBlocksElements = 0;

                    //To store the bm25 score for the current doc id
                    double tf_currentBm25;

                    //Write the docids and frequencies in their respective files and create the skip blocks
                    for(int i=0; i < docIds.size(); i++) {

                        //Retrieve the maximum to compute the TFIDF term upper bound
                        if(frequencies.get(i) > maxFreq){
                            maxFreq = frequencies.get(i);
                        }

                        //Compute the bm25 scoring for the current document
                        tf_currentBm25 = frequencies.get(i)/ (K1 * ((1-B) + B * ( (double) FileReaderUtility.readDocLenFromDisk(documentIndex, docIds.get(i)) / statistics.getAvdl()) + frequencies.get(i)));

                        if(tf_currentBm25 > tf_maxScoreBm25){
                            tf_maxScoreBm25 = tf_currentBm25;
                        }


                        //Write the docIds as a long to the end of the docIds file
                        docIdsFile.writeLong(docIds.get(i));

                        //Write the frequencies as an integer to the end of the frequencies file
                        frequenciesFile.writeInt(frequencies.get(i));

                        //If we're at a skip position, we create a new skip block
                        if(((i+1)%skipBlocksLength == 0) || ((i + 1) == docIds.size())){

                            //if the size of the skip block is less than skipBlocksLength then used the reminder,
                            // to get the actual dimension of the skip block, since if we're at the end we can have less
                            // than skipBlockLength postings
                            // Since we don't have compression the lengths of docids and frequencies skip blocks are the same
                            int currentSkipBlockSize = ((i + 1) % skipBlocksLength == 0) ? skipBlocksLength : ((i+1) % skipBlocksLength);

                            //Creation of the skip block
                            skipBlocks.add(new SkipBlock(
                                    (long) skipBlocksElements *Long.BYTES,
                                    currentSkipBlockSize,
                                    (long) skipBlocksElements *Integer.BYTES,
                                    currentSkipBlockSize,
                                    docIds.get(i)
                            ));

                            //Increment the number of elements seen until now, otherwise we're not able to obtain the first offset
                            // equal to 0
                            skipBlocksElements += currentSkipBlockSize;
                        }

                    }


                } catch (IOException e) {
                    System.err.println("[MERGER] File not found: " + e.getMessage());
                    throw new RuntimeException(e);
                }

                //Compute idf
                double idf = Math.log(statistics.getNumberOfDocuments()/ (double)docIds.size())/Math.log(2);

                //Compute the tfidf term upper bound
                int tfidfTermUpperBound = (int) Math.ceil((1 + Math.log(maxFreq) / Math.log(2))*idf);

                //Compute the bm25 term upper bound
                int bm25TermUpperBound = (int) Math.ceil(tf_maxScoreBm25*idf);


                //Instantiate a new LexiconEntry object with the current term information, here we use the information in
                //the docids and frequencies objects
                MergedLexiconEntry mergedLexiconEntry = new MergedLexiconEntry(
                        minTerm,
                        docIdsOffset,                //offset in the docids file in which the docids list starts
                        frequenciesOffset,           //offset in the frequencies file in which the frequencies list starts
                        idf,                         //idf of the term for future scoring
                        docIds.size(),               //length in number of long in the docids list
                        frequencies.size(),          //length number of integers in the frequencies list
                        docIds.size(),               //Length of the posting list of the current term
                        skipBlocksOffset,            //Offset of the SkipBlocks in the SkipBlocks file
                        skipBlocks.size(),           //number of SkipBlocks
                        tfidfTermUpperBound,         //term upper bound for the tfidf
                        bm25TermUpperBound);          //term upper bound for the bm25


                //For DEBUG
                if(IS_DEBUG_MODE && numberOfTermsElaborated%25000 == 0) {
                    System.out.println("[DEBUG] Current lexicon entry: " + mergedLexiconEntry);
                    System.out.println("[DEBUG] Number of skipBlocks created: " + skipBlocks.size());
                }

                FileWriterUtility.writeMergedLexiconEntryToFile(lexiconFile, mergedLexiconEntry);

                docIdsOffset += 8L*docIds.size();
                frequenciesOffset += 4L*frequencies.size();

            }


            for(SkipBlock s : skipBlocks){
                FileWriterUtility.writeSkipBlockToFile(s, skipBlocksFile);
                skipBlocksOffset += SkipBlockConfig.SKIP_BLOCK_LENGTH;
            }

            //Clear the accumulators for the next iteration
            docIds.clear();
            frequencies.clear();
            skipBlocks.clear();
            minTerm = null; //Otherwise it will be always the first min term found at the beginning of the merge
            blocksWithMinTerm.clear(); //Clear the list of blocks with the min term
        }

        System.out.println("[MERGER] Closing the streams of the files. Analyzed " + numberOfTermsElaborated + " terms");

        try {
            //Close the streams of the files
            for (int i = 0; i < NUMBER_OF_BLOCKS; i++) {
                randomAccessFileDocIds[i].close();
                randomAccessFilesFrequencies[i].close();
                randomAccessFilesLexicon[i].close();
            }

            lexiconFile.close();
            docIdsFile.close();
            frequenciesFile.close();

        } catch (RuntimeException | IOException e) {
            System.err.println("[MERGER] File not found: " + e.getMessage());
            throw new RuntimeException(e);
        }

        if(deleteBlocks(NUMBER_OF_BLOCKS)){
            System.out.println("[MERGER] Blocks deleted successfully");
        }

        System.out.println("[MERGER] Total processing time: " + (System.nanoTime() - begin)/1000000000+ "s");
        System.out.println("[MERGER] MERGING PROCESS COMPLETE");
    }


    /**
     * Check if all the files have reached the end of the file, and if so return true, otherwise return false
     * @param endOfBlocks array of boolean indicating if the files have reached the end of the file
     * @param numberOfBlocks number of blocks, it is the length of the array
     * @return true if all the files have reached the end of the file, and if so return true, otherwise return false
     */
    private static boolean endOfAllFiles(boolean[] endOfBlocks, int numberOfBlocks) {

        //For each block check if it has reached the end of the file
        for(int i = 0; i < numberOfBlocks; i++) {
            if(!endOfBlocks[i])
                //At least one file has not reached the end of the file
                return false;
        }
        //All the files have reached the end of the file
        return true;
    }

    /**
     * Delete the partial block of lexicon and inverted index
     * @param numberOfBlocks number of partial blocks
     * @return true if all the files are successfully deleted, false otherwise
     */
    private static boolean deleteBlocks(int numberOfBlocks) {
        File file;
        for (int i = 0; i < numberOfBlocks; i++) {
            file = new File(DOCIDS_BLOCK_PATH+(i+1)+".txt");
            if(!file.delete())
                return false;
            file = new File(FREQUENCIES_BLOCK_PATH+(i+1)+".txt");
            if(!file.delete())
                return false;
            file = new File(LEXICON_BLOCK_PATH+(i+1)+".txt");
            if(!file.delete())
                return false;
        }
        return true;
    }

    private static RandomAccessFile[] initializeRandomAccessFiles(String path, int numberOfBlocks, String debugMessage) {
        RandomAccessFile[] randomAccessFiles = new RandomAccessFile[numberOfBlocks];
        for (int i = 0; i < numberOfBlocks; i++) {
            try {
                randomAccessFiles[i] = new RandomAccessFile(path + (i + 1) + ".txt", "r");
                if (IS_DEBUG_MODE) {
                    System.out.println("[DEBUG MODE] " + debugMessage + " " + (i+1) + " opened");
                }
            } catch (FileNotFoundException e) {
                System.err.println("[MERGER] File not found: " + path + (i + 1) + ".txt");
                throw new RuntimeException(e);
            }
        }
        return randomAccessFiles;
    }

    private static RandomAccessFile openOrCreateFile(String filePath, String mode) {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                boolean created = file.createNewFile();
                if (created) {
                    System.out.println("[MERGER] File created: " + filePath);
                } else {
                    System.err.println("[MERGER] Failed to create file: " + filePath);
                }
            }
            return new RandomAccessFile(file, mode);
        } catch (IOException e) {
            System.err.println("[MERGER] Error accessing file: " + filePath);
            throw new RuntimeException(e);
        }
    }


}
