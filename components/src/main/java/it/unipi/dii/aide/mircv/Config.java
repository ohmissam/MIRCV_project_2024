package it.unipi.dii.aide.mircv;

import java.util.Arrays;

public class Config {

    //String for the path of stopwords file
    public final static String STOPWORDS_PATH = "data\\stopwords.txt";

    //String for the document collection path
    public final static String COLLECTION_PATH = "data\\collection.tsv";

    //String for the compressed document collection path
    public final static String COMPRESSED_COLLECTION_PATH = "data\\collection_small.tar.gz";

    //String for the path of
    public static final String LEXICON_FILE_PATH = "data\\Lexicon.txt";

    //String for the path of
    public static final String DOCINDEX_FILE_PATH = "data\\DocumentIndex.txt";

    //String for the path of
    public static final String INVINDEX_FILE_PATH = "data\\InvertedIndex.txt";


    public static final boolean ENABLE_STEMMING_AND_STOPWORD_REMOVAL = false;

    public static final double PERCENTAGE = 0.7;
}
