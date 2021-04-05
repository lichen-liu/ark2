# ark2
Anonymous Blockchain-Based Forum

# Install Gradle on your local environment
https://gradle.org/install/

# How to build
```
gradle build
```

# How to run
```
gradle run
```

# Install Test Hyperledger Network on your local machine
## Prerequisite 
```
Docker
```
## Step 1 - Gradle build the project
## Step 2 - Initialize and start up the test network
```
./network.sh up createChannel
```
## Step 3 - Install the chaincode
```
./network deployCC -l java
```
At the end of step 3, a chaincode function is automatically invoked, if the deployment is successful, you should see:
![image](https://user-images.githubusercontent.com/19659223/113533938-3d8d3300-959d-11eb-94d2-183453de5291.png)
<br /> that fetches data from the ledger
