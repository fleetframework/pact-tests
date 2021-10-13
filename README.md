Create contracts (consumer)
=========================
```
gradlew :consumer:test -PbrokerUrl=BROKER_URL_WITHOUT_PROTOCOL -PbrokerUsername=BROKER_USER -PbrokerPassword=BROKER_PASS -Dproject=PROJECT -Dusername=USER -Dpassword=PASS -Durl=URL  
```

Publish contracts to pact broker (consumer)
=========================
```
gradlew :consumer:pactPublish -PbrokerUrl=BROKER_URL_WITHOUT_PROTOCOL -PbrokerUsername=BROKER_USER -PbrokerPassword=BROKER_PASS
```

Verify contracts (provider)
========================
```
gradlew :provider:test -DbrokerUrl=BROKER_URL_WITHOUT_PROTOCOL -DbrokerUsername=BROKER_USER -DbrokerPassword=BROKER_PASS -Durl=URL_WITHOUT_PROTOCOL 
```
