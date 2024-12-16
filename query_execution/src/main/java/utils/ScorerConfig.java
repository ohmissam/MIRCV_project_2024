package utils;

import it.unipi.dii.aide.mircv.model.Statistics;


public class ScorerConfig {
    public static final double K1 = 1.5;
    public static final double B = 0.75;
    public static final Statistics STATISTICS = Statistics.readStatistics();
    public static final int BEST_K_VALUE = 20; //Length of the final ranking
    public static final boolean USE_BM25 = true;
    public static final boolean IS_DEBUG_MODE = true;
}
