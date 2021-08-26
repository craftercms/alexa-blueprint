# Alexa Facts Skill Blueprint

The Alexa Fact Skill Blueprint is an example of a headless content project that backs an Alexa Skill.

# Installation

The blueprint can be installed to your site from the Crafter CMS Marketplace.

# Setup

After the plugin has been installed you need setup your Alexa Skill. 

## Prerequisites
Must set up company account on Alexa Developer Console (https://developer.amazon.com/alexa/console/ask)
Must be able to post to an HTTPS address

## Set up

### Alexa Developer Console
In order to configure this Blueprint, you can either copy the JSON from below, or manually create the skill from scratch yourself.

To learn more about skills refer to the Alexa Developer Console docs here: 

https://developer.amazon.com/en-US/docs/alexa/ask-overviews/build-skills-with-the-alexa-skills-kit.html

### Replicate by copying JSON
#### Step 1
In the Alexa Developer Console (https://developer.amazon.com/alexa/console/ask), you should log in with a corporate account. If you don’t have one, setting it up is simple. Just enter the information required on the sign up page.

#### Step 2
After signing up, navigate to create a new skill. Enter a name for your skill and the Custom option should be selected. Scroll down to step 2 and select "Provision your own" for the backend resources. This makes it so that Alexa will be able to communicate with the blueprint site.

#### Step 3
After creating the skill, select the Hello World template. This is a plain skill with a basic built-in intent. 

#### Step 4
Navigate to Custom > Interaction Model > JSON Editor and copy this code into the editor: 

```

   "interactionModel":{
      "languageModel":{
         "invocationName":"my crafter",
         "modelConfiguration":{
            "fallbackIntentSensitivity":{
               "level":"LOW"
            }
         },
         "intents":[
            {
               "name":"AMAZON.FallbackIntent",
               "samples":[
                  
               ]
            },
            {
               "name":"AMAZON.CancelIntent",
               "samples":[
                  
               ]
            },
            {
               "name":"AMAZON.HelpIntent",
               "samples":[
                  
               ]
            },
            {
               "name":"AMAZON.StopIntent",
               "samples":[
                  
               ]
            },
            {
               "name":"AMAZON.NavigateHomeIntent",
               "samples":[
                  
               ]
            },
            {
               "name":"fact",
               "slots":[
                  {
                     "name":"Date",
                     "type":"AMAZON.DATE"
                  }
               ],
               "samples":[
                  "tell me what happened on {Date}",
                  "tell me what happened {Date} in history",
                  "tell me what happened on {Date} in history"
               ]
            },
            {
               "name":"secondary",
               "slots":[
                  {
                     "name":"Search",
                     "type":"AMAZON.SearchQuery"
                  }
               ],
               "samples":[
                  "Fact {Search}",
                  "Tell me more about {Search}",
                  "Tell me about {Search}"
               ]
            },
            {
               "name":"AMAZON.YesIntent",
               "samples":[
                  
               ]
            },
            {
               "name":"AMAZON.NoIntent",
               "samples":[
                  
               ]
            }
         ],
         "types":[
            
         ]
      }
   }
}    
```



#### Step 5
The next step is to set up your endpoint. Navigate to Custom > Interaction Model > Endpoint. Select the https option and enter your endpoint. 

https://website.com/api/alexa/skill/fact.json

Example: https://alexafacts.craftercloud.io/api/alexa/skill/fact.json

#### Step 6
Save the model with the Save Model button and then build it with the build model.

You should get an on-screen notification when the process is finished.

#### Step 7
Navigate to the Test option on the top banner and on the dropdown next to “Test is disabled for this skill”, select Development and you should be free to test your skill.

Building Your Skill and Using the Alexa Fact Skill
After importing the interaction module for your skill, the final step is to build the model which should be as simple as navigating back to Custom and selecting number 3 in the list on the right side of the screen. It should take a few moments to complete the build.

Moving on to the Test selection on the upper banner, select Development in the only dropdown menu on the page. 


To invoke your skill, your line of dialogue will look something like:

```
Alexa tell {invocation name} to {intent utterance}
```

The invocation name in this Blueprint is “my crafter” and an example utterance would be “tell me what happened on {Date}”. Once a session, or conversation, is started, there is no need to re-invoke the skill. You can continue with only intent utterances until the dialogue is finished.

### REST Script
In order to handle the request sent by Alexa, usage of a REST script is necessary. The request Alexa sends over is in JSON format, and the response should be in such format as well. To learn more about REST Scripts, refer here: https://docs.craftercms.org/en/3.1/developers/projects/engine/api/groovy-api.html



# Usage

### Service Usage
Once the skill has been configured in Alexa Developer Console and your service is available to Amazon over HTTPS you can use the test console to invoke your skill.

To invoke your skill, your line of dialogue will look something like:

```
Alexa tell {invocation name} to {intent utterance}
```
### CMS Usage
Use the CMS to edit and create new facts. Publish the facts to make them available to the skill.
