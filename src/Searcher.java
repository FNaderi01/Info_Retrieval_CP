import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Paths;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.search.similarities.LMJelinekMercerSimilarity;
import org.apache.lucene.store.*;

public class Searcher {
    public static void main(String[] args) throws IllegalArgumentException,IOException, ParseException {    
        String indexDir = "";
        String q = "";

        search(indexDir, q);
    }

    public static void search(String indexDir, String q) throws IOException, ParseException {

        Directory directory = FSDirectory.open(FileSystems.getDefault().getPath(indexDir));
        IndexReader indexReader = DirectoryReader.open(directory); 
        IndexSearcher indexSearcher = new IndexSearcher(indexReader); 

        // float alpha = 0.7F;
        // indexSearcher.setSimilarity(new LMJelinekMercerSimilarity(alpha)); 
        // indexSearcher.setSimilarity(new ClassicSimilarity());

        QueryParser parser = new QueryParser("contents", new StandardAnalyzer());
        Query query = parser.parse(q);

        long start = System.currentTimeMillis();
        TopDocs hits = indexSearcher.search(query, 10);
        long end = System.currentTimeMillis();
        System.err.println("Found " + hits.totalHits + " document(s) (in " + (end - start) +
        " milliseconds) that matched query '" + q + "':");
        for(ScoreDoc scoreDoc : hits.scoreDocs) {
            Document doc = indexSearcher.doc(scoreDoc.doc);
            System.out.println(doc.get("fullpath"));
        }
    }
}
