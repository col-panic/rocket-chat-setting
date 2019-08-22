# rocket-chat-setting

A Java commandline tool to get/set rocketchat setting values

Uses https://github.com/col-panic/rocket-chat-rest-client, 
an adapted version of https://github.com/baloise/rocket-chat-rest-client.


## Building

1. Import as Eclipse project
2. Select `src/rocket_chat_setting/Main.java`  and `Export` / `Runnable Jar File`

## Usage

```
marco@Marcos-MacBook-Pro-2019 ~> java -jar RocketchatSetting.jar 
The following options are required: [-u], [-l], [-p]

Usage: <main class> [options] [ setting_key ]... | if set mode: [ setting_key 
      setting_value ]...
  Options:
  * -l
      username
  * -p
      password
    -s
      Enable set mode, expects key/value tuples a parameters
      Default: false
    -t
      Trust all HTTPS certificates
      Default: false
  * -u
      RocketChat service url, e.g. https://rocketchat:3000
    -v
      Verbose output
      Default: false
```

## Changes

* Aug 22, 19 - Support for direct read/set of multiple settings
