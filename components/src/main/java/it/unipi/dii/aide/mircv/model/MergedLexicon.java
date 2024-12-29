package it.unipi.dii.aide.mircv.model;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;
import java.util.HashMap;
import it.unipi.dii.aide.mircv.utils.Config;
import it.unipi.dii.aide.mircv.utils.FileReaderUtility;
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
                lexiconEntry = FileReaderUtility.readNextTerm(lexiconFile, offset);

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


}
