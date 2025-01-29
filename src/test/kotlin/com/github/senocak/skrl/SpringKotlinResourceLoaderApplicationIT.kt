package com.github.senocak.skdps
/*
import com.github.dockerjava.api.command.CreateContainerCmd
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.support.DefaultSingletonBeanRegistry
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.ApplicationContext
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.test.context.ActiveProfiles
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.lang.Thread.sleep
import java.time.Duration
import kotlin.test.assertEquals

@Tag("integration-test")
@ActiveProfiles(value = ["integration-test"])
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestConfiguration
@Testcontainers(disabledWithoutDocker = true)
class SpringKotlinResourceLoaderApplicationIT {
    @Autowired private lateinit var databaseProperties: DatabaseProperties
    @Autowired private lateinit var propertyService: PropertyService
    @Autowired private lateinit var applicationContext: ApplicationContext
    private lateinit var configContext: ConfigurableApplicationContext
    private lateinit var registry: DefaultSingletonBeanRegistry

    @Container
    private var postgres: PostgreSQLContainer<*> = PostgreSQLContainer("postgres:14")
        .withDatabaseName("spring")
        .withUsername("postgres")
        .withPassword("senocak")
        .withStartupTimeout(Duration.ofMinutes(2))
        .withCreateContainerCmdModifier { cmd: CreateContainerCmd -> cmd.withName("SQL_CONTAINER") }

    init {
        postgres.start()
        // Following properties are set to be used in application.properties because of "spring.factories" injects the bean
        // before setting the properties in the test class
        System.setProperty("spring.datasource.url", postgres.jdbcUrl)
        System.setProperty("spring.datasource.username", postgres.username)
        System.setProperty("spring.datasource.password", postgres.password)
    }

    @BeforeEach
    fun beforeEach() {
        configContext = applicationContext as ConfigurableApplicationContext
        registry = configContext.beanFactory as DefaultSingletonBeanRegistry
    }

    @Test
    fun propertySourceTest() {
        var property1: String = propertyService.property1
        assertEquals(expected = "value1", actual = property1, message = "Property1 is not equal to 'initial value'")
        propertyService.updateProperty(key = "key1", value = "updated")
        sleep(databaseProperties.propertyRefreshInterval)
        registry.destroySingleton("propertyService")
        propertyService = applicationContext.getBean("propertyService") as PropertyService
        property1 = propertyService.property1
        assertEquals(expected = "updated", actual = property1, message = "Property1 is not equal to 'updated'")
    }
}


 */