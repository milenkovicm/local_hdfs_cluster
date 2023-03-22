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
    val bindAddress = "0.0.0.0"
    val dataFile = File(hdfsData)
    val configPath = Paths.get(hdfsConfig, "core-site.xml")

    val conf = Configuration()
    conf["dfs.permissions.enabled"]             = "false"
    conf["dfs.namenode.rpc-bind-host"]          = bindAddress // https://hadoop.apache.org/docs/r3.1.0/hadoop-project-dist/hadoop-hdfs/HdfsMultihoming.html
    conf["dfs.namenode.servicerpc-bind-host"]   = bindAddress
    conf["dfs.namenode.http-bind-host"]         = bindAddress
    conf["dfs.namenode.https-bind-host"]        = bindAddress
    //conf["dfs.client.use.datanode.hostname"]    = "true"
    conf["dfs.client.read.shortcircuit"]        = "false"

    // control ports for data node
    val confDN0 = Configuration()
    confDN0["dfs.datanode.address"]                = "${bindAddress}:50010"
    confDN0["dfs.datanode.ipc.address"]            = "${bindAddress}:50011"
    confDN0["dfs.datanode.http.address"]           = "${bindAddress}:50012"
    confDN0["dfs.datanode.https.address"]          = "${bindAddress}:50013"
    confDN0["dfs.client.read.shortcircuit"]           = "false"

    val cluster = MiniDFSCluster.Builder(conf, dataFile)
        .clusterId("Testcontainer HDFS")
        .skipFsyncForTesting(true)
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