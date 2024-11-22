package it.unipi.dii.aide.mircv.utils;

import it.unipi.dii.aide.mircv.builder.InvertedIndexBuilder;
import it.unipi.dii.aide.mircv.model.DocumentEntry;
import it.unipi.dii.aide.mircv.model.LexiconEntry;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicInteger;

import static it.unipi.dii.aide.mircv.utils.Config.*;
import static it.unipi.dii.aide.mircv.utils.LexiconEntryConfig.*;
import static it.unipi.dii.aide.mircv.utils.DocumentIndexEntryConfig.*;



public class FileWriterUtility {

    public void writeInvertedIndexAndLexiconToFiles(InvertedIndexBuilder invertedIndexBuilder, int blockNumber) throws FileNotFoundException {
        //Write the inverted index's files into the block's files
        writeInvertedIndexToFile(invertedIndexBuilder,
                DOCIDS_BLOCK_PATH+blockNumber+".txt",
                FREQUENCIES_BLOCK_PATH +blockNumber+".txt");

        //Write the block's lexicon into the given file
        writeLexiconToFile(invertedIndexBuilder, LEXICON_BLOCK_PATH+blockNumber+".txt");

        System.out.println("Block "+blockNumber+"'s inverted index and lexicon written");

        //Clear the inverted index and lexicon data structure and call the garbage collector
        invertedIndexBuilder.clear();
    }

    public void writeLexiconToFile(InvertedIndexBuilder invertedIndexBuilder, String outputPath) {
        try (RandomAccessFile lexiconFile = new RandomAccessFile(outputPath, "rw")) {
            invertedIndexBuilder.getLexicon().getLexicon().forEach((key, lexiconEntry) -> {
                try {
                    writeLexiconEntryToFile(lexiconEntry, lexiconFile, key);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void writeInvertedIndexToFile(InvertedIndexBuilder invertedIndexBuilder, String outputPathDocIds, String outputPathFrequencies) throws FileNotFoundException {

        try (RandomAccessFile docIdBlock = new RandomAccessFile(outputPathDocIds, "rw");
             RandomAccessFile frequencyBlock = new RandomAccessFile(outputPathFrequencies, "rw")) {
            AtomicInteger currentOffsetDocId = new AtomicInteger(0);
            AtomicInteger currentOffsetFrequency = new AtomicInteger(0);

            invertedIndexBuilder.getInvertedIndex().getInvertedIndex().forEach((key, postingList) -> {

                //Set the current offsets to be written in the lexicon
                int offsetDocId = currentOffsetDocId.get();
                int offsetFrequency = currentOffsetFrequency.get();

                postingList.getPostingList().forEach((docId, freq) -> {
                    //Create the buffers for each element to be written
                    byte[] postingDocId = ByteBuffer.allocate(8).putLong(docId).array();
                    byte[] postingFreq = ByteBuffer.allocate(4).putInt(freq).array();

                    try {
                        //Append each element to the file, each one adds 4 bytes to the file
                        docIdBlock.write(postingDocId);
                        frequencyBlock.write(postingFreq);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    //Increment the current offset
                    currentOffsetDocId.addAndGet(8);
                    currentOffsetFrequency.addAndGet(4);
                });

                //Set the docId offset, the frequency offset, the posting list length of the term in the lexicon
                invertedIndexBuilder.getLexicon().getLexicon().get(key).getDocumentFrequency();
                invertedIndexBuilder.getLexicon().getLexicon().get(key).set(offsetDocId, offsetFrequency, postingList.getPostingList().size());
            });


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void writeLexiconEntryToFile(LexiconEntry termInfo, RandomAccessFile lexiconFile, String key) throws FileNotFoundException {

        //Fill with whitespaces to keep the length standard
        String tmp = Utils.leftpad(key, TERM_LENGTH);

        byte[] term = ByteBuffer.allocate(TERM_LENGTH).put(tmp.getBytes()).array();
        byte[] offsetDocId = ByteBuffer.allocate(OFFSET_DOCIDS_LENGTH).putLong(termInfo.getOffsetDocId()).array();
        byte[] offsetFrequency = ByteBuffer.allocate(OFFSET_FREQUENCIES_LENGTH).putLong(termInfo.getOffsetFrequency()).array();
        byte[] postingListLength = ByteBuffer.allocate(POSTING_LIST_LENGTH).putInt(termInfo.getPostingListLength()).array();

        try {
            lexiconFile.write(term);
            lexiconFile.write(offsetDocId);
            lexiconFile.write(offsetFrequency);
            lexiconFile.write(postingListLength);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void writeDocumentEntryToFile(long docId, DocumentEntry documentEntry, RandomAccessFile documentIndexFile){

        //Fill with whitespaces to keep the length standard
        String tmp = Utils.leftpad(documentEntry.getDocNo(), DOCNO_LENGTH);

        //Instantiating the ByteBuffer to write to the file
        byte[] docIdBytes = ByteBuffer.allocate(DOCID_LENGTH).putLong(docId).array();
        byte[] docNoBytes = ByteBuffer.allocate(DOCNO_LENGTH).put(tmp.getBytes()).array();
        byte[] docLenBytes = ByteBuffer.allocate(DOCLENGTH_LENGTH).putInt(documentEntry.getDocLength()).array();

        try {
            documentIndexFile.write(docIdBytes);
            documentIndexFile.write(docNoBytes);
            documentIndexFile.write(docLenBytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Write the statistics of the execution, in particular the number of blocks written and the total number of
     * documents parsed.
     * @param numberOfBlocks Number of blocks written
     * @param numberOfDocs Number of documents parsed in total
     */
    public void writeStatisticsToFile(int numberOfBlocks, int numberOfDocs, float avdl){

        //Object used to build the lexicon line into a string
        StringBuilder stringBuilder = new StringBuilder();

        //Buffered writer used to format the output
        BufferedWriter bufferedWriter;

        try {
            bufferedWriter = new BufferedWriter(new FileWriter(STATISTICS_PATH,false));

            //build the string
            stringBuilder
                    .append(numberOfBlocks).append("\n")
                    .append(numberOfDocs).append("\n")
                    .append(Math.round(avdl)).append("\n");

            //Write the string in the file
            bufferedWriter.write(stringBuilder.toString());

            //Close the writer
            bufferedWriter.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
