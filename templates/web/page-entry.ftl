<#import "/templates/system/common/cstudio-support.ftl" as studio />


<!DOCTYPE html>
<html lang="en">
    <head>
        <#include  "/templates/web/common/head.ftl" />
    </head>
    <body>
        <#include  "/templates/web/common/header.ftl" />

        <div id="browser" class="container">
            <div class="row">
				<div class="col-md-6" >
                     <a href="https://developer.amazon.com/alexa/console/ask" target="new">
                        <img src="/static-assets/app/amazon-alexa.png" >
                    </a>
				</div>
				<div class="col-md-6" stye="text-align: center;">
				    <h3>To use the Alexa Skill Testing Tool you will need to sign-in to your Alexa Developer account and 
                       follow the <a href="/setup-instructions">documentation</a> to set up and connect your Alexa Skill.<h3>
                </div>
            </div>
        </div>
        <#include "/templates/web/common/help-modal.ftl" />        

        <#include "/templates/web/common/scripts.ftl" />
	    <@studio.toolSupport/>
    </body>
</html>

