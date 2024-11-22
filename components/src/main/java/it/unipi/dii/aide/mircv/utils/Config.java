package it.unipi.dii.aide.mircv.utils;

import java.util.Arrays;

public class Config {

    //String for the path of stopwords file
    public final static String STOPWORDS_PATH = "data\\stopwords.txt";

    //String for the document collection path
    public final static String COLLECTION_PATH = "data\\collection.tsv";

    //String for the compressed small document collection path
    public final static String COMPRESSED_COLLECTION_PATH = "data\\collection_small.tar.gz";

    //String for the compressed document collection path
    public final static String TAR_COLLECTION_PATH = "data\\collection.tar.gz";

    //String for the path of the lexicon file
    public static final String LEXICON_FILE_PATH = "data\\Lexicon.txt";

    //String for the path of the document index file
    public static final String DOCINDEX_FILE_PATH = "data\\DocumentIndex.txt";

    //String for the path of the inverted index file
    public static final String INVINDEX_FILE_PATH = "data\\InvertedIndex.txt";

    //String or thw path of the statistics file
    public static final String STATISTICS_PATH = "data\\statistics.txt";

    public static final String LEXICON_BLOCK_PATH = "components/src/main/resources/tmp/lexiconBlock";

    public static final String DOCIDS_BLOCK_PATH = "components/src/main/resources/tmp/invertedIndexDocIds";

    public static final String FREQUENCIES_BLOCK_PATH = "components/src/main/resources/tmp/invertedIndexFrequencies";

    //Flag to enable stemming and stopword removal
    public static final boolean ENABLE_STEMMING_AND_STOPWORD_REMOVAL = true;

    // Threshold of memory over which the index must be flushed to disk
    public static final double PERCENTAGE = 0.7;
}
