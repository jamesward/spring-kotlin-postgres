package kotlinbars

import org.springframework.stereotype.Component
import javax.annotation.PostConstruct


@Component
class TestDb(val initDB: InitDB) {
    @PostConstruct
    fun init() {
        initDB.run("init")
    }
}
