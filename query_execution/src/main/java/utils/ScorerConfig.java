package utils;

import it.unipi.dii.aide.mircv.model.Statistics;


public class ScorerConfig {
    public static final double K1 = 1.5;
    public static final double B = 0.75;
    public static final Statistics STATISTICS = Statistics.readStatistics();
    public static final int BEST_K_VALUE = 20; //Length of the final ranking
    public static boolean USE_BM25 = false; //else tfidf
    public static boolean IS_DEBUG_MODE = true;
    public static boolean USE_CONJUNCTIVE_SCORER = true; //else disjunctive scorer


    public static void setUseBm25(boolean useBm25) {
        USE_BM25 = useBm25;
    }

    public static void setDebugMode(boolean debugMode) {
        IS_DEBUG_MODE = debugMode;
    }

    public static void setUseConjunctiveScorer(boolean useConjunctiveScorer) {
        USE_CONJUNCTIVE_SCORER = useConjunctiveScorer;
    }

}
