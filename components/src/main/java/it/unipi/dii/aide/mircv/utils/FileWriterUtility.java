package it.unipi.dii.aide.mircv.utils;

import it.unipi.dii.aide.mircv.builder.InvertedIndexBuilder;
import it.unipi.dii.aide.mircv.model.LexiconEntry;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicInteger;

import static it.unipi.dii.aide.mircv.utils.Config.DOCIDS_BLOCK_PATH;
import static it.unipi.dii.aide.mircv.utils.Config.FREQUENCIES_BLOCK_PATH;
import static it.unipi.dii.aide.mircv.utils.Config.LEXICON_BLOCK_PATH;
import static it.unipi.dii.aide.mircv.utils.LexiconEntryConfig.*;



public class FileWriterUtility {

    public void writeInvertedIndexAndLexiconToFiles(InvertedIndexBuilder invertedIndexBuilder, int blockNumber) throws FileNotFoundException {
        //Write the inverted index's files into the block's files
        writeInvertedIndexToFile(invertedIndexBuilder,
                DOCIDS_BLOCK_PATH+blockNumber+".txt",
                FREQUENCIES_BLOCK_PATH +blockNumber+".txt");

        //Write the block's lexicon into the given file
        writeLexiconToFile(invertedIndexBuilder, LEXICON_BLOCK_PATH+blockNumber+".txt");

        System.out.println("Block "+blockNumber+" written");

        //Clear the inverted index and lexicon data structure and call the garbage collector
        invertedIndexBuilder.clear();
    }

    public void writeLexiconToFile(InvertedIndexBuilder invertedIndexBuilder, String outputPath) throws FileNotFoundException {
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

    public void writeLexiconEntryToFile(LexiconEntry termInfo,RandomAccessFile lexiconFile, String key) throws FileNotFoundException {

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

    }
