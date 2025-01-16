# MIRCV PROJECT

A project for the Multimedia Information Retrieval and Computer Vision course at the University of Pisa (A.Y. 2022/2023), carried out by Elisa De Filomeno, Massimo Merla, and Riccardo Orrù.

## PROJECT
The project consists of 3 packages:
- components
- evaluation
- query execution


## COMPONENTS
Within **components**, we manage the construction of the necessary data structures.  
**Maincomponents** is the main class that creates the inverted index, the lexicon, and document index.  
The **Config** class contains final variables that allow us to enable stemming andstopword removal, compression, and debug mode. 
It also contains the path to the collection file and stopword file. To start with the creation of the data structures, these files must be added to the **data** directory.

## QUERY EXECUTION
**DemoInterface**
This module implement the scoring algorithms.

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
