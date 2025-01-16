# MIRCV PROJECT

A project for the Multimedia Information Retrieval and Computer Vision course at the University of Pisa (A.Y. 2022/2023), carried out by Elisa De Filomeno, Massimo Merla, and Riccardo Orrù.
This project aims to develop a search engine capable of performing text retrieval tasks on a large-scale dataset containing 8.8 million documents. 
The dataset is part of the [MS MARCO TREC Deep Learning 2020](https://microsoft.github.io/msmarco/TREC-Deep-Learning-2020) collection. 
It is used to construct an inverted index data structure, enabling efficient and accurate document retrieval based on user queries.

## PROJECT
The project consists of 3 packages:
- components
- evaluation
- query execution


## COMPONENTS
Within **components**, we manage the construction of the necessary data structures.  
**Maincomponents** is the main class that creates the inverted index, the lexicon and document index.  
The **Config** class contains final variables that allow us to enable stemming and stopword removal, compression, and debug mode. 
It also specifies the paths to the collection file and the stopword file. To begin creating the data structures, these files must be placed in the **data** directory.
After the indexing, the files created wil be saved in the same directory.

## QUERY EXECUTION
The **DemoInterface** class provides a user-friendly interface for submitting search queries and retrieving a ranked list of the top 20 relevant documents. 
By typing '!settings' in the command line, users can adjust the scoring function, query type, and debug mode.

## EVALUATION
In the **evaluation** package, we have the **EvaluationConfig** class, where we can configure the paths for the tests and results. 
The class **Evaluation** performs tests on 200 queries and writes the results in a format suitable for the trec_eval tool.
It processes queries using the BM25 and TF-IDF scoring algorithms in both conjunctive and disjunctive modes.




Example of UI:

--- Main Menu ---
Type your query or use the following commands:
- Type '!settings' to configure settings
- Type '!exit' to quit
Enter your query or command: !settings

--- Settings Menu ---
1. Toggle USE_BM25 (Current: false)
2. Toggle USE_CONJUNCTIVE_SCORER (Current: true)
3. Toggle IS_DEBUG_MODE (Current: true)
4. Back to Main Menu
