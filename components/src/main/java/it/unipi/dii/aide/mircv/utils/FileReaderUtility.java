package it.unipi.dii.aide.mircv.utils;

import it.unipi.dii.aide.mircv.model.BlockLexiconEntry;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Map;

import static it.unipi.dii.aide.mircv.utils.BlockLexiconEntryConfig.TERM_LENGTH;
import static it.unipi.dii.aide.mircv.utils.DocumentIndexEntryConfig.*;

public class FileReaderUtility {
    /**
     * Reads the posting list's ids from the given inverted index file, starting from offset it will read the number
     * of docIds indicated by the given length parameter.
     * @param randomAccessFileDocIds RandomAccessFile of the docIds block file
     * @param offset offset starting from where to read the posting list
     * @param length length of the posting list to be read
     */
    public static ArrayList<Long> readPostingListDocIds(RandomAccessFile randomAccessFileDocIds, long offset, int length) {

        //ArrayList to store the posting list's ids
        ArrayList<Long> list = new ArrayList<>();

        try {

            //Set the file pointer to the start of the posting list
            randomAccessFileDocIds.seek(offset);

        } catch (IOException e) {
            System.err.println("[ReadPostingListDocIds] Exception during seek");
            throw new RuntimeException(e);
        }

        //Read the docIds from the file
        for(int i = 0; i < length; i ++) {
            try {

                //Read the docId and add it to the list
                list.add(randomAccessFileDocIds.readLong());

            } catch (IOException e) {
                System.err.println("[ReadPostingListDocIds] Exception during read");
                throw new RuntimeException(e);
            }
        }

        //Return the list
        return list;
    }

    /**
     * Reads the posting list's frequencies from the given inverted index file, starting from offset it will read the
     * number of frequencies indicated by the given length parameter.
     * @param randomAccessFileFrequencies RandomAccessFile of the frequencies block file
     * @param offset offset starting from where to read the posting list
     * @param length length of the posting list to be read
     */
    public static ArrayList<Integer> readPostingListFrequencies(RandomAccessFile randomAccessFileFrequencies, long offset, int length) {

        //ArrayList to store the posting list's frequencies
        ArrayList<Integer> list = new ArrayList<>();

        try {

            //Set the file pointer to the start of the posting list
            randomAccessFileFrequencies.seek(offset);

        } catch (IOException e) {
            System.err.println("[ReadPostingListFrequencies] Exception during seek");
            throw new RuntimeException(e);
        }

        //Read the frequencies from the file
        for(int i = 0; i < length; i ++) {
            try {

                //Read the frequency and add it to the list
                list.add(randomAccessFileFrequencies.readInt());

            } catch (IOException e) {
                System.err.println("[ReadPostingListFrequencies] Exception during read");
                throw new RuntimeException(e);
            }
        }

        //Return the list
        return list;
    }

    /**
     * Reads the next lexicon entry from the given lexicon block file, starting from offset it will read the first 60
     * bytes, then if resetOffset is true, it will reset the offset to the value present ate the beginning, otherwise it
     * will keep the cursor as it is after the read of the entry.
     * @param randomAccessFileLexicon RandomAccessFile of the lexicon block file
     * @param offset offset starting from where to read the lexicon entry
     */
    public static Map.Entry<String, BlockLexiconEntry> readNextBlockLexiconEntry(RandomAccessFile randomAccessFileLexicon, int offset) {

        //Array of bytes in which put the term
        byte[] termBytes = new byte[TERM_LENGTH]; //48 bytes

        //String containing the term
        String term;

        //LexiconEntry containing the term information to be returned
        BlockLexiconEntry blockLexiconEntry;

        try {
            //Set the file pointer to the start of the lexicon entry
            randomAccessFileLexicon.seek(offset);

            //Read the first 48 containing the term
            randomAccessFileLexicon.readFully(termBytes, 0, TERM_LENGTH);

            //Convert the bytes to a string and trim it
            term = new String(termBytes, Charset.defaultCharset()).trim();

            //Instantiate the LexiconEntry object reading the next 3 integers from the file
            blockLexiconEntry = new BlockLexiconEntry(randomAccessFileLexicon.readLong(), randomAccessFileLexicon.readLong(), randomAccessFileLexicon.readInt());

            return new AbstractMap.SimpleEntry<>(term, blockLexiconEntry);

        } catch (IOException e) {
            //System.err.println("[ReadNextTermInfo] EOF reached while reading the next lexicon entry");
            return null;
        }
    }

    /**
     * Read from the document index the document index entry related to the given doc id
     * @param documentIndexFile random access file containing the document index
     * @param docId document id of which we want to retrieve the entry
     * @return the document index entry associated to the doc id
     */
    public static int readDocLenFromDisk(RandomAccessFile documentIndexFile, long docId){

        //Accumulator for the current offset in the file
        long offset = (docId - 1)*DOCUMENT_INDEX_ENTRY_LENGTH + DOCID_LENGTH + DOCNO_LENGTH;

        try {
            //Move to the correct offset
            documentIndexFile.seek(offset);

            //Read the length of the document, 4 bytes starting from the offset
            return documentIndexFile.readInt();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
