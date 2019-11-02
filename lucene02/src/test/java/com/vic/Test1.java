package com.vic;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
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
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.Test;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;

/**
 * 1. 测试创建、检索索引
 * https://blog.csdn.net/eson_15/article/details/51792910
 * @author 罗利华
 * date: 2019/11/2 15:36
 */
public class Test1 {

    // 索引存放目录
    String indexDir = "d:/lucene/02";

    // 待索引的数据目录
    String dataDir = "d:/lucene/02/data";

    /**
     * 2. 检索索引库
     * @throws IOException
     */
    @Test
    public void searchIndexDB() throws Exception {

        // 要查询字符串
        String queryStr = "retract";

        // 打开索引目录
        IndexReader indexReader = DirectoryReader.open(FSDirectory.open(Paths.get(indexDir)));
        IndexSearcher searcher = new IndexSearcher(indexReader);

        // 标准分词
        Analyzer analyzer = new StandardAnalyzer();
        QueryParser queryParser = new QueryParser("content", analyzer);
        Query query = queryParser.parse(queryStr);

        // 查询前10条数据，将结果保存在topDocs中
        long startTime = System.currentTimeMillis();
        TopDocs topDocs = searcher.search(query, 10);

        System.out.println("匹配共耗时:" + (System.currentTimeMillis() - startTime));
        System.out.println("匹配到:" + topDocs.totalHits + "条记录");

        for(ScoreDoc scoreDoc : topDocs.scoreDocs) {
            Document doc = searcher.doc(scoreDoc.doc);
            String fullPath = doc.get("fullPath");
            System.out.println("fullPath: " + fullPath);
        }

        indexReader.close();
    }

    /**
     * 1. 创建索引库
     * @throws IOException
     */
    @Test
    public void createIndexDB() throws IOException {
        Directory directory = FSDirectory.open(Paths.get(indexDir));

        Analyzer analyzer = new StandardAnalyzer();
        IndexWriterConfig conf = new IndexWriterConfig(analyzer);

        IndexWriter writer = new IndexWriter(directory, conf);

        long startTime = System.currentTimeMillis();

        File[] files = new File(dataDir).listFiles();
        for(File file : files) {
            Document doc = new Document();
            doc.add(new TextField("content", new FileReader(file)));
            // 文件名称，并存入索引文件中
            doc.add(new TextField("fileName", file.getName(), Field.Store.YES));
            // 文件路径，并存入索引文件中
            doc.add(new TextField("fullPath", file.getCanonicalPath(), Field.Store.YES));
            writer.addDocument(doc);
            System.out.println("添加文件 " + file.getName() + "到索引库");
        }

        System.out.println("建立索引耗时:" + (System.currentTimeMillis() - startTime) + "ms");
        System.out.println("共索引了" + writer.numDocs() + "文件");

        writer.close();
    }

}
