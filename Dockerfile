FROM clojure:openjdk-17-tools-deps-1.11.1.1113-slim-buster

RUN mkdir -p /app
WORKDIR /app

EXPOSE 3000

### Handling deps separately allows layer caching to kick in
COPY deps.edn /app
RUN clojure -P

COPY . /app

CMD clojure -M -m ca.usefulprojects.main
