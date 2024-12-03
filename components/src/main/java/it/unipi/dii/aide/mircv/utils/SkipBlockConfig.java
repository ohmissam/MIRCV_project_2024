package it.unipi.dii.aide.mircv.utils;

public class SkipBlockConfig {
    static final int OFFSET_LENGTH = 8;

    static final int SKIP_BLOCK_DIMENSION_LENGTH = 4;

    static final int MAX_DOC_ID_LENGTH = 8;

    //Length in byte of each skip block (32)
    public static final int SKIP_BLOCK_LENGTH = 2*OFFSET_LENGTH + 2*SKIP_BLOCK_DIMENSION_LENGTH + MAX_DOC_ID_LENGTH;
}
