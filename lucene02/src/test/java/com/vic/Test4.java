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
import org.apache.lucene.util.BytesRef;
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

    // 索引读器
    IndexReader reader;

    // 索引查询器
    IndexSearcher searcher;

    /**
     *
     * @throws IOException
     */
    @Before
    public void setUp() throws IOException {
        reader = DirectoryReader.open(FSDirectory.open(Paths.get("D:\\lucene\\lucene02")));
        searcher = new IndexSearcher(reader);
    }


    @After
    public void tearDown() throws IOException {
        reader.close();
    }

    /**
     * 6. 排序查询
     * 比较的是ASCII码值，中文没有太大意义
     */
    @Test
    public void testSort() throws Exception {
        // 排序
        Sort sort = new Sort();
        // 按人口排序
        /**
         * java.lang.IllegalStateException: unexpected docvalues type NONE for field 'population' (expected=NUMERIC). Use UninvertingReader or index with docvalues.
         * 原因：是lucene对排序的索引字段有要求，即要使用DocValuesField的字段才能进行排序
         * 解决：创建索引时，使用DocValuesField
         *
         *
         * 第一个参数：要排序的字段
         * 第二个参数：字段类型
         * 第三个参数：true降序 false升序
         */
        SortField sf = new SortField("population", SortField.Type.LONG, false);
        sort.setSort(sf);

        Query query = new MatchAllDocsQuery();
        TopDocs hits = searcher.search(query, 10, sort);

        System.out.println("匹配到" + hits.totalHits + "条文档");

        for (ScoreDoc score : hits.scoreDocs) {
            Document doc = searcher.doc(score.doc);
            System.out.println(doc.get("id") + ", " + doc.get("city") + ", " + doc.get("desc") + ", " + doc.get("population"));
        }
    }
    /**
     * 6. 字符串范围查询
     * 比较的是ASCII码值，中文没有太大意义
     */
    @Test
    public void testStringRange() throws Exception {

        Query query = new TermRangeQuery("id", new BytesRef("1"), new BytesRef("9"), true, true);
        TopDocs hits = searcher.search(query, 10);

        System.out.println("匹配到" + hits.totalHits + "条文档");

        for (ScoreDoc score : hits.scoreDocs) {
            Document doc = searcher.doc(score.doc);
            System.out.println(doc.get("id") + ", " + doc.get("city") + ", " + doc.get("desc") + ", " + doc.get("population"));
        }

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


        /**
         1．MUST和MUST：取得两个查询子句的交集。  and
         2．MUST和MUST_NOT：表示查询结果中不能包含MUST_NOT所对应得查询子句的检索结果。
         3．SHOULD与MUST_NOT：连用时，功能同MUST和MUST_NOT。
         4．SHOULD与MUST连用时，结果为MUST子句的检索结果,但是SHOULD可影响排序。
         5．SHOULD与SHOULD：表示“或”关系，最终检索结果为所有检索子句的并集。
         6．MUST_NOT和MUST_NOT：无意义，检索无结果。
         */
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
