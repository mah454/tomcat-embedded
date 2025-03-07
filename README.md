### Tomcat Embedded 

Compile :   
```shell
mvn clean compile package
```
Create Keystore for ssl connection : 
```shell
keytool -genkey -alias tomcat-embedded -keyalg RSA -keystore keystore.jks -storepass tompass -validity 3650 -keysize 2048
```

Run application :
```shell
java -cp "target/lib/*" ir.moke.MainClass
```

Test api :   
```shell
# with httpie : 
http http://localhost:8080/sample 

# with curl :
curl -X GET http://localhost:8080/sample
```

Test websocket : 
```shell
websocat ws://localhost:8080/ws
```

