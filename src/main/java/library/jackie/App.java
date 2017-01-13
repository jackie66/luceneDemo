package library.jackie;

import java.io.File; 
import java.io.IOException;
import java.nio.file.Paths;

import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
/**
 * Hello world!
 *
 */
public class App 
{
    @SuppressWarnings("resource")
	public static void main( String[] args )
    {
    	String[] content = {"中国", "印度", "日本", "俄国", "英国", "美丽的国家"};
    	File file = new File("index");
    	 try {
			Directory dir = FSDirectory.open(Paths.get(file.toURI()));
			SmartChineseAnalyzer analyzer = new SmartChineseAnalyzer();
			IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
			if(file.exists()){
				iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);
			}else{
				iwc.setOpenMode(OpenMode.CREATE);
			}
			IndexWriter writer = new IndexWriter(dir, iwc);
			// 清空所有
			writer.deleteAll();
			for(int i=0; i<6; i++){
				Document document = new Document();
				StringField idField = new StringField("id", "" + i, Store.YES);
				document.add(idField);
				TextField nameField = new TextField("name", content[i], Store.NO);
				document.add(nameField);
				writer.addDocument(document);
			}
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	// .......................
    	App.search(file);
    }

    public static void search(File file){
    	try {
			IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(file.toURI())));
			IndexSearcher searcher = new IndexSearcher(reader);
			QueryParser parser = new QueryParser("name",new SmartChineseAnalyzer());
			Query query = parser.parse("美丽");
			ScoreDoc[] hits = searcher.search(query, 10).scoreDocs;
			for( ScoreDoc hit : hits){
				Document hitDocument = searcher.doc(hit.doc);
				System.out.println(hitDocument.get("id"));
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
    	
    	
    }
    
}



















