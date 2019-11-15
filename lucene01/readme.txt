Digest
1. lucene 8.2.0版本的官方demo
    依赖见pom.xml
2. IndexFiles.java: code to create a Lucene index
    步骤
    a) 传入将要索引的目录或文件（获取目录Path docDir = Paths.get(docsPath);）
    b) 初始化IndexWriter、打开Directory（存储index的目录）、初始化Analyzer（StandardAnalyzer为标准，各种语言使用不同analyzer）、初始化IndexWriterConfig
    c) 创建Document对象并添加到IndexWriter中
    d) 使用IndexWriter和IndexWriterConfig索引化doc
    e) 测试1：索引大小为51MB，原数据大小为176MB，28%，耗时118276 total milliseconds，约为2分钟
       测试2：索引大小为105MB，原数据大小为360MB，29%，耗时28667 total milliseconds，约为4.8分钟
4. SearchFiles.java: code to search a Lucene index
    步骤
    a) 声明索引名称及位置
    b) 打开索引、初始化IndexSearcher、Analyzer、QueryParser(field, analyzer)
        IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(index)));
        IndexSearcher searcher = new IndexSearcher(reader);
        Analyzer analyzer = new StandardAnalyzer();
        QueryParser parser = new QueryParser(field, analyzer);
    c) 读取并解析用户输入
        Query query = parser.parse(line);
    d) 执行查询，获取以score排序的结果
        TopDocs results = IndexSearcher.search(query, n)
        results.scoreDocs //The top hits for the query. results.scoreDocs[i].doc表示document
        results.totalHits //The total number of hits for the query

Reference
https://lucene.apache.org/core/8_2_0/demo/overview-summary.html#overview.description
https://lucene.apache.org/core/8_2_0/core/overview-summary.html#overview.description