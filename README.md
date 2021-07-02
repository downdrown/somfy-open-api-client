![Build](https://github.com/downdrown/somfy-open-api-client/actions/workflows/build.yml/badge.svg)

# Somfy Open API Client

Somfy Open API Client is a Java library for easy interaction with Somfy's Open API.
For details see Somfy's official documentation [here](https://developer.somfy.com/apis-docs).

## Motivation

Somfy („**S**ocieté d’**O**utillage et du **M**écanique du **F**aucign**y**“)
is a french company that mainly produces controllers and drives for entrance gates,
garage doors, blinds and awnings.
They also produce other home automation products such as security devices.

I personally use Somfy blinds at home but was not happy about their app ( sorry guys ¯\_(ツ)_/¯ ) and wanted to create
my own application to control my blinds.
I happily discovered that there is an "Open API" that you can use **if** you have one of Somfy's HUBs at home.

I then thought *"maybe I'm not the only one that needs this api"* and decided to create this library.

It is completely free to use, however I'll enjoy a cup if you want to [buymeacoffee.com](https://www.buymeacoffee.com/downdrown)

I hope it is useful for you, enjoy it!


## Limitations

Since Somfy uses
the [Authorization Code Flow](https://auth0.com/docs/flows/authorization-code-flow#:~:text=Because%20regular%20web%20apps%20are,Authorization%20Code%20for%20a%20token.)
to authenticate API requests you need to be able to interact with the client's User-Agent in order to use this API.

## Licencing

This library is distributed under the [MIT licence](https://choosealicense.com/licenses/mit/). This is a pretty
permissive licence and you are allowed to

* use this library commercially
* use this library private
* distribute this library
* modify this library to fit your needs

You **do not** get any *warranty* or guarantees for liability and will use this library at your own risk.
