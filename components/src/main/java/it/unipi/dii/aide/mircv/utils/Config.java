package it.unipi.dii.aide.mircv.utils;

import java.util.Arrays;

public class Config {

    //String for the path of stopwords file
    public final static String STOPWORDS_PATH = "data\\stopwords.txt";

    //String for the document collection path
    public final static String COLLECTION_PATH = "data\\collection.tsv";

    //String for the compressed small document collection path
    public final static String SAMPLED_COLLECTION_PATH = "data\\collection_150000sampled.tar.gz";

    //String for the compressed document collection path
    public final static String TAR_COLLECTION_PATH = "data\\collection.tar.gz";

    //String for the path of the lexicon file
    public static final String LEXICON_FILE_PATH = "data\\Lexicon.txt";

    //String for the path of the document index file
    public static final String DOCINDEX_FILE_PATH = "data\\DocumentIndex.txt";

    //String for the path of the inverted index docIds and frequencies files
    public static final String INVINDEX_DOC_IDS_FILE_PATH = "data\\InvertedIndexDocIds.txt";
    public static final String INVINDEX_FREQUENCIES_FILE_PATH = "data\\InvertedIndexFrequencies.txt";

    //String for the path of th Skip Blocks file
    public static final String SKIP_BLOCKS_FILE_PATH = "data\\SkipBlocks.txt";


    //String or thw path of the statistics file
    public static final String STATISTICS_PATH = "data\\statistics.txt";

    public static final String LEXICON_BLOCK_PATH = "components/src/main/resources/tmp/lexiconBlock";

    public static final String DOCIDS_BLOCK_PATH = "components/src/main/resources/tmp/invertedIndexDocIds";

    public static final String FREQUENCIES_BLOCK_PATH = "components/src/main/resources/tmp/invertedIndexFrequencies";

    //Flag to enable stemming and stopword removal
    public static final boolean ENABLE_STEMMING_AND_STOPWORD_REMOVAL = true;
    //Flag to enable compression
    public static final boolean ENABLE_COMPRESSION = true;
    //Flag to enable debug mode
    public static boolean IS_DEBUG_MODE = false;

    // Threshold of memory over which the index must be flushed to disk
    public static final double PERCENTAGE = 0.7;

    public static void setIsDebugMode(boolean isDebugMode) {
        IS_DEBUG_MODE = isDebugMode;
    }
}
