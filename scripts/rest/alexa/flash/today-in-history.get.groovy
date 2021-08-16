import org.alexa.*
//import java.util.Calendar.*
//import java.text.SimpleDateFormat

def searchHelper = new SearchHelper(elasticsearch)
def fact = new Fact(searchHelper)

def results = []

//def cal = Calendar.getInstance()
def todaysDate = "1776/07/04" //""+new SimpleDateFormat("yyyy/MM/dd").format(cal.getTime())

 try {
    def facts = fact.getFactsAsObj(siteItemService, todaysDate)
    facts.each { factResult ->
        def flashItem = createFlashItem(factResult)
        results.add(flashItem)
    }

} 
catch (err) {
    logger.info("We broke. This is the error: " + err)
}  

return results

def createFlashItem(item) {
    def flashItem = [
        "uid": UUID.randomUUID().toString(),
        "updateDate": new Date(),
        "titleText": item.title,
        "mainText": item.detail,
        "redirectionUrl": null
    ]

    return flashItem
}