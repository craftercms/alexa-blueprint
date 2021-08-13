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
def date =  null
def term = null

if(intent.equals("Learn")) {
    date = parsedReq.request.intent.slots.Date.value
}

if(intent.equals("Search")) {
    term = parsedReq.request.intent.slots.value.value
}


//retrieving facts
if(date != "null") {
    try {
        date = date.replaceAll("-", "/")
        alexaResponse = fact.getFacts(siteItemService, session, date, null)
    } catch (err) {
        logger.info("We broke. This is the error: " + err)
        alexaResponse = "<speak>There was an error with the request</speak>"
    }
} 
    
if(intent.equals("Search")) {
    try {
         reqText = params.q.replaceAll("_", " ")
         if(fact.sessionMap){
            alexaResponse = fact.getFactDetailsByMap(session, reqText)
         } else {
            alexaResponse = fact.getFacts(siteItemService, session, null, reqText)
         }
    } catch (err) {
         logger.info("We broke. This is the error: " + err)
         alexaResponse = "<speak>There was an error with the request</speak>"
    }
    endSession = true
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