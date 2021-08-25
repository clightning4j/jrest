FROM vincenzopalazzo/clightning4j-node:v0.10.0-dev1
LABEL mantainer="Vincenzo Palazzo vincenzopalazzodev@gmail.com"

COPY ./scripts/entrypoint.sh .
RUN chmod +x entrypoint.sh

CMD ["./entrypoint.sh"]