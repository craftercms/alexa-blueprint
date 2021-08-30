/*
*
* Released under MIT License
*
* Copyright 2021 Crafter Software Corporation
*
* Permission is hereby granted, free of charge, to any person obtaining a copy of this software and 
* associated documentation files (the "Software"), to deal in the Software without restriction, 
* including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, 
* and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do 
* so, subject to the following conditions:
*
* The above copyright notice and this permission notice shall be included in all copies or substantial * portions of the Software.
*
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR  * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL 
* THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR 
* OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, 
* ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR 
* OTHER DEALINGS IN THE SOFTWARE.
*
* @author ethanvert
*
*/


package org.alexa

import groovy.util.logging.Slf4j
import org.apache.commons.lang3.StringUtils
import org.craftercms.engine.service.UrlTransformationService
import org.elasticsearch.action.search.SearchRequest
import org.elasticsearch.index.query.QueryBuilders
import org.elasticsearch.search.builder.SearchSourceBuilder
import org.elasticsearch.search.sort.FieldSortBuilder
import org.elasticsearch.search.sort.SortOrder

@Slf4j
class SearchHelper {

    static final String FACT_CONTENT_TYPE_QUERY = "(content-type:\"/component/fact\")"
    static final int DEFAULT_START = 0
    static final int DEFAULT_ROWS = 10

    def elasticsearch

    SearchHelper(elasticsearch) {
        this.elasticsearch = elasticsearch
        
    }

    def search(date, text, start = DEFAULT_START, rows = DEFAULT_ROWS) {
        
        def q = "${FACT_CONTENT_TYPE_QUERY}"
        
        if (date) {
            def month = date.getMonth()+1//[Calendar.MONTH] + 1
            def day = date.getDay()//[Calendar.DAY_OF_MONTH]
            def dateQuery = "(factMonth_s:${month} AND factDay_s:${day})"

            //if(date[Calendar.YEAR]<Calendar.getInstance().get(Calendar.YEAR)) {
            if(date.getYear() && date.getYear() > 0) {
              
                def year = date.getYear()//[Calendar.YEAR]
                dateQuery = "(factYear_s:${year} AND factMonth_s:${month} AND factDay_s:${day})"
            }

            q = "${q} AND ${dateQuery}"
        }
        
        if (text) {
            def textQuery = "(fact_html:${text} OR detail_html:${text})"
            
            q = "${q} and ${textQuery}"
        }
System.out.println(">>>"+q)
        def builder = new SearchSourceBuilder()
            .query(QueryBuilders.queryStringQuery(q))
            .from(start)
            .size(rows)
        
        def req = new SearchRequest().source(builder)
        def result = elasticsearch.search(req)
        
        if (result) {
            return processUserSearchResults(result)
        } else {
            return []
        }

    }

    private def processUserSearchResults(result) {
        def facts = []
        def hits = result.hits.hits

        if (hits) {

            hits.each { hit ->
                def doc = hit.getSourceAsMap()
                if(doc.facts_o && doc.facts_o.item && doc.facts_o.item.size() > 2) {     

                    try {
                        def fact = [:]
                        doc.facts_o.item.each { k, v ->
                            v = v.replace("<p class=\"disappointed\">", "<amazon:emotion name=\"disappointed\" intensity=\"high\">")
                            v = v.replace("<span class=\"disappointed\">", "<amazon:emotion name=\"disappointed\" intensity=\"high\">")
                            v = v.replace("<p class=\"excited\">", "<amazon:emotion name=\"excited\" intensity=\"high\">")
                            v = v.replace("<span class=\"excited\">", "<amazon:emotion name=\"excited\" intensity=\"high\">")
                            v = v.replace("</span>", "</amazon:emotion>")
                            v = v.replace("</p>", "</amazon:emotion>")
                            v = v.replace("<span>", "<amazon:emotion name=\"excited\" intensity=\"low\">")
                            v = v.replace("<br />", " ")
                            v = v.replace("<p>", "<amazon:emotion name=\"excited\" intensity=\"low\">")
                            
                            if(k.equals("fact_html_raw")) {
                               fact.put("title", v)
                            }
                            else if(k.equals("detail_html_raw")) {
                               fact.put("detail", v)
                            }
                        }
                        facts << fact            
                    }
                    catch(err) {
                    }

                } 
                else {
                    def fact = [:]
                    fact.put("title", doc.facts_o.item.fact_html_raw)
                    fact.put("detail", doc.facts_o.item.detail_html)
                    facts << fact  
                }
                
            }

            return facts
        }
    }
}
