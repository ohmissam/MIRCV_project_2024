package it.unipi.dii.aide.mircv.builder;

import it.unipi.dii.aide.mircv.model.InvertedIndex;
import it.unipi.dii.aide.mircv.model.Lexicon;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

/*
* Represents a builder for the creation of documents
* */

public class InvertedIndexBuilder {

    Lexicon lexicon;
    InvertedIndex invertedIndex;

    public InvertedIndexBuilder(Lexicon lexicon, InvertedIndex invertedIndex) {
        this.lexicon = lexicon;
        this.invertedIndex = invertedIndex;
    }

    public void writeLexiconToFile(String outputPath) throws FileNotFoundException {
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(outputPath, "rw")) {
            lexicon.getDictionary().forEach((key, lexiconEntry) -> lexicon.writeToFile(randomAccessFile, outputPath, key, lexiconEntry, lexicon));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void writeInvertedIndexToFile(String outputPathDocIds, String outputPathFrequencies) throws FileNotFoundException {

       /* try (RandomAccessFile docIdBlock = new RandomAccessFile(outputPathDocIds, "rw");
             RandomAccessFile frequencyBlock = new RandomAccessFile(outputPathFrequencies, "rw")) {
            AtomicInteger currentOffsetDocId = new AtomicInteger(0);
            AtomicInteger currentOffsetFrequency = new AtomicInteger(0);

            invertedIndex.getInvertedIndex().forEach(

            );


        } catch (IOException e) {
            throw new RuntimeException(e);
        }*/
    }
}
