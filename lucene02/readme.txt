Digest
1. lucene版本5.3.1
2. Test1说明
    a) 匹配英文，resources目录下的a.txt,b.txt文件为数据文件，以此为基础产生索引文件
    b) 创建索引库、查询索引库
    c) 词典文件（Term Dictionary）、频率文件（frequencies）和位置文件（positions）
3. Test2说明
    a) 匹配中文，要引入smartcn依赖
    b) 创建索引库、查询索引库
    c) 高亮显示，要引入highlighter依赖
    d) 得分：多处出现关键字，lucene会自动计算每一处的得分，最接近用户搜索的
    e) 通过短语来检索（了解）PhraseQuery
4. Test3和Test31说明
    a) 生成索引、查询索引
    b) 删除文档在合并前、删除文档在合并后、全部删除(deleteAll)、恢复(rollback)
    c) 更新文档，是有限制的！
        1) 设置Term的Field类型必须为字符串类型
        2) 如果是int类型，必须先删除后创建
        3) 参考Test3#testUpdate()
    d) 文档域加权，比如boss
5. Test4说明
    a) 对特定项搜索，如：按content包含hello查询
    b) 使用查询表达式QueryParser搜索，支持or,and,~等
    c) 指定数字范围搜索，如：按id范围
    d) 指定字符串开头搜索，如：按city以sh开头
    e) 多条件查询（即组合查询），如：查询id在1到2之间，并且以s开头的city
    f) 字符串范围查询 TermRangeQuery
    g) 排序查询 Sort,DocValuesField
    g) 查询全部 MatchAllDocsQuery
    h) 模糊查询 FuzzyQuery
    i) 通配符查询 WildcardQuery

Reference
https://www.jianshu.com/p/48aad01ebc7c
https://blog.csdn.net/eson_15/article/category/6301481