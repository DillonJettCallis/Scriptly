import com.redgear.scriptly.config.Config
import com.redgear.scriptly.lang.ScalaLang
import com.redgear.scriptly.repo.Repository
import com.redgear.scriptly.repo.impl.AetherRepo
import groovy.transform.CompileStatic
import org.junit.jupiter.api.Test

@CompileStatic
class ScalaLangTest {

  @Test
  void basicTest() {
    Repository repo = new AetherRepo(new Config())

    def lang = new ScalaLang()

    lang.exec(new File("C:\\Users\\Dillon\\Projects\\Scriptly\\src\\test\\resources\\test.scala"), repo, ['Hello', 'World'])
  }

}
