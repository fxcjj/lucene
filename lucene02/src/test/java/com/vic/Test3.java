package com.vic;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Paths;

/**
 * 3. 构建索引
 * @author 罗利华
 * https://blog.csdn.net/eson_15/article/details/51798774
 * date: 2019/11/2 16:31
 */
public class Test3 {

    //存放索引的位置
    private Directory dir;

    // 测试数据
    private String ids[] = {"1", "2", "3"}; //用来标识文档
//    private Integer ids[] = {1, 2, 3}; //用来标识文档
    private String citys[] = {"shanghai", "nanjing", "qingdao"};
    private String descs[] = {
            "Shanghai is a bustling city.",
            "Nanjing is a city of culture.",
            "Qingdao is a beautiful city"
    };

    // 生成索引
    @Test
    public void index() throws Exception {
        IndexWriter writer = getWriter(); //获取写索引的实例
        for(int i = 0; i < ids.length; i++) {
            Document doc = new Document();
//            doc.add(new IntField("id", ids[i], Field.Store.YES));
            doc.add(new StringField("id", ids[i], Field.Store.YES));
            doc.add(new StringField("city", citys[i], Field.Store.YES));
            doc.add(new TextField("descs", descs[i], Field.Store.NO));
            writer.addDocument(doc); //添加文档
        }
        writer.close(); //close了才真正写到文档中
    }

    // 获取IndexWriter实例
    private IndexWriter getWriter() throws Exception {
        dir = FSDirectory.open(Paths.get("D:\\lucene\\02"));
        Analyzer analyzer = new StandardAnalyzer(); //标准分词器，会自动去掉空格啊，is a the等单词
        IndexWriterConfig config = new IndexWriterConfig(analyzer); //将标准分词器配到写索引的配置中
        IndexWriter writer = new IndexWriter(dir, config); //实例化写索引对象
        return writer;
    }

    // 测试写入了几个文档
    @Test
    public void testIndexWriter() throws Exception {
        IndexWriter writer = getWriter();
        System.out.println("总共写入了" + writer.numDocs() + "个文档");
        writer.close();
    }

    /**
     * 以id遍历整个文档
     * @throws Exception
     */
    @Test
    public void iter() throws Exception {

        // 读取索引
        IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get("D:\\lucene\\02")));

        // 索引查询器
        IndexSearcher searcher = new IndexSearcher(reader);

        PrefixQuery prefixQuery = new PrefixQuery(new Term("city", "s"));
        TopDocs hits = searcher.search(prefixQuery, 10);

        System.out.println("匹配到" + hits.totalHits + "条文档");

        for (ScoreDoc score : hits.scoreDocs) {
            Document doc = searcher.doc(score.doc);
            System.out.println(doc.get("id") + ", " + doc.get("city") + ", " + doc.get("desc"));
        }

        reader.close();
    }

    /**
     * 3. 更新文档
     * @throws Exception
     */
    @Test
    public void testUpdate() throws Exception {
        IndexWriter writer = getWriter();
        // 新建一个Document
        Document doc = new Document();
//        doc.add(new IntField("id", 5, Field.Store.YES));
        doc.add(new StringField("id", "5", Field.Store.YES));
        doc.add(new StringField("city", "shanghai22", Field.Store.YES));
        doc.add(new TextField("descs", "shanghai update", Field.Store.NO)); // Field.Store.NO 没存到索引文件中

        /**
         * 将原来id为1对应的文档，用新建的文档替换
         * 注意：当标识id为字符串类型时，替换成功，id也替换了!!!
         * 当标识id为整型时，先删除再创建
         */
        writer.updateDocument(new Term("id", "1"), doc);
        writer.close();

        iter();
    }

    /**
     * 关键字查询
     * @throws IOException
     */
    @Test
    public void testTermQuery() throws IOException {
        /**************** 查询 *****************/
        IndexReader indexReader = DirectoryReader.open(FSDirectory.open(Paths.get("D:\\lucene\\03")));

        IndexSearcher indexSearcher = new IndexSearcher(indexReader);

        String searchField = "city";
        String queryStr = "shanghai22";

        Term term = new Term(searchField, queryStr);
        Query query = new TermQuery(term);

        TopDocs hits = indexSearcher.search(query, 10);
        System.out.println("匹配" + queryStr + "总共查询到" + hits.totalHits + "个文档");
        for(ScoreDoc score : hits.scoreDocs) {
            Document doc = indexSearcher.doc(score.doc);
            System.out.println(doc.get("id") + ", " + doc.get("city")); // 打印对应的id
        }
        indexReader.close();
    }



    /**
     * 2. 测试删除文档
     * 2.2 在合并后
     * note: 测试完合并前删除，要删除目录下的所有索引文件，重新调用index方法生成
     * @throws Exception
     */
    @Test
    public void testDeleteAfterMerge() throws Exception {
        IndexWriter writer = getWriter();
        System.out.println("删除前有" + writer.numDocs() + "个文档");
        writer.deleteDocuments(new Term("id", "2")); //删除id=1对应的文档
        writer.forceMergeDeletes(); //强制合并（强制删除），没有索引了
        writer.commit(); //提交删除，真的删除了
        System.out.println("删除后最大文档数：" + writer.maxDoc());
        System.out.println("删除后实际文档数：" + writer.numDocs());
        writer.close();
    }

    /**
     * 2. 测试删除文档
     * 2.1 在合并前
     * @throws Exception
     */
    @Test
    public void testDeleteBeforeMerge() throws Exception {
        IndexWriter writer = getWriter();
        System.out.println("删除前有" + writer.numDocs() + "个文档");
        writer.deleteDocuments(new Term("id", "1")); //删除id=1对应的文档
        writer.commit(); //提交删除,并没有真正删除
        System.out.println("删除后最大文档数：" + writer.maxDoc());
        System.out.println("删除后实际文档数：" + writer.numDocs());
        writer.close();
    }

    /**
     * 1. 测试读取文档
     * @throws Exception
     */
    @Test
    public void testIndexReader() throws Exception {
        dir = FSDirectory.open(Paths.get("D:\\lucene\\03"));
        IndexReader reader = DirectoryReader.open(dir);
        System.out.println("最大文档数：" + reader.maxDoc());
        System.out.println("实际文档数：" + reader.numDocs());
        reader.close();
    }


}
