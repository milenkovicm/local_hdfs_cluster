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

## Running 

```bash
docker run -ti -p 9000:9000 -p 8020:8020 -p 50010-50015 milenkovicm/testcontainer-hdfs
```