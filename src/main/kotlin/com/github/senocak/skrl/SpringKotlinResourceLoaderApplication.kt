package com.github.senocak.skrl

import com.github.senocak.skrl.DatabaseResource.Companion.PROTOCOL_PREFIX
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.ResourceLoaderAware
import org.springframework.core.io.AbstractResource
import org.springframework.core.io.DefaultResourceLoader
import org.springframework.core.io.ProtocolResolver
import org.springframework.core.io.Resource
import org.springframework.core.io.ResourceLoader
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.simple.JdbcClient
import org.springframework.jdbc.core.support.SqlBinaryValue
import org.springframework.jdbc.datasource.DataSourceTransactionManager
import org.springframework.stereotype.Component
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.support.TransactionTemplate
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import java.io.InputStream
import java.sql.ResultSet
import java.sql.Types
import javax.sql.DataSource

fun main(args: Array<String>) {
    runApplication<SpringKotlinResourceLoaderApplication>(*args)
}

@SpringBootApplication
@RestController
class SpringKotlinResourceLoaderApplication(
//    @Value("postgresql://cat.jpg") private val resource: Resource
){
//    @GetMapping("/cat")
//    fun cat(): Resource = resource
//
//    @GetMapping("/isCat")
//    fun isCat(): Boolean = resource.exists()
}

@RestController
class SpringBootApplicationController(
    @Value("postgresql://cat.jpg") private val resource: Resource,
    val dataSource: DataSource,
    private val jdbcClient: JdbcClient,
) {
    private val log: Logger = LoggerFactory.getLogger(javaClass)

    @GetMapping("/cat")
    fun cat(): Resource = resource

    @GetMapping("/isCat")
    fun isCat(): Boolean = resource.exists()

    @PostMapping("/cat")
    fun createCat(): Boolean =
        try {
            val transactionManager: PlatformTransactionManager = DataSourceTransactionManager(dataSource)
            val transactionTemplate = TransactionTemplate(transactionManager)
            transactionTemplate.executeWithoutResult {
                SpringKotlinResourceLoaderApplication::class.java.getClassLoader().getResourceAsStream("cat.jpg")
                    .use { inputStream: InputStream? ->
                        if (inputStream == null)
                            throw RuntimeException("Resource 'cat.jpg' not found");
                        jdbcClient.sql("INSERT INTO spring_resource (file_name, file_data) VALUES (:name, :data)")
                            .param("name","cat.jpg", Types.VARCHAR)
                            .param("data", SqlBinaryValue(inputStream, inputStream.available().toLong()), Types.BINARY)
                            .update()
                    }
            }
            true
        } catch (e: Exception) {
            log.error("Error while uploading file: ${e.message}")
            false
        }
}

@Component
internal class DatabaseResourceResolver(
    private val jdbcClient: JdbcClient,
    @Value("\${databaseResource.table:spring_resource}") val table: String,
    @Value("\${databaseResource.blobColumn:file_data}") val blobColumn: String,
    @Value("\${databaseResource.fileNameColumn:file_name}") val fileNameColumn: String
): ResourceLoaderAware, ProtocolResolver {
    override fun resolve(location: String, resourceLoader: ResourceLoader): Resource? {
        if (location.startsWith(prefix = PROTOCOL_PREFIX)) {
            val fileName: String = location.substring(startIndex = PROTOCOL_PREFIX.length)
            return DatabaseResource(jdbcClient = jdbcClient, table = table, blobColumn = blobColumn,
                fileName = fileName, fileNameColumn = fileNameColumn)
        }
        return null
    }
    override fun setResourceLoader(resourceLoader: ResourceLoader) {
        if (DefaultResourceLoader::class.java.isAssignableFrom(resourceLoader.javaClass)) {
            (resourceLoader as DefaultResourceLoader).addProtocolResolver(this)
        }
    }
}

class DatabaseResource(
    private val jdbcClient: JdbcClient,
    private val table: String,
    private val blobColumn: String,
    private val fileNameColumn: String,
    private val fileName: String
) : AbstractResource() {
    override fun getInputStream(): InputStream = content.inputStream()
    override fun contentLength(): Long = content.size.toLong()
    override fun exists(): Boolean = single { rs: ResultSet, _: Int -> rs.next() }
    override fun getDescription(): String = "{$PROTOCOL_PREFIX}$table/$fileName"
    override fun getFilename(): String = fileName

    private val content: ByteArray by lazy {
        single { rs: ResultSet, _: Int -> rs.getBinaryStream(blobColumn).readAllBytes() }
    }
    private val query: String = "select $blobColumn from $table where $fileNameColumn = '$fileName'"
    private fun <T> single(rowMapper: RowMapper<T>): T = jdbcClient.sql(query).query(rowMapper).single()

    companion object {
        const val PROTOCOL_PREFIX: String = "postgresql://"
    }
}
