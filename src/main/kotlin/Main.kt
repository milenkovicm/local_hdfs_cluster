import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.hdfs.MiniDFSCluster
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption

const val READY_MESSAGE = "testcontainers.hdfs-ready"

fun main(args: Array<String>) {

    val hdfsData = "./HDFS/data"
    val hdfsConfig = "./HDFS/config"

    val dataFile = File(hdfsData)
    val configPath = Paths.get(hdfsConfig, "core-site.xml")

    val conf = Configuration()
    conf["dfs.permissions.enabled"] = "false"

    val cluster = MiniDFSCluster.Builder(conf, dataFile)
        .nameNodeHttpPort(8080)
        .numDataNodes(1)
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