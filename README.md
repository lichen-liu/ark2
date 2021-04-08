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
### Prerequisites
- Docker
- Hyperledger Flex Docker Image

## Step 1 - Gradle build the project
```
gradle build
cd ./hlf2-network
```

## Step 2 - (Optional) Download bin and config files
```
curl -sSL https://bit.ly/2ysbOFE | bash -s -- -d -s
```

## Step 3 - Initialize and start up the test network
```
./network.sh up createChannel
```

## Step 4 - Install the chaincode
```
./network deployCC -l java
```
At the end of step 3, a chaincode function is automatically invoked, if the deployment is successful, you should see:
![image](https://user-images.githubusercontent.com/19659223/113533938-3d8d3300-959d-11eb-94d2-183453de5291.png)
<br /> that fetches data from the ledger

## Step 5 - Stop the network
```
./network.sh down
```