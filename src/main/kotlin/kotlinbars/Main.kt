package kotlinbars

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.core.io.Resource
import org.springframework.data.annotation.Id
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.nativex.hint.TypeHint
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.await
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.net.URI
import java.util.Properties


data class Bar(@Id val id: Long?, val name: String)

interface BarRepo : CoroutineCrudRepository<Bar, Long>

// note: bug workarounds (ie typehints missing in Spring)
@TypeHint(typeNames = [
    "org.springframework.boot.sql.init.dependency.DatabaseInitializationDependencyConfigurer\$DependsOnDatabaseInitializationPostProcessor",
    "org.springframework.boot.context.properties.ConfigurationPropertiesBinder\$Factory",
])
@SpringBootApplication
@RestController
class WebApp(val barRepo: BarRepo) {

    @GetMapping("/bars")
    suspend fun getBars(): Flow<Bar> {
        return barRepo.findAll()
    }

    @PostMapping("/bars")
    suspend fun addBar(@RequestBody bar: Bar): ResponseEntity<Unit> {
        barRepo.save(bar)
        return ResponseEntity(HttpStatus.NO_CONTENT)
    }

}

@Component
class InitDB(val databaseClient: DatabaseClient, @Value("classpath:init.sql") val initSql: Resource) : CommandLineRunner {

    private val logger = LoggerFactory.getLogger(InitDB::class.java)

    override fun run(vararg args: String?) {
        if (args.contains("init")) {
            logger.info("Init DB Schema")
            val lines = initSql.inputStream.bufferedReader().use { it.readText() }
            runBlocking {
                databaseClient.sql(lines).await()
            }
        }
    }

}

fun main(args: Array<String>) {
    val props = Properties()

    System.getenv()["DATABASE_URL"]?.let {
        val dbUri = URI(it)
        props["spring.r2dbc.url"] = "r2dbc:postgresql://" + dbUri.host + dbUri.path
        props["spring.r2dbc.username"] = dbUri.userInfo.split(":")[0]
        props["spring.r2dbc.password"] = dbUri.userInfo.split(":")[1]
    }

    runApplication<WebApp>(*args) {
        setDefaultProperties(props)
    }
}
