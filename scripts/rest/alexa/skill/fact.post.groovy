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