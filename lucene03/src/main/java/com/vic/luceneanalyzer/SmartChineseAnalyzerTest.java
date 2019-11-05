package com.vic.luceneanalyzer;


import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import java.io.IOException;

/**
 * Lucene提供的中文分词器模块，中文分词器 SmartChineseAnalyzer
 * 中英文分词效果都不好
 */
public class SmartChineseAnalyzerTest {

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
        String etext = "Analysis is one of the main causes of slow indexing. Simply put, the more you analyze the slower analyze the indexing (in most cases).";
        String chineseText = "张三说的确实在理。";
        // Lucene 的中文分词器 SmartChineseAnalyzer
        try (Analyzer smart = new SmartChineseAnalyzer()) {
            TokenStream ts = smart.tokenStream("content", etext);
            System.out.println("smart中文分词器，英文分词效果：");
            doToken(ts);
            ts = smart.tokenStream("content", chineseText);
            System.out.println("smart中文分词器，中文分词效果：");
            doToken(ts);
        }

    }
}
