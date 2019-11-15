package com.vic.extikanalyzer;


import com.vic.ikanalyzer.IKAnalyzer4Lucene7;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import java.io.IOException;


/**
 * 扩展 IKAnalyzer的词典测试
 *
 */
public class ExtendedIKAnalyzerDicTest {

    private static void doToken(TokenStream ts) throws IOException {
        ts.reset();
        CharTermAttribute cta = ts.getAttribute(CharTermAttribute.class);
        while (ts.incrementToken()) {
            System.out.print(cta.toString() + "|");
        }
        System.out.println();
        ts.end();
        ts.close();
    }

    public static void main(String[] args) throws IOException {
        String chineseText = "厉害了我的国一经播出，受到各方好评，强烈激发了国人的爱国之情、自豪感！";
        // IKAnalyzer 细粒度切分
        try (Analyzer ik = new IKAnalyzer4Lucene7()) {
            TokenStream ts = ik.tokenStream("content", chineseText);
            System.out.println("IKAnalyzer中文分词器 细粒度切分，中文分词效果：");
            doToken(ts);
        }

        // IKAnalyzer 智能切分
        try (Analyzer ik = new IKAnalyzer4Lucene7(true)) {
            TokenStream ts = ik.tokenStream("content", chineseText);
            System.out.println("IKAnalyzer中文分词器 智能切分，中文分词效果：");
            doToken(ts);
        }

        /*
        output:
        未加停用词前：
        IKAnalyzer中文分词器 细粒度切分，中文分词效果：
        厉害|害了|我|的|国一|一经|一|经|播出|受到|各方|好评|强烈|激发|发了|国人|的|爱国|之情|自豪感|自豪|感|
        IKAnalyzer中文分词器 智能切分，中文分词效果：
        厉|害了|我|的|国|一经|播出|受到|各方|好评|强烈|激|发了|国人|的|爱国|之情|自豪感|

        说明：输出我(1)、的(2)

        加停用词后：
        IKAnalyzer中文分词器 细粒度切分，中文分词效果：
        厉害|害了|我|国一|一经|一|经|播出|受到|各方|好评|强烈|激发|发了|国人|爱国|之情|自豪感|自豪|感|
        IKAnalyzer中文分词器 智能切分，中文分词效果：
        厉|害了|我|国|一经|播出|受到|各方|好评|强烈|激|发了|国人|爱国|之情|自豪感|

        说明：“的”没有了

        加入扩展字典ext.dic、扩展停用词my_ext_stopword.dic后：
        IKAnalyzer中文分词器 细粒度切分，中文分词效果：
        厉害了我的国|厉害|害了|我|国一|一经|一|经|播出|受到|各方|好评|强烈|激发|发了|国人|爱国|之情|自豪感|自豪|感|
        IKAnalyzer中文分词器 智能切分，中文分词效果：
        厉害了我的国|一经|播出|受到|各方|好评|强烈|激|发了|国人|爱国|之情|自豪感|
         */
    }
}