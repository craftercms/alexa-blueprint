/*
*
* Released under MIT License
*
* Copyright 2022 Crafter Software Corporation
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

import groovy.json.JsonSlurper
import org.alexa.*
import java.io.*

def slurper = new JsonSlurper()
def alexaResponse = "<speak>Unset</speak>"
def endSession = false
def searchHelper = new SearchHelper(elasticsearch)
def fact = new Fact(searchHelper)

def requestBody = request.reader.text
logger.debug("Request from Alexa: " + requestBody)

def parsedReq = slurper.parseText(requestBody)

def session = parsedReq.session.sessionId
def intent = parsedReq.request.intent.name

if(!parsedReq.request.intent.slots.equals(null)) {
    if(parsedReq.request.intent.slots.Date) {
        try {
            def date = parsedReq.request.intent.slots.Date.value.replaceAll("-", "/")
            alexaResponse = fact.getFacts(siteItemService, session, date, null)
        } 
        catch (err) {
            logger.info("We broke. This is the error for intent ("+intent+"): " + err)
            alexaResponse = "<speak>There was an error with the request for intent ("+intent+"): " + err+"</speak>"
        }
    } 
    
    if (parsedReq.request.intent.slots.Search) {
        try {
            reqText = parsedReq.request.intent.slots.Search.value.toLowerCase()
            session = parsedReq.session.sessionId
            alexaResponse = fact.getFactDetailsByMap(session, reqText)
        } 
        catch (err) {
            logger.info("We broke. This is the error for intent ("+intent+"): " + err)
            alexaResponse = "<speak>There was an error with the request for intent ("+intent+"): " + err+"</speak>"
        }
    }
}

if(intent.equals("AMAZON.NoIntent")) {
    alexaResponse = "<speak>Goodbye!</speak>"
    endSession = true
}

if(intent.equals("AMAZON.YesIntent")) {
    alexaResponse = fact.getFactDetailsByYes(session)
    endSession = true
}

def alexaResult = [
  "version": "1.0",
  "response": [
    "outputSpeech": [
      "type": "SSML",
      "ssml": alexaResponse
    ],
    "reprompt": [
      "outputSpeech": [
        "type": "SSML",
        "ssml": "<speak>Can I help you with anything else?</speak>"
      ]
    ],
    "shouldEndSession": endSession
  ]
]

return alexaResult
