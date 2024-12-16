package it.unipi.dii.aide.mircv.model;
import it.unipi.dii.aide.mircv.utils.Config;
import it.unipi.dii.aide.mircv.utils.DocumentIndexEntryConfig;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

public class DocumentIndex {
    private static Map<Long, DocumentIndexEntry> documentIndex;

    public DocumentIndex() {
        documentIndex = new HashMap<>();
    }

    public static Map<Long, DocumentIndexEntry> getDocumentIndex() {
        return documentIndex;
    }

    public void addDoc(long docId, DocumentIndexEntry documentIndexEntry) {
        documentIndex.put(docId, documentIndexEntry);
    }

    /**
     * Retrieves a document from the document index.
     *
     * @param docId The ID of the document to retrieve.
     * @return The corresponding entry, or null if it does not exist.
     */
    public DocumentIndexEntry getDoc(long docId) {
        return documentIndex.get(docId);
    }

    /**
     * Load the document index in memory.
     */
    public void loadDocumentIndex() {
        System.out.println("[DOCUMENT INDEX LOADER] Document index loading");
        try (//Object to open the stream from the document index file
             RandomAccessFile documentIndexFile = new RandomAccessFile(Config.DOCINDEX_FILE_PATH, "r")) {
            //Start the stream from the document index file

            //Accumulator for the current offset in the file
            int offset = 0;

            //Array of bytes in which put the docno
            byte[] docnoBytes = new byte[DocumentIndexEntryConfig.DOCNO_LENGTH];

            long docid;

            int docLength;

            String docno;

            //System.out.println(documentIndexFile.length());
            //While we're not at the end of the file
            while (offset < documentIndexFile.length()) {

                //Read the docid from the first 8 bytes starting from the offset
                docid = documentIndexFile.readLong();

                //Read the first DOCUMENT_INDEX_DOCNO_LENGTH bytes containing the docno
                documentIndexFile.readFully(docnoBytes, 0, DocumentIndexEntryConfig.DOCNO_LENGTH);

                //Convert the bytes to a string and trim it
                docno = new String(docnoBytes, Charset.defaultCharset()).trim();

                //Read the length of the document, 4 bytes starting from the offset
                docLength = documentIndexFile.readInt();

                //Insert the termInfo into the HashMap
                documentIndex.put(docid, new DocumentIndexEntry(docno, docLength));

                //Increment the offset
                offset += DocumentIndexEntryConfig.DOCUMENT_INDEX_ENTRY_LENGTH;
            }

            System.out.println("[DOCUMENT INDEX LOADER] Document index loaded");

        } catch (Exception e) {
            System.out.println("[DOCUMENT INDEX LOADER] Error loading the document index: " + e.getMessage());
            e.printStackTrace();
        }


    }
}
