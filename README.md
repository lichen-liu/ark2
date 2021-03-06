# ark2
Anonymous Immutable Reward-Motivated Forum on Blockchain

[![java_gradle_build](https://github.com/lichen-liu/ark2/actions/workflows/gradle.yml/badge.svg)](https://github.com/lichen-liu/ark2/actions/workflows/gradle.yml)

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

## Check Number of Lines for Java
```
find ./ -name '*.java' | xargs wc -l | sort -nr
```

# Project Application
Application (client) code that interacts with the blockchain via Hyperledger Fabric Gateway APIs

## Requirements
1. Netbeans (Java Swing GUI Development): https://netbeans.apache.org/

## How
```
cd application
```

### To run GUI
```bash
gradle runApp
```

### To run TEST
```bash
# Input the test ID
gradle runApp -Pargs="test 1"
```

### To analyze TEST performance
```
# Input the performance run name
python -m analyzer performance benchmarks/perf/perf_*.csv
```

### To analyze SIMULATION TEST reward modelling
```
# Input the rewards run name
python -m analyzer simulation benchmarks/simulation/rewards/*/
```

# Project Blockchain
Hyperledger Fabric blockchain and chaincodes

## How
```
cd blockchain
```

### Quick Run - Restart the network and install the chaincode
```bash
# Prerequiste
cd hlf2-network

# Quick Run
pushd ../.; gradle build; gradle install; popd; ./network.sh down; ./network.sh up createChannel -ca;./network.sh deployCC -l java;
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
