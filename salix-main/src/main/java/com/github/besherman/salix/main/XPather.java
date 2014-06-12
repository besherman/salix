/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.github.besherman.salix.main;

import java.util.stream.IntStream;
import java.util.stream.Stream;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 *
 * @author Richard
 */
public class XPather {
    private static final ThreadLocal<XPath> instance = new ThreadLocal<XPath>() {
        @Override
        protected XPath initialValue() {
            return XPathFactory.newInstance().newXPath();
        }        
    };
    
    public static Stream<Element> evaluate(String expression, Object source) {
        XPath xpath = instance.get();
        try {
            NodeList nodeList = (NodeList)xpath.evaluate(expression, source, XPathConstants.NODESET);
            return IntStream.range(0, nodeList.getLength()).mapToObj(nodeList::item).map((n) -> (Element)n);            
        } catch(XPathExpressionException ex) {
            throw new RuntimeException(ex);
        }
    }   

}
