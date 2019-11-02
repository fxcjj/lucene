package com.vic;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Paths;

/**
 * 文档域加权测试
 * https://blog.csdn.net/eson_15/article/details/51798774
 * @author 罗利华
 * date: 2019/11/2 17:38
 */
public class Test31 {

    // 索引目录
    String path = "D:\\lucene\\03\\01";

    // 索引目录
    Directory dir;

    private String ids[] = {"1","2","3","4"};
    private String authors[] = {"Jack","Marry","John","Json"};
    private String positions[] = {"accounting","technician","salesperson","boss"};
    private String titles[] = {"Java is a good language.","Java is a cross platform language","Java powerful","You should learn java"};
    private String contents[] = {
            "If possible, use the same JRE major version at both index and search time.",
            "When upgrading to a different JRE major version, consider re-indexing. ",
            "Different JRE major versions may implement different versions of Unicode.",
            "For example: with Java 1.4, `LetterTokenizer` will split around the character U+02C6."
    };

    @Test
    public void testSearch() throws IOException {
        IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(path)));
        IndexSearcher search = new IndexSearcher(reader);
        String searchField = "title"; //要查询的Field
        String q = "java"; //要查询的字符串

        // 构造关键字
        Term term = new Term(searchField, q);
        // 关键字查询
        Query query = new TermQuery(term);

        TopDocs hits = search.search(query, 10);
        System.out.println("匹配" + q + "总共查询到" + hits.totalHits + "个文档");
        for(ScoreDoc score : hits.scoreDocs) {
            Document doc = search.doc(score.doc);
            System.out.println(doc.get("author")); // 打印一下查出来记录对应的作者
        }
        reader.close();
    }

    @Test
    public void testCreateIndex() throws IOException {

        dir = FSDirectory.open(Paths.get(path));

        Analyzer analyzer = new StandardAnalyzer();
        IndexWriterConfig conf = new IndexWriterConfig(analyzer);
        IndexWriter indexWriter = new IndexWriter(dir, conf);

        for(int i = 0; i < ids.length; i++) {
            Document doc = new Document();
            doc.add(new StringField("id", ids[i], Field.Store.YES));
            doc.add(new StringField("author", authors[i], Field.Store.YES));
            doc.add(new StringField("position", positions[i], Field.Store.YES));

            // 这部分就是加权操作了，对title这个Field进行加权，因为等会要查这个Field
            TextField field = new TextField("title", titles[i], Field.Store.YES);
            // 先判断之个人对应的职位是不是boss，如果是就加权
            if("boss".equals(positions[i])) {
                field.setBoost(1.5f); // 加权操作，默认为1，1.5表示加权了，小于1就降权了
            }

            doc.add(field);
            doc.add(new TextField("content", contents[i], Field.Store.NO));
            indexWriter.addDocument(doc); //添加文档
        }

        indexWriter.close();
    }

}
