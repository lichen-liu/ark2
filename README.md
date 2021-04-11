# ark2
Anonymous Blockchain-Based Forum

# Requirements

## Gradle
https://gradle.org/install/

## Docker

# Helps

## API Documents
1. Chaincode API: https://hyperledger.github.io/fabric-chaincode-java/release-2.2/api
2. Gateway SDK: https://hyperledger.github.io/fabric-gateway-java/release-2.2/

## Code Samples
1. https://github.com/hyperledger/fabric-samples/tree/main/asset-transfer-basic

# Project Application
Application (client) code that interacts with the blockchain via Hyperledger Flex Gateway APIs

## How
```
cd application
```

### To run
```
gradle runApp
```

# Project Blockchain
Hyperledger Flex blockchain and chaincodes

## How
```
cd blockchain
```

### Step 0 - (Optional) Download bin, config and Docker image
```
cd hlf2-network
curl -sSL https://bit.ly/2ysbOFE | bash -s -- -s
cd ..
```

### Step 1 - Gradle build the chaincode
```
gradle build
gradle install
cd hlf2-network
```

### Step 2 - Initialize and start up the network
```
./network.sh up -ca
./network.sh createChannel
```

### Step 3 - Install the chaincode
```
./network.sh deployCC -l java
```
At the end of step 3, a chaincode function is automatically invoked, if the deployment is successful, you should see:
![image](https://user-images.githubusercontent.com/19659223/113533938-3d8d3300-959d-11eb-94d2-183453de5291.png)
<br /> that fetches data from the ledger

### Step 4 - Stop the network
```
./network.sh down
```
