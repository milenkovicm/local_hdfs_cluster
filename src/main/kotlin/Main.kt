import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.hdfs.MiniDFSCluster
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption

const val READY_MESSAGE = "testcontainers.hdfs.status.READY"

// FIXME: we need java 11 for this project

fun main(args: Array<String>) {

    val hdfsData = "./HDFS/data"
    val hdfsConfig = "./HDFS/config"

    val dataFile = File(hdfsData)
    val configPath = Paths.get(hdfsConfig, "core-site.xml")

    val conf = Configuration()
    conf["dfs.permissions.enabled"]             = "false"

    // https://hadoop.apache.org/docs/r3.1.0/hadoop-project-dist/hadoop-hdfs/HdfsMultihoming.html
    conf["dfs.namenode.rpc-bind-host"]          = "0.0.0.0"
    conf["dfs.namenode.servicerpc-bind-host"]   = "0.0.0.0"
    conf["dfs.namenode.http-bind-host"]         = "0.0.0.0"
    conf["dfs.namenode.https-bind-host"]        = "0.0.0.0"
    conf["dfs.client.use.datanode.hostname"]    = "true"

    // control ports for data node
    val confDN0 = Configuration()
    confDN0["dfs.datanode.address"]                = "0.0.0.0:50010"
    confDN0["dfs.datanode.ipc.address"]            = "0.0.0.0:50011"
    confDN0["dfs.datanode.http.address"]           = "0.0.0.0:50012"
    confDN0["dfs.datanode.https.address"]          = "0.0.0.0:50013"

//    val confDN1 = Configuration()
//    confDN1["dfs.datanode.address"]                = "0.0.0.0:50020"
//    confDN1["dfs.datanode.ipc.address"]            = "0.0.0.0:50021"
//    confDN1["dfs.datanode.http.address"]           = "0.0.0.0:50022"
//    confDN1["dfs.datanode.https.address"]          = "0.0.0.0:50023"

    val cluster = MiniDFSCluster.Builder(conf, dataFile)
        .clusterId("Testcontainer HDFS")
        .nameNodeHttpPort(8020)
        .numDataNodes(1)
        .nameNodePort(9000)
        .checkDataNodeAddrConfig(true) // required if we want to change the name node address and port
        .checkDataNodeHostConfig(true) // required if we want to change the name node address and port
        .dataNodeConfOverlays(arrayOf(confDN0))
        .manageNameDfsDirs(true)
        .manageDataDfsDirs(true)
        .format(true)
        .build()

    Runtime.getRuntime().addShutdownHook(Thread { cluster.shutdown() })

    cluster.waitActive()

    Files.newOutputStream(
        configPath,
        StandardOpenOption.TRUNCATE_EXISTING,
        StandardOpenOption.CREATE
    ).use { outputStream -> cluster.getConfiguration(0).writeXml(outputStream) }

    println("Name Node  :  ${cluster.nameNode.hostAndPort}")
    println("Data Nodes : ${cluster.dataNodes}")
    println(READY_MESSAGE)

}