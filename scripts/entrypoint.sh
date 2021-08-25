#! /bin/bash
lightningd --disable-plugin=/opt/lightning-rest/lightning-rest-gen.sh \
    --network=testnet  \
    --alias=clihgnting4j-node \
    --disable-plugin=bcli \
    --log-level=debug \
    --plugin=/opt/btcli4j/btcli4j-gen.sh \
    --log-file=/clightning.log \
    --daemon

lightning-cli --testnet getinfo

cd /code && ./gradlew test