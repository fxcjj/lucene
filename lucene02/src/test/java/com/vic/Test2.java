package com.vic;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.search.highlight.SimpleSpanFragmenter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.Test;

import java.io.StringReader;
import java.nio.file.Paths;

/**
 * 2. 中文分词和高亮显示
 * https://www.jianshu.com/p/48aad01ebc7c
 * https://blog.csdn.net/eson_15/article/details/51802981
 */
public class Test2 {

    // 存放索引目录
    String indexDir = "D:\\lucene";


    /**
     * 3. 检索并高亮显示
     * @throws Exception
     */
    @Test
    public void highlighter() throws Exception {

        String field = "content";
        String queryStr = "内容";

        // 读取索引
        IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(indexDir)));

        // 索引查询器
        IndexSearcher searcher = new IndexSearcher(reader);

        Analyzer analyzer = new SmartChineseAnalyzer();
        QueryParser parser = new QueryParser(field, analyzer);
        Query query = parser.parse(queryStr);

        long startTime = System.currentTimeMillis();
        TopDocs topDocs = searcher.search(query, 10);

        System.out.println("查找" + queryStr + "所用时间：" + (System.currentTimeMillis()-startTime));
        System.out.println("查询到" + topDocs.totalHits + "条记录");

        // 高亮显示
        SimpleHTMLFormatter simpleHTMLFormatter = new SimpleHTMLFormatter("<b><font color=red>", "</font></b>");
        // 计算得分，会初始化一个查询结果最高的得分
        QueryScorer scorer = new QueryScorer(query);
        // 根据得分算出一个片段
        SimpleSpanFragmenter fragmenter = new SimpleSpanFragmenter(scorer);

        Highlighter highlighter = new Highlighter(simpleHTMLFormatter, scorer);
        // 设置显示高亮的片段
        highlighter.setTextFragmenter(fragmenter);

        //遍历查询结果
        for(ScoreDoc scoreDoc : topDocs.scoreDocs){
            Document doc = searcher.doc(scoreDoc.doc);
            String content = doc.get("content");
            if(content != null){
                TokenStream tokenStream = analyzer.tokenStream(field, new StringReader(content));
                String summary = highlighter.getBestFragment(tokenStream, content);
                System.out.println(summary);
            }
        }
        reader.close();
    }

    /**
     * 2. 普通检索
     * @throws Exception
     */
    @Test
    public void search() throws Exception {

        String field = "content";
        String queryStr = "内容";

        // 读取索引
        IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(indexDir)));

        // 索引查询器
        IndexSearcher searcher = new IndexSearcher(reader);

        Analyzer analyzer = new SmartChineseAnalyzer();
        QueryParser parser = new QueryParser(field, analyzer);
        Query query = parser.parse(queryStr);

        long startTime = System.currentTimeMillis();
        TopDocs topDocs = searcher.search(query, 10);

        System.out.println("查找" + queryStr + "所用时间：" + (System.currentTimeMillis()-startTime));
        System.out.println("查询到" + topDocs.totalHits + "条记录");

        // 遍历结果
        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
            Document doc = searcher.doc(scoreDoc.doc);
            String content = doc.get("content");
            System.out.println("content: " + content);
        }

        reader.close();
    }

    /**
     * 1. 创建索引文件
     * @throws Exception
     */
    @Test
    public void createIndexDB() throws Exception {

        // 测试数据
        Integer[] ids = {1, 2, 3};
        String[] titles = {"标题1", "标题2", "标题3"};
        String[] contents = {
                "内容1内容啊哈哈哈",
                "内容2内容啊哈哈哈",
                "内容3内容啊哈哈哈"
        };

        // 开始时间
        long startTime = System.currentTimeMillis();

        Directory directory = FSDirectory.open(Paths.get(indexDir));

        Analyzer analyzer = new SmartChineseAnalyzer();
        IndexWriterConfig conf = new IndexWriterConfig(analyzer);

        IndexWriter indexWriter = new IndexWriter(directory, conf);

        for (int i = 0; i < ids.length; i++) {
            Document doc = new Document();
            // 添加字段，并把存到索引文件中
            doc.add(new IntField("id", ids[i].intValue(), Field.Store.YES));
            doc.add(new TextField("title", titles[i], Field.Store.YES));
            doc.add(new TextField("content", contents[i], Field.Store.YES));

            indexWriter.addDocument(doc);
        }

        indexWriter.commit();
        System.out.println("共索引了" + indexWriter.numDocs() + "个文件");
        indexWriter.close();
        System.out.println("创建索引所用时间：" + (System.currentTimeMillis()-startTime) + "毫秒");
    }

}
