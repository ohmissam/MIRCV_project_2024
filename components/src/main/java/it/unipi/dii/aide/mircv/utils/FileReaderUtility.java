package it.unipi.dii.aide.mircv.utils;

import it.unipi.dii.aide.mircv.model.BlockLexiconEntry;
import it.unipi.dii.aide.mircv.compressor.Compressor;
import it.unipi.dii.aide.mircv.model.LexiconEntry;
import it.unipi.dii.aide.mircv.model.SkipBlock;
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
     * Reads the posting list's skip blocks from the given file, starting from offset it will read the
     * number of skip blocks indicated by the given length parameter.
     * @param randomAccessFileSkipBlocks RandomAccessFile of the skip blocks' file
     * @param offset offset starting from where to read the skip blocks'
     * @param length number of skip blocks to read
     */
    public static ArrayList<SkipBlock> readPostingListSkipBlocks(RandomAccessFile randomAccessFileSkipBlocks, long offset, int length) {

        //ArrayList to store the posting list's frequencies
        ArrayList<SkipBlock> list = new ArrayList<>();

        try {

            //Set the file pointer to the start of the posting list
            randomAccessFileSkipBlocks.seek(offset);

        } catch (IOException e) {
            System.err.println("[ReadPostingListSkipBlocks] Exception during seek");
            throw new RuntimeException(e);
        }

        //Read the skip blocks from the file
        for(int i = 0; i < length; i ++) {
            try {

                //Read the next skip block from the file and add it to the result list
                list.add(new SkipBlock(
                        randomAccessFileSkipBlocks.readLong(), //Docids offset
                        randomAccessFileSkipBlocks.readInt(),  //Docids length
                        randomAccessFileSkipBlocks.readLong(), //Frequencies offset
                        randomAccessFileSkipBlocks.readInt(),  //Frequencies length
                        randomAccessFileSkipBlocks.readLong()) //Max docid in the skip block
                );

            } catch (IOException e) {
                System.err.println("[ReadPostingListSkipBlocks] Exception during read");
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

    /**
     * Read the next term from the lexicon file.
     * @param offset starting offset of the next term to be read
     * @return The next term from the lexicon file.
     */
    public static LexiconEntry readNextTerm(RandomAccessFile lexiconFile, int offset){
        //Array of bytes in which put the term
        byte[] termBytes = new byte[MergedLexiconEntryConfig.TERM_LENGTH];

        //String containing the term
        String term;

        //TermInfo containing the term information to be returned
        LexiconEntry lexiconEntry;

        try {
            //Set the file pointer to the start of the lexicon entry
            lexiconFile.seek(offset);

            //Read the first 48 containing the term
            lexiconFile.readFully(termBytes, 0, MergedLexiconEntryConfig.TERM_LENGTH);

            //Convert the bytes to a string and trim it
            term = new String(termBytes, Charset.defaultCharset()).trim();

            //Instantiate the TermInfo object reading the next 3 integers from the file
            lexiconEntry = new LexiconEntry(term,   //Term
                    lexiconFile.readLong(),  //Offset docids file
                    lexiconFile.readLong(),  //Offset frequencies file
                    lexiconFile.readDouble(), //idf
                    lexiconFile.readInt(),  //Length in bytes of the docids list
                    lexiconFile.readInt(),  //Length in bytes of the frequencies list
                    lexiconFile.readInt(),  //Length of the term's posting list
                    lexiconFile.readLong(), //Offset of the skipBlocks in the skipBlocks file
                    lexiconFile.readInt(),  //Number of skipBlocks
                    lexiconFile.readInt(), //TFIDF term upper bound
                    lexiconFile.readInt()  //BM25 term lower bound
            );


            return lexiconEntry;

        } catch (IOException e) {
            //System.err.println("[ReadNextTermInfo] EOF reached while reading the next lexicon entry");
            return null;
        }
    }



    /**
     * Reads the posting list's ids from the given inverted index file, starting from offset it will read the number
     * of docIds indicated by the given length parameter. It assumes that the file is compressed using VBE.
     * @param randomAccessFileDocIds RandomAccessFile of the docIds block file
     * @param offset offset starting from where to read the posting list
     * @param length length of the bytes of the encoded posting list
     */
    public static ArrayList<Long> readPostingListDocIdsCompressed(RandomAccessFile randomAccessFileDocIds, long offset, int length) {

        byte[] docidsByte = new byte[length];

        try {

            //Set the file pointer to the start of the posting list
            randomAccessFileDocIds.seek(offset);

            randomAccessFileDocIds.readFully(docidsByte, 0, length);

            return Compressor.variableByteDecodeLong(docidsByte);

        } catch (IOException e) {
            System.err.println("[ReadPostingListDocIds] Exception during seek");
            throw new RuntimeException(e);
        }
    }


    /**
     * Reads the posting list's frequencies from the given inverted index file, starting from offset it will read the number
     * of docIds indicated by the given length parameter. It assumes that the file is compressed using VBE.
     * @param randomAccessFileFreq RandomAccessFile of the frequencies file
     * @param offset offset starting from where to read the posting list
     * @param length length of the bytes of the encoded posting list
     */
    public static ArrayList<Integer> readPostingListFrequenciesCompressed(RandomAccessFile randomAccessFileFreq, long offset, int length) {

        byte[] docidsByte = new byte[length];

        try {

            //Set the file pointer to the start of the posting list
            randomAccessFileFreq.seek(offset);

            randomAccessFileFreq.readFully(docidsByte, 0, length);

            return Compressor.variableByteDecode(docidsByte);

        } catch (IOException e) {
            System.err.println("[ReadPostingListDocIds] Exception during seek");
            throw new RuntimeException(e);
        }
    }

    /*TODO
       readPostingListDocIdsCompressed
       readPostingListFrequenciesCompressed
    */
}
