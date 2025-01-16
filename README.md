# MIRCV PROJECT

A project for the Multimedia Information Retrieval and Computer Vision course at the University of Pisa (A.Y. 2022/2023), carried out by Elisa De Filomeno, Massimo Merla, and Riccardo Orrù.

## PROJECT

The project consists of 3 packages:
- components
- evaluation
- query execution

## COMPONENTS
Within **components**, we find the construction of the necessary data structures.  
**Maincomponents** is the main class that creates the inverted index and the lexicon, and it also shows the percentage of memory used.  
In the **config** class, we find the final variables where we can set the enabling of stemming and stopwords, compression, and debug mode.


## EVALUATION
In the `evaluation` package, we have the `EvaluationConfig` class, where we can configure the paths for the tests and results. 
The other class, `Evaluation`, uses the parameters set on the `invertedIndex` to execute the BM25 and TFIDF scoring algorithms in both Conjunctive and Disjunctive modes.

## QUERY EXECUTION

Implementation of scoring algorithms.
