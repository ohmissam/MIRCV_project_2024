package it.unipi.dii.aide.mircv.model;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;
import java.util.HashMap;
import it.unipi.dii.aide.mircv.utils.Config;
import it.unipi.dii.aide.mircv.utils.MergedLexiconEntryConfig;

public class MergedLexicon {
    private HashMap<String, MergedLexiconEntry> lexicon;
    private RandomAccessFile lexiconFile;


    public MergedLexicon() {
        this.lexicon = new HashMap<>();
    }

    public HashMap<String, MergedLexiconEntry> getLexicon() {
        return lexicon;
    }

    /**
     * Load the lexicon in memory.
     * @return
     */
    public void loadMergedLexicon() {
        System.out.println("[LEXICON LOADER] Lexicon loading...");
        try {
            //Start the stream from the lexicon file
            lexiconFile = new RandomAccessFile(Config.LEXICON_FILE_PATH, "r");

            //Accumulator for the current offset in the file
            int offset = 0;

            //Accumulator for the current lexiconEntry reading
            MergedLexiconEntry lexiconEntry;

            //While we're not at the end of the file
            while (offset < lexiconFile.length()) {

                //Read the next lexiconEntry from the file starting at the current offset
                lexiconEntry = readNextTerm(offset);

                //If the lexiconEntry is not null (no problem encountered, or we aren't at the end of the file)
                if (lexiconEntry!= null){

                    //Insert the lexiconEntry into the HashMap
                    this.lexicon.put(lexiconEntry.getTerm(), lexiconEntry);

                    //Increment the offset
                    offset += MergedLexiconEntryConfig.MERGED_LEXICON_ENTRY_LENGTH;
                }
            }

            System.out.println("[LEXICON LOADER] Lexicon loaded");

        } catch (Exception e) {
            System.out.println("[LEXICON LOADER] Error loading lexicon: " + e.getMessage());
            e.printStackTrace();
        }

    }

    /**
     * Read the next term from the lexicon file.
     * @param offset starting offset of the next term to be read
     * @return The next term from the lexicon file.
     */
    private MergedLexiconEntry readNextTerm(int offset){
        //Array of bytes in which put the term
        byte[] termBytes = new byte[MergedLexiconEntryConfig.TERM_LENGTH];

        //String containing the term
        String term;

        //TermInfo containing the term information to be returned
        MergedLexiconEntry lexiconEntry;

        try {
            //Set the file pointer to the start of the lexicon entry
            lexiconFile.seek(offset);

            //Read the first 48 containing the term
            lexiconFile.readFully(termBytes, 0, MergedLexiconEntryConfig.TERM_LENGTH);

            //Convert the bytes to a string and trim it
            term = new String(termBytes, Charset.defaultCharset()).trim();

            //Instantiate the TermInfo object reading the next 3 integers from the file
            lexiconEntry = new MergedLexiconEntry(term,   //Term
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


}
