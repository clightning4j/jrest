FROM vincenzopalazzo/clightning4j-node:v0.10.0-dev1
LABEL mantainer="Vincenzo Palazzo vincenzopalazzodev@gmail.com"

RUN lightningd --disable-plugin=/opt/lightning-rest/lightning-rest-gen.sh \
    --network=testnet  \
    --alias=clihgnting4j-node \
    --disable-plugin=bcli \
    --log-level=debug \
    --log-file=/clightning.log \
    --plugin=/opt/btcli4j/btcli4j-gen.sh \
    --daemon

COPY ./docker/entrypoint.sh .
RUN chmod +x entrypoint.sh

CMD ["./entrypoint.sh"]