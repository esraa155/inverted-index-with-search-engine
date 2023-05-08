import java.io.*;
import java.util.*;


// build an inverted index data structure
public class InvertedIndex {
    HashMap<String, DictEntry> index;

    public InvertedIndex() {
        index = new HashMap<>();
    }

//builds an inverted index
    public void buildIndex(String[] filenames) throws IOException {
        for (String filename : filenames) {
            int docId = Integer.parseInt(filename.substring(0, filename.lastIndexOf(".")));
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            String line = null;
            while ((line = reader.readLine()) != null) {
                String[] terms = line.split(" ");
                for (String term : terms) {
                    term = term.toLowerCase().replaceAll("[^a-z0-9 ]", "");
                    if (term.length() == 0)
                        continue;
                    if (!index.containsKey(term))
                        index.put(term, new DictEntry());
                    DictEntry entry = index.get(term);
                    entry.term_freq++;
                    if (entry.pList == null || entry.pList.docId != docId) {
                        entry.doc_freq++;
                        Posting posting = new Posting();
                        posting.docId = docId;
                        entry.pList = addPostingToList(entry.pList, posting);
                    } else {
                        entry.pList.term_freq++;
                    }
                }
            }
            reader.close();
        }
    }
//adds a new posting to the posting list of a given term in the inverted index.
    private Posting addPostingToList(Posting head, Posting posting) {
        if (head == null)
            return posting;
        if (posting.docId < head.docId) {
            posting.next = head;
            return posting;
        }
        head.next = addPostingToList(head.next, posting);
        return head;
    }
//takes a query string as input and searches for the query in the inverted index
public SearchResult search(String query) {
    query = query.toLowerCase().replaceAll("[^a-z0-9 ]", "");
    if (!index.containsKey(query))
        return null;
    Posting pList = index.get(query).pList;
    SearchResult result = new SearchResult();
    result.term_freq = index.get(query).term_freq;
    Set<Integer> docIds = new HashSet<>();
    while (pList != null) {
        docIds.add(pList.docId);
        pList = pList.next;
    }
    result.doc_freq = docIds.size(); // set doc_freq to the number of distinct document IDs
    result.docIds.addAll(docIds);
    return result;
}

    /*term_freq: an integer representing the frequency of the term in the document.
         docId: an integer representing the ID of the document.
           dtf: an integer representing the document term frequency, which is the number of times the term appears in the document.
           The next variable is a reference to the next Posting object in the linked list. */
    
    public class Posting {
        public int term_freq;
        int docId;
        int dtf = 1;
        Posting next = null;
    }
/*
 * doc_freq: an integer variable that represents the document frequency of the corresponding term.
term_freq: an integer variable that represents the term frequency of the corresponding term.
pList: a Posting object that represents the postings list for the corresponding term.
The doc_freq and term_freq variables are initialized to 0, and the pList variable is initialized to null. This class is used in the InvertedIndex class to store the information related to a term.
 */

    public class DictEntry {
        int doc_freq = 0;
        int term_freq = 0;
        Posting pList = null;
    }
    /*term_freq, doc_freq, and docIds. term_freq and doc_freq are both of type int and represent the term frequency and document frequency of a particular search term. docIds is a List of Integer objects that stores the IDs of the documents in which the search term appears. The docIds list is initialized as an empty ArrayList using the no-argument constructor of the ArrayList class. */
    public class SearchResult {
        int term_freq;
        int doc_freq;
        List<Integer> docIds = new ArrayList<>();
    }



    public static void main(String[] args) throws IOException {
        String[] filenames = {"0.txt","1.txt","2.txt","3.txt"};
        InvertedIndex index = new InvertedIndex();
        index.buildIndex(filenames);
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter a query: ");
        String query = scanner.nextLine();
        SearchResult result = index.search(query);
        if (result != null) {
            System.out.println("Term frequency: " + result.term_freq);
            System.out.println("Document frequency: " + result.doc_freq);
            System.out.println("Document IDs: " + result.docIds);
        } else {
            System.out.println("Not found.");
        }

    }

    
}