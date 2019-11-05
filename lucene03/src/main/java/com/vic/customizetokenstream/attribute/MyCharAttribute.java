package com.vic.customizetokenstream.attribute;

import org.apache.lucene.util.Attribute;

/**
 * 建立自己的Attribute接口
 */
public interface MyCharAttribute extends Attribute {

    void setChars(char[] buffer, int length);

    char[] getChars();

    int getLength();

    String getString();

}
