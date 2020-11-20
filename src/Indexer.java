import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.FileSystems;

import org.apache.lucene.index.IndexWriter.DocStats;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.search.similarities.LMJelinekMercerSimilarity;
import org.apache.lucene.store.*;
import org.apache.lucene.index.*;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.TextField;
import org.apache.lucene.document.Field;


public class Indexer {
    public static void main(String[] args) throws Exception{
        String indexDir = new String("C:\\Users\\admin\\Documents\\IR\\LM-S0.2_index");
        String dataDir = new String("C:\\Users\\admin\\Documents\\IR\\Pages");
        // System.out.println(indexDir + dataDir);
        // System.out.println("Hello World!");
        long start = System.currentTimeMillis();
        Indexer indexer = new Indexer(indexDir);
        int numIndexed;
        try {
            numIndexed = indexer.index(dataDir, new TextFilesFilter());
        } finally {
        indexer.close();
        }
        long end = System.currentTimeMillis();
        System.out.println("Indexing " + numIndexed + " files took "
            + (end - start) + " milliseconds");

    }
    private IndexWriter writer;

    public Indexer(String indexDir) throws IOException {
        Directory dir = FSDirectory.open(FileSystems.getDefault().getPath(indexDir));
        StandardAnalyzer analyzer = new StandardAnalyzer();
        IndexWriterConfig conf = new IndexWriterConfig(analyzer);
        // You can choose the type of similarity classic similarity is TF-IDF
        // conf.setSimilarity(new ClassicSimilarity());
        float alpha = 0.2F;
        conf.setSimilarity(new LMJelinekMercerSimilarity(alpha));
        System.out.println(conf.getSimilarity());
        writer = new IndexWriter(dir, conf);
    }
    public void close() throws IOException {
        writer.close();
    }
    public int index(String dataDir, FileFilter filter) throws Exception {
        File[] files = new File(dataDir).listFiles();
            for (File f: files) {
                if (!f.isDirectory() && !f.isHidden() && f.exists() &&f.canRead() && 
                (filter == null || filter.accept(f))) {
                    indexFile(f);
                }
            }
        DocStats p = writer.getDocStats();
        return p.numDocs;
    }
    private static class TextFilesFilter implements FileFilter {
        public boolean accept(File path) {
            return path.getName().toLowerCase()
            .endsWith(".txt");
        }
    }
    protected Document getDocument(File f) throws Exception {
        Document doc = new Document();
        doc.add(new TextField("contents", new FileReader(f)));
        doc.add(new TextField("filename", f.getName(),
            Field.Store.YES));
        doc.add(new TextField("fullpath", f.getCanonicalPath(),
            Field.Store.YES));
        return doc;
    }
    private void indexFile(File f) throws Exception {
        // System.out.println("Indexing " + f.getCanonicalPath());
        Document doc = getDocument(f);
        writer.addDocument(doc);
    }
            
}
