import fileinput
filenames = ["./docker/docker-compose-test-net.yaml", "./docker/docker-compose-e2e.yaml", "./configtx/configtx.yaml", "./config/configtx.yaml", "./scripts/deployCC.sh", "./scripts/createChannel.sh"]
for f in filenames:
    with fileinput.FileInput(f, inplace=True, backup='.bak') as file:
        for line in file:
            print(line.replace("7050", "9050"), end='')