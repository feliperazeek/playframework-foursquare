# Play Framework - Module play-foursquare 0.1
by Felipe Oliveira
http://geeks.aretotally.in
http://twitter.com/_felipera

Easily integrate Foursquare's OAuth authentication into any Java web application using the super cool Play Framework (http://playframework.org).


## Prerequisites

Register your application with Foursquare (http://developer.foursquare.com/). 


### Install the module

Install the foursquare module from the modules repository:

`
play install foursquare
`

### Enable the module

After installing the module, add the following to your application.conf to enable it:

`
module.foursquare=${play.path}/modules/foursquare-0.1
`

### Configure the module

And finally, you need to configure the module by setting these properties in your application.conf (Register API Key at http://developer.foursquare.com/):

     foursquare.apiKey=YOUR_API_KEY_HERE
     foursquare.secret=YOUR_APP_SECRET_HERE
     foursquare.model=models.User
     foursquare.landUrl=/

All of the properties are required.



### Use the foursquare.button tag in your view

The foursquare.button tag outputs a link that will prompt your users to authenticate with Foursquare when it is clicked.

`#{foursquare.button /}`

* label which defaults to button *Login with Foursquare*
* cssClass which defaults to *play-foursquare-button*


### Define your OAuth callback

Your foursquare.model class needs to implement a static method called @foursquareOAuthCallback@. After a user has authenticated using FourSquare, the module will call this method with Foursquare's profile information. This is your opportunity to add the user to your database, add the user to your session, or do anything else you want.


## Source Code

Fork it on Github https://github.com/feliperazeek/playframework-foursquare.
