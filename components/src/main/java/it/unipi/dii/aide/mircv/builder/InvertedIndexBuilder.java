package it.unipi.dii.aide.mircv.builder;

import it.unipi.dii.aide.mircv.model.InvertedIndex;
import it.unipi.dii.aide.mircv.model.Lexicon;
import it.unipi.dii.aide.mircv.model.LexiconEntry;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import static it.unipi.dii.aide.mircv.model.LexiconEntry.*;

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
            lexicon.getLexicon().forEach((key, lexiconEntry) -> lexiconEntry.writeToFile(randomAccessFile, key, lexiconEntry));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void writeInvertedIndexToFile(String outputPathDocIds, String outputPathFrequencies) throws FileNotFoundException {

        try (RandomAccessFile docIdBlock = new RandomAccessFile(outputPathDocIds, "rw");
             RandomAccessFile frequencyBlock = new RandomAccessFile(outputPathFrequencies, "rw")) {
            AtomicInteger currentOffsetDocId = new AtomicInteger(0);
            AtomicInteger currentOffsetFrequency = new AtomicInteger(0);

            invertedIndex.getInvertedIndex().forEach((key, postingList) -> {

                //Set the current offsets to be written in the lexicon
                int offsetDocId = currentOffsetDocId.get();
                int offsetFrequency = currentOffsetFrequency.get();

                postingList.getPostingList().forEach((docId, freq) -> {
                    //Create the buffers for each element to be written
                    byte[] postingDocId = ByteBuffer.allocate(8).putLong(Long.parseLong(docId)).array();
                    byte[] postingFreq = ByteBuffer.allocate(4).putInt(freq).array();

                    try {
                        //Append each element to the file, each one adds 4 bytes to the file
                        docIdBlock.write(postingDocId);
                        frequencyBlock.write(postingFreq);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    //Increment the current offset
                    currentOffsetDocId.addAndGet(8);
                    currentOffsetFrequency.addAndGet(4);
                });

                //Set the docId offset, the frequency offset, the posting list length of the term in the lexicon
                lexicon.getLexicon().get(key).getDocumentFrequency();
                lexicon.getLexicon().get(key).set(offsetDocId, offsetFrequency, postingList.getPostingList().size());
            });


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
        public void addTerm(String term) {
            if (lexicon.getLexicon().containsKey(term)) {
                LexiconEntry entry = lexicon.getLexicon().get(term);
                int tf = entry.getTermFrequency();
                entry.setTermFrequency(tf + 1);
            } else {
                lexicon.getLexicon().put(term, new LexiconEntry(1, 0));
            }
        }

        public Set<String> getLexiconTerms() {
            return lexicon.getLexicon().keySet();
        }

        public void setDocumentFrequency(InvertedIndex invertedIndex, String term) {
            LexiconEntry entry = lexicon.getLexicon().get(term);
            entry.setDocumentFrequency(invertedIndex.getPostingListLength(term));
        }

        public void setLexicon(Lexicon lexicon) {
            this.lexicon = lexicon;
        }

        public void writeToFile(RandomAccessFile lexiconFile, String outputPath, String key, LexiconEntry lexiconEntry, Lexicon lexicon) {

            //Fill with whitespaces to keep the length standard
            String tmp = String.format("%" + TERM_LENGTH + "." + TERM_LENGTH + "s", outputPath);

            byte[] term = ByteBuffer.allocate(TERM_LENGTH).put(tmp.getBytes()).array();
            byte[] offsetDocId = ByteBuffer.allocate(OFFSET_DOCIDS_LENGTH).putLong(lexicon.getLexicon().get(key).getOffsetDocId()).array();
            byte[] offsetFrequency = ByteBuffer.allocate(OFFSET_FREQUENCIES_LENGTH).putLong(lexicon.getLexicon().get(key).getOffsetFrequency()).array();
            byte[] postingListLength = ByteBuffer.allocate(POSTING_LIST_LENGTH).putInt(lexicon.getLexicon().get(key).getPostingListLength()).array();

            try {
                lexiconFile.write(term);
                lexiconFile.write(offsetDocId);
                lexiconFile.write(offsetFrequency);
                lexiconFile.write(postingListLength);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public String toString() {
            StringBuilder result = new StringBuilder();

            for (Map.Entry<String, LexiconEntry> entry : lexicon.getLexicon().entrySet()) {
                String term = entry.getKey();
                LexiconEntry lexiconEntry = entry.getValue();

                result.append(term).append("    ");
                result.append(lexiconEntry.getTermFrequency()).append(", ");
                result.append(lexiconEntry.getDocumentFrequency()).append(", ");
                result.append(lexiconEntry.getInverseDocumentFrequency()).append("\n");
            }

            return result.toString();
        }
}

