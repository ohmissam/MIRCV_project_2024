package it.unipi.mircv.invertedIndex;

import java.io.*;
import java.util.*;
import opennlp.tools.stemmer.*;

public class preProcessing {

    private final static String STOPWORDS_PATH = "data\\stopwords.txt";
    private final static String COLLECTION_PATH = "data\\collection.tsv";
    private static final String LEXICON_FILE_PATH = "data\\Lexicon.txt";
    private static final String DOCINDEX_FILE_PATH = "data\\DocumentIndex.txt";
    private static final String INVINDEX_FILE_PATH = "data\\InvertedIndex.txt";

    public static void main(String[] args) throws IOException {

        ArrayList<String> stopWords;
        stopWords = retrieveStopwords(STOPWORDS_PATH);

        Lexicon lexicon = new Lexicon();
        DocumentIndex docIndex = new DocumentIndex();
        InvertedIndex invIndex = new InvertedIndex();
        PorterStemmer stemmer = new PorterStemmer();
        String line;
        String[] temp;
        String[] tokens;
        int numOfDocs = 0;
        int docLength = 0;
        String docId = "";

        try (BufferedReader br = new BufferedReader(new FileReader(COLLECTION_PATH))) {
            /*IMPORTANTE DALLA DOC
             your program should have a compile flag that allows you to enalbe/disble stemming & stopword removal.
             */
            while ((line = br.readLine()) != null) {
                temp = line.split("\t");

                //do things only if there is any content
                if (!temp[1].isEmpty()) {
                    numOfDocs++;        //keep total documents count

                    docId = temp[0];
                    line = textCleaning(temp[1]); //temp[1] is a string - textCleaning does toLowerCase()

                    docIndex.addDoc(docId); //add current doc in the documentIndex

                    tokens = line.split(" ");
                    for (String token : tokens) {
                        if (removeStopwords(token, stopWords))
                            continue;
                        token = stemWord(token, stemmer);
                        //keep the count of stemmed words in the document
                        docLength++;

                        /*
                        first time a term is encountered set tf = 1 and df = 0 (df = num di doc in cui appare il termine)
                        df is set after the while-loop
                        */
                        lexicon.addTerm(token);

                        //the check on IF the posting EXISTS is done within the method
                        invIndex.setPosting(token, docId);

                    }
                    //put the doc tot.length in the length of the DocEntry obj
                    docIndex.setDocumentLength(docId, docLength);
                    docLength = 0;
                }
            }
            //documentFrequency of a term = length of the list of postings of that term
            for(String term : lexicon.getLexiconTerms()) {
                lexicon.setDocumentFrequency(invIndex, term);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        //save data structure
        try{
            BufferedWriter writer = new BufferedWriter(new FileWriter(LEXICON_FILE_PATH));
            writer.write(lexicon.toString());
            writer.close();

            writer = new BufferedWriter(new FileWriter(DOCINDEX_FILE_PATH));
            writer.write(docIndex.toString());
            writer.close();

            writer = new BufferedWriter(new FileWriter(INVINDEX_FILE_PATH));
            writer.write(invIndex.toString());
            writer.close();


        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static String stemWord(String token, PorterStemmer stemmer){
        return stemmer.stem(token);
    }

    public static boolean removeStopwords(String token, ArrayList<String> stopwords){
        for (String word: stopwords) {
            if (token.equals(word))
                return true;
        }
        return false;
    }

    public static ArrayList<String> retrieveStopwords(String stopwordsPath) throws IOException {

        BufferedReader bfr = new BufferedReader(new FileReader(stopwordsPath));
        ArrayList<String> stopwordsList = new ArrayList<>();
        String word;
        while ((word = bfr.readLine()) != null) {
            stopwordsList.add(word);
        }
        bfr.close();
        return stopwordsList;
    }

    public static String textCleaning(String text){
        //remove URLs - testato
        text = text.replaceAll("(https?):\\/\\/(w+)?\\.[a-zA-Z0-9!\"£$%&\\/()=?^'*@:\\._\\-\\+~#]+", " ");

        //remove tags like <£asd 3P > - testato, ma non prende tag che contiene un altro tag
        text = text.replaceAll("<[a-zA-Z0-9\\s+!\"£$%&\\/()=?^'*@:\\._\\-\\+~#]+>", "");

        //remove non-letter and non-numeric characters - testato elimina tutto tranne che lettere e numeri
        text = text.replaceAll("[^\\w\\s]+"," ");

        //replace white multispaces with just one -testato
        text = text.replaceAll("[\\s]{2,}", " ");

        return text.trim().toLowerCase();
    }
}
