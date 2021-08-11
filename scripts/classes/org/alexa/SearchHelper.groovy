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
            def month = date[Calendar.MONTH] + 1
            def day = date[Calendar.DAY_OF_MONTH]
            def dateQuery = "(factMonth_s:${month} AND factDay_s:${day})"

            if(date[Calendar.YEAR]<Calendar.getInstance().get(Calendar.YEAR)) {
                def year = date[Calendar.YEAR]
                dateQuery = "(factYear_s:${year} AND factMonth_s:${month} AND factDay_s:${day})"
            }

            q = "${q} AND ${dateQuery}"
        }
        
        if (text) {
            def textQuery = "(fact_html:${text} OR detail_html:${text})"
            
            q = "${q} and ${textQuery}"
        }

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
                    fact.put("title", "c")//doc.facts_o.item.fact_html_raw)
                    fact.put("detail", "d")//doc.facts_o.item.detail_html)
                    facts << fact  
                }
                
            }

            return facts
        }
    }
}