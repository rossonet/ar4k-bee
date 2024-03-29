FROM ubuntu:20.04 as ar4k-builder
RUN apt update && DEBIAN_FRONTEND=noninteractive apt install -y openjdk-8-jdk
COPY . /ar4kAgent
WORKDIR /ar4kAgent
RUN chmod +x gradlew
RUN ./gradlew clean shadowJar

FROM ubuntu:20.04
RUN apt update && DEBIAN_FRONTEND=noninteractive apt install -y openjdk-8-jre wget net-tools isc-dhcp-server tftpd-hpa tftp bind9 dnsutils apache2 nmap tcpdump openssh-client curl && apt-get clean && rm -rf /var/lib/apt/lists/*
RUN curl -LO "https://storage.googleapis.com/kubernetes-release/release/$(curl -s https://storage.googleapis.com/kubernetes-release/release/stable.txt)/bin/linux/amd64/kubectl" \
    && chmod +x ./kubectl \
    && mv ./kubectl /usr/local/bin/kubectl \
    && curl -fsSL -o get_helm.sh https://raw.githubusercontent.com/helm/helm/master/scripts/get-helm-3 \
    && chmod +x get_helm.sh && ./get_helm.sh
RUN wget https://github.com/k3s-io/k3s/releases/download/v1.23.6%2Bk3s1/k3s -O /usr/local/bin/k3s && chmod +x /usr/local/bin/k3s
RUN wget https://github.com/rancher/rke/releases/download/v1.3.11/rke_linux-amd64 -O /usr/local/bin/rke && chmod +x /usr/local/bin/rke
RUN wget https://github.com/docker/compose/releases/download/v2.5.1/docker-compose-linux-x86_64 -O /usr/local/bin/docker-compose && chmod +x /usr/local/bin/docker-compose
ENTRYPOINT ["java"]
CMD ["-XX:+UnlockExperimentalVMOptions","-Djava.net.preferIPv4Stack=true","-XX:+UseCGroupMemoryLimitForHeap","-XshowSettings:vm","-Djava.security.egd=file:/dev/./urandom","-jar","/agent.jar"]
COPY --from=ar4k-builder /ar4kAgent/build/libs/*-all.jar /agent.jar
