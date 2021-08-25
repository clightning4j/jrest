FROM vincenzopalazzo/clightning4j-node:v0.10.0-dev1
LABEL mantainer="Vincenzo Palazzo vincenzopalazzodev@gmail.com"

RUN lightningd --disable-plugin=/opt/lightning-rest/lightning-rest-gen.sh \
    --network=bitcoin  \
    --alias=clihgnting4j-node \
    --disable-plugin=bcli \
    --log-level=debug \
    --daemon

COPY ./docker/test-entrypoint.sh .

CMD ["./test-entrypoint.sh"]
