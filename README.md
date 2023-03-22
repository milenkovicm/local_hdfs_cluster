# Local HDFS mock

## References 

- https://github.com/ooraini/testcontainers-hdfs/blob/main/docker/src/main/java/omaraloraini/testcontainers/hdfs/Main.java
- https://hub.docker.com/r/omaraloraini/testcontainers-hdfs
- https://github.com/kohsuke/hadoop/blob/master/src/test/org/apache/hadoop/hdfs/MiniDFSCluster.java
- https://mvnrepository.com/artifact/org.apache.hadoop/hadoop-minicluster jar

## Building

```bash
mvn compile jib:dockerBuild
```

## Running Docker

```bash
docker run -ti -p 9000:9000 -p 8020:8020 -p 50010:50010 -p 50011:50011 -p 50012:50012 -p 50013:50013 -p 50014:50014 --rm milenkovicm/testcontainer-hdfs
```