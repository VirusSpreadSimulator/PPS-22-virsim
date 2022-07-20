import org.scalatest.funsuite.AnyFunSuite

class TestName extends AnyFunSuite:
  test("The project name must be virsim") {
    assert(HelloWorld.getProjectName === "virsim")
  }
