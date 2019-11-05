package com.vic;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Paths;

/**
 * lucene搜索功能
 * https://blog.csdn.net/eson_15/article/details/51802974
 */
public class Test4 {

    // 索引目录
    Directory dir;

    // 索引读
    IndexReader reader;

    // 索引查询器
    IndexSearcher searcher;

    /**
     *
     * @throws IOException
     */
    @Before
    public void setUp() throws IOException {
        reader = DirectoryReader.open(FSDirectory.open(Paths.get("d:/lucene/02")));
        searcher = new IndexSearcher(reader);
    }


    @After
    public void tearDown() throws IOException {
        reader.close();
    }

    /**
     * 5. 多条件查询（即组合查询）
     */
    @Test
    public void testBooleanQuery() throws Exception {
        // 数字范围查询
        NumericRangeQuery<Integer> numericRangeQuery = NumericRangeQuery.newIntRange("id", 1, 2, true, true);
        // 前缀查询
        PrefixQuery prefixQuery = new PrefixQuery(new Term("city", "sh"));

        // 组合在一起
        BooleanQuery.Builder builder = new BooleanQuery.Builder();
        builder.add(numericRangeQuery, BooleanClause.Occur.MUST);
        builder.add(prefixQuery, BooleanClause.Occur.MUST);

        TopDocs hits = searcher.search(builder.build(), 10);

        System.out.println("匹配到" + hits.totalHits + "条文档");

        for (ScoreDoc score : hits.scoreDocs) {
            Document doc = searcher.doc(score.doc);
            System.out.println(doc.get("id") + ", " + doc.get("city") + ", " + doc.get("desc"));
        }
    }


    /**
     * 4. 指定字符串开头搜索
     */
    @Test
    public void testPrefixQuery() throws Exception {
        PrefixQuery prefixQuery = new PrefixQuery(new Term("city", "qi"));
        TopDocs hits = searcher.search(prefixQuery, 10);

        System.out.println("匹配到" + hits.totalHits + "条文档");

        for (ScoreDoc score : hits.scoreDocs) {
            Document doc = searcher.doc(score.doc);
            System.out.println(doc.get("id") + ", " + doc.get("city") + ", " + doc.get("desc"));
        }

    }

    /**
     * 3. 指定数字范围搜索
     */
    @Test
    public void testNumericRangeQuery() throws Exception {
        NumericRangeQuery<Integer> numericRangeQuery = NumericRangeQuery.newIntRange("id", 1, 5, true, true);
        TopDocs hits = searcher.search(numericRangeQuery, 10);

        System.out.println("匹配到" + hits.totalHits + "条文档");

        for (ScoreDoc score : hits.scoreDocs) {
            Document doc = searcher.doc(score.doc);
            System.out.println(doc.get("id") + ", " + doc.get("city") + ", " + doc.get("desc"));
        }
    }



    /**
     * 2. 使用查询表达式QueryParser搜索
     */
    @Test
    public void testQueryParser() throws Exception {
        String fld = "content";
//        String queryStr = "around OR Unicode"; //使用or或者and
        String queryStr = "aroun~"; //~表示匹配


        StandardAnalyzer standardAnalyzer = new StandardAnalyzer();
        QueryParser parser = new QueryParser(fld, standardAnalyzer);


        Query query = parser.parse(queryStr);

        TopDocs hits = searcher.search(query, 10);

        System.out.println("匹配[" + queryStr + "]" + "共查询到" + hits.totalHits + "条文档");

        ScoreDoc[] scoreDocs = hits.scoreDocs;
        for(ScoreDoc scoreDoc : scoreDocs) {
            Document doc = searcher.doc(scoreDoc.doc);
            System.out.println(doc.get("fullPath"));
        }
    }


    /**
     * 1. 对特定项搜索
     */
    @Test
    public void testTermQuery() throws IOException {
        String fld = "content";
        String queryStr = "aroun";
        Term term = new Term(fld, queryStr);
        Query query = new TermQuery(term);

        TopDocs hits = searcher.search(query, 10);

        System.out.println("匹配[" + queryStr + "]" + "共查询到" + hits.totalHits + "条文档");

        ScoreDoc[] scoreDocs = hits.scoreDocs;
        for(ScoreDoc scoreDoc : scoreDocs) {
            Document doc = searcher.doc(scoreDoc.doc);
            System.out.println(doc.get("fullPath"));
        }
    }

}
