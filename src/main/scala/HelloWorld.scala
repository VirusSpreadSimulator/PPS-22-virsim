object HelloWorld:
  @main def hello(): Unit =
    print("ciao")

  /** Get the project name
    * @return
    *   the project name
    */
  def getProjectName: String = "virsim"
