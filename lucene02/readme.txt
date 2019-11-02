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
4. Test3和Test31说明
    a) 生成索引、查询索引
    b) 删除文档在合并前、删除文档在合并后
    c) 更新文档 TODO
    d) 遍历整个文档 TODO
    e) 文档域加权
5. Test4说明


Reference
https://www.jianshu.com/p/48aad01ebc7c
https://blog.csdn.net/eson_15/article/details/51792910
https://blog.csdn.net/eson_15/article/details/51802981
https://blog.csdn.net/eson_15/article/details/51798774
https://blog.csdn.net/eson_15/article/details/51802974