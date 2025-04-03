/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.util;

import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.SimpleHtmlSerializer;
import org.htmlcleaner.TagNode;

/**
 *
 * @author Fernando
 */
public abstract class HtmlUtil {
    
    public static String cleanHtml(String htmlText){
        
        HtmlCleaner cleaner = new HtmlCleaner();
        
        CleanerProperties props = cleaner.getProperties();
        
        TagNode node = cleaner.clean(htmlText);
        
        SimpleHtmlSerializer htmlSerializer = new SimpleHtmlSerializer(props);
        
        String htmlResult = htmlSerializer.getAsString(node).replaceAll("\\<\\?xml(.+?)\\?\\>", "").trim();
        
        return htmlResult;
    }
    
}
