# ark2
Anonymous Blockchain-Based Forum

# Requirements

## Gradle
https://gradle.org/install/

## Docker

# Project Application
Application (client) code that interacts with the blockchain via Hyperledger Flex APIs

## How

### To build
```
gradle build
```

### To run
```
gradle run
```

# Project Blockchain
Hyperledger Flex blockchain and smart contracts

## How

### Step 0 - (Optional) Download bin, config and Docker image
```
curl -sSL https://bit.ly/2ysbOFE | bash -s -- -s
```

### Step 1 - Gradle build the smart contract
```
gradle build
gradle install
cd ./hlf2-network
```

### Step 2 - Initialize and start up the network
```
./network.sh up createChannel
```

### Step 3 - Install the smart contract
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
