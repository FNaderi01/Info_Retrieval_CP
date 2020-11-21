import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Paths;
import java.util.ArrayList;

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
        String indexDir = "C:\\Users\\admin\\Documents\\IR\\LM-S0.7_index";
        String q = "New training methods for coronavirus emergencies";
        String method = "LM-S0.7";
        ArrayList<String> queries = readQueries("queries.txt");

        for (int i=0; i<queries.size(); i++){
            String name = "Q" + (i+1) +"-"+method+".txt"; 
            q = queries.get(i);
            search(indexDir, q, name);
        }
    }

    public static ArrayList<String> readQueries(String querisFileName) throws IOException{
        ArrayList<String> result = new ArrayList<String>();
        File qs = new File(querisFileName);
        BufferedReader br = new BufferedReader(new FileReader(qs));
        String line;
        while ((line = br.readLine())!=null){
            result.add(line);
        }
        br.close();
        return result;
    }
    public static void search(String indexDir, String q, String fileName) throws IOException, ParseException {

        Directory directory = FSDirectory.open(FileSystems.getDefault().getPath(indexDir));
        IndexReader indexReader = DirectoryReader.open(directory); 
        IndexSearcher indexSearcher = new IndexSearcher(indexReader); 

        float alpha = 0.7F;
        indexSearcher.setSimilarity(new LMJelinekMercerSimilarity(alpha)); 
        // indexSearcher.setSimilarity(new ClassicSimilarity());

        QueryParser parser = new QueryParser("contents", new StandardAnalyzer());
        Query query = parser.parse(q);

        long start = System.currentTimeMillis();
        TopDocs hits = indexSearcher.search(query, 10);
        long end = System.currentTimeMillis();
        System.err.println("Found " + hits.totalHits + " document(s) (in " + (end - start) +
        " milliseconds) that matched query '" + q + "':");
        String firstLine = "Found " + hits.totalHits + " document(s) (in " + (end - start) +
        " milliseconds) that matched query '" + q + "':"+"\n";

        FileWriter myWriter = new FileWriter(fileName);
        myWriter.write(firstLine);
        int i =0;
        for(ScoreDoc scoreDoc : hits.scoreDocs) {
            Document doc = indexSearcher.doc(scoreDoc.doc);
            String temp = Integer.toString(i+1)+ "- "+doc.get("filename")+"\n";
            myWriter.write(temp);
            System.out.println(doc.get("fullpath"));
            i++;
        }
        myWriter.close();

    }
}
