# rocket-chat-setting

A Java commandline tool to get/set rocketchat setting values

Uses https://github.com/col-panic/rocket-chat-rest-client, 
an adapted version of https://github.com/baloise/rocket-chat-rest-client.


## Building

1. Import as Eclipse project
2. Select `src/rocket_chat_setting/Main.java`  and `Export` / `Runnable Jar File`

## Usage

```
$> java -jar RocketchatSetting.jar 
The following options are required: [-u], [-l], [-p]

Usage: <main class> [options]
  Options:
  * -l
      username
  * -p
      password
    -s
      values to set as key=value; multiple occurences allowed
      Default: []
    -t
      Trust all HTTPS certificates
      Default: false
  * -u
      RocketChat service url, e.g. https://rocketchat:3000
    -v
      Verbose output
      Default: false
```

## Examples

```
java -jar /RocketchatSetting.jar -l AdminUser -p AdminPassword -u https://my.service/chat -v \
    -s SAML_Custom_Default=true -s SAML_Custom_Default_provider=rocketchat-saml \
    -s SAML_Custom_Default_issuer=rocketchat-saml -s SAML_Custom_Default_button_label_text=Some_Text \
    -s SAML_Custom_Default_name_overwrite=true  -s SAML_Custom_Default_mail_overwrite=true \
    -s SAML_Custom_Default_generate_username=false -s SAML_Custom_Default_immutable_property=Username
```

## Changes

* Jan 15, 20 - major refactorings
* Aug 22, 19 - Support for direct read/set of multiple settings
