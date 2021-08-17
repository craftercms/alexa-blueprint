package org.alexa

import groovy.util.logging.Slf4j
import org.craftercms.engine.service.impl.SiteItemServiceImpl
import org.alexa.SearchHelper
import java.util.Date
import java.lang.*
import java.text.*

@Slf4j
class Fact {
    static Map<String,String> sessionMap = new HashMap<String,String>()
    def searchHelper
    def start
    def rows
    
    def Fact(searchHelper) {
        this.searchHelper = searchHelper
    }
    
    def getFactsAsObj(SiteItemService, dt) {
        def alexaResponse = ""
        def facts = []

        if (dt) {
            facts = searchHelper.search(formatDate(dt), null)
        }
        
        return facts   
    }

    def getFacts(SiteItemService, session, dt, req) {
        try{
            def alexaResponse = ""
            def items

            if (dt) {
                items = searchHelper.search(formatDate(dt), null)
            }
            
            if (req) {
                items = searchHelper.search(null, req)
            }

            if(items.size > 0) {
                sessionMap.put(session, items)
            }
            
            if (items.size() > 1) {
                alexaResponse += "Here's what I found:<break time=\"1s\"/> "    
                items.each { v -> 
                alexaResponse += v.title.value.toString().concat(" <break time=\"1s\"/> ")
                }
    
                alexaResponse += "Which of these facts would you like to hear more about?"
            } else {
                alexaResponse = items.get(0).title.value.toString().concat("<break time=\"1s\"/>  Would you like to hear more about this fact?")
            }
    
            if (!alexaResponse.equals("unset")) {
              return formatResponse(alexaResponse)
            } else {
              return "<speak>We couldn't find any facts about that.</speak>"
            }
        } catch (err) {
            log.info("we broke! : " + err)
        }
    }

    def getFactDetailsByYes(session) {
        def alexaResponse = ""
        def items = sessionMap.get(session)
        items.each {
            alexaResponse += it.detail.value.toString()
        }
        
        if (!alexaResponse.equals(null)) {
            return formatResponse(alexaResponse)
        } else {
            return ""
        }
    }


    def getFactDetailsByMap(session, request) {
      def alexaResponse = ""

      if(request) {
        def items = sessionMap.get(session)
        
        items.each {
            if(it.title.value.toString().toLowerCase().contains(request) || it.detail.value.toString().toLowerCase().contains(request)) {
                alexaResponse += it.detail.value.toString()
            }
        }
      }

      if (!alexaResponse.equals(null)) {
        return formatResponse(alexaResponse)
      } else {
        return ""
      }
    }

    private def formatResponse(response) {
        return "<speak>" + response + "</speak>"
    }
    
    private def formatDate(dt) {
        Date date = new SimpleDateFormat("yyyy/MM/dd").parse(dt) //.clearTime()
        
        return date
    }
}