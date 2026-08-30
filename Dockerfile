FROM eclipse-temurin:25-jdk AS build
RUN apt-get update \
    && apt-get install -y --no-install-recommends curl rlwrap ca-certificates \
    && rm -rf /var/lib/apt/lists/*
RUN curl -fsSL https://download.clojure.org/install/linux-install-1.12.4.1618.sh -o /tmp/install-clojure.sh \
    && chmod +x /tmp/install-clojure.sh \
    && /tmp/install-clojure.sh \
    && rm /tmp/install-clojure.sh
WORKDIR /app
COPY deps.edn ./
RUN clojure -P
COPY src ./src
COPY test ./test
RUN clojure -M:test

FROM eclipse-temurin:25-jdk
RUN apt-get update \
    && apt-get install -y --no-install-recommends curl rlwrap ca-certificates \
    && rm -rf /var/lib/apt/lists/*
RUN curl -fsSL https://download.clojure.org/install/linux-install-1.12.4.1618.sh -o /tmp/install-clojure.sh \
    && chmod +x /tmp/install-clojure.sh \
    && /tmp/install-clojure.sh \
    && rm /tmp/install-clojure.sh
WORKDIR /app
COPY deps.edn ./
RUN clojure -P
COPY src ./src
ENTRYPOINT ["clojure", "-M", "-m", "stakeholder.core"]
