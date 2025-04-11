# To build a Dockerized version execute: `docker build -t web3app .`
FROM ubuntu as Build
COPY . /root/app
RUN apt-get update && apt-get install -y \
	curl bash openjdk-21-jre \
	&& rm -rf /var/cache/apk/* \
	&& curl -L get.web3j.io | sh \
	&& /root/.web3j/web3j \
	&& cd /root/app \
	&& ./gradlew generateContractWrappers

FROM alpine
COPY --from=Build /root /root/
RUN apk add --no-cache bash openjdk21-jre\
	&& rm -rf /var/cache/apk/* \
	&& mkdir /opt/app
WORKDIR /root/app
ENTRYPOINT /root/.web3j/web3j run ${WEB3J_NODE_URL} ${WEB3J_WALLET_PATH} ${WEB3J_WALLET_PASSWORD}
