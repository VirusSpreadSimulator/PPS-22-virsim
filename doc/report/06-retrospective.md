## Retrospettiva

In questo capitolo si prenderanno in considerazione tutti gli episodi di interesse che sono emersi e i principali aspetti positivi e negativi incontrati durante il progetto.

### Processo di sviluppo

Lo sviluppo del software è stato gestito attraverso la versione del framework **Scrum** descritta nel capitolo del processo di sviluppo. Esso ci ha concesso di sperimentare i vari ruoli presenti all'interno di un team **Scrum** e questo ha permesso di considerare diversi punti di vista che ci hanno aiutato nella comprensione del dominio e nella risoluzione delle problematiche.

I diversi incontri previsti da **Scrum** sono risultati molto utili al fine di definire in modo preciso i compiti da svolgere e per mantenere sempre sotto controllo lo stato di avanzamento dello sviluppo. Le riunioni a fine sprint ci hanno permesso di capire il giusto carico di lavoro che mantenesse allo stesso tempo una buona produttività e la giusta motivazione nel team. Inoltre, si è sempre cercato di avere sprint significativi cioè che portassero ad un risultato tangibile per il committente.

Infine, l'utilizzo di tutta la documentazione prodotta, e mantenuta in versione, ed in particolare della Sprint task board è risultata particolarmente utile per l'organizzazione del lavoro e per avere una panoramica sullo stato di avanzamento dello Sprint corrente.

Tutta la documentazione relativa a:

- Product Backlog
- Sprint Goal
- Sprint Backlog
- Screen della Sprint Taskboard svolta su Trello
- Product Backlog Refinement
- Sprint Review
- Sprint Retrospective

è stata prodotta e aggiornata per ogni sprint, seguendo i principi del framework **Scrum**, e mantenuta in versione nella repository del progetto sotto forma di file Markdown. In particolare si trova nella directory `doc/process/`.

### Andamento dello sviluppo

Durante lo sviluppo dell'applicazione sono emersi i seguenti problemi:

- *Integrazione del progetto cross-plaftorm e IDE Intellij IDEA*: durante lo sviluppo è emerso che l'IDE utilizzato, Intellij IDEA, non supporta adeguatamente progetti cross-plaftform in cui vi è la generazione di moduli con dipendenze reciproche, come appunto svolto dall'insieme di plugin utilizzati di **sbt-crossproject**. A conferma di ciò sono presenti i seguenti post: [issue](https://youtrack.jetbrains.com/issue/SCL-18334/sbt-crossproject-shared-sources-do-not-see-jvm-js-sources?_gl=1*15jal5d*_ga*MzYyMTY0MjY3LjE2NTgyOTk3MjU.*_ga_9J976DJZ68*MTY1OTA4OTg0NS40LjEuMTY1OTA5MDY2Ni42MA..&_ga=2.79344852.1915785814.1659015060-362164267.1658299725), [discussione sul forum di Jet Brains](https://intellij-support.jetbrains.com/hc/en-us/community/posts/206633785-scala-js-IDEA-does-not-find-individual-JS-and-JVM-implementations-of-an-object-sbt-compile-sbt-test-works-fine).

  Al fine di consentire ugualmente il raggiungimento dell'obiettivo, anche se con qualche rinuncia e con un aumento della viscosità dell'ambiente di lavoro, abbiamo adottato i seguenti workaround:

  - Disabilitare il server di compilazione Scala: inizialmente ha permesso di risolvere il problema però con una rinuncia quasi completa ai suggerimenti dell'IDE. Questo workaround può essere compiuto seguendo i seguenti passi: *Setttings -> Compiler -> Scala Compiler Server -> Rimuovere flag "Use compile server"*
  - Forzare Intellij IDEA a produrre i .class in target/classes: successivamente, dopo un incrontro con il prof. Aguzzi, abbiamo adottato il seguente workaround che ci ha permesso di ri-ottenere parte dei suggerimenti dell'IDE. I passi sono: *ProjectSettings -> Modules -> Si crea un nuovo module posizionato nella root del progetto cross platform e con il nome "shared" impostando tutte le versioni in modo corretto (Scala: 3.1.1, SBT: 1.6.2)*.

- *Utilizzo di Prolog all'interno del progetto cross-platform*: inizialmente la volontà era quella di sviluppare una parte delle logiche dell'engine sfruttando i vantaggi offerti dal paradigma logico. Dopo lo sviluppo della logica di movimento però, ci siamo resi conto che la libreria utilizzata durante il corso per l'utilizzo di Prolog da Scala era integrabile con JS e quindi con ScalaJS con uno sforzo che andava ben oltre il monte ore. Stesso discorso per le alternative trovate. Per questo motivo la scelta finale è stata quella di mantenere tutte le logiche in Scala, ma allo stesso tempo mantenere in un package separato il lavoro svolto dal componente del team Giacomo Accursi.

Nonostante ciò, lo sviluppo e le strategie adottate hanno permesso di ottenere i seguenti aspetti positivi:

- *Automazione offerta dal workflow di Continuous Integration*: la scelta di dedicare uno sforzo iniziale importante nell'impostare i workflow di Continuous Integration ha concesso di portare avanti agilmente tutto il processo di sviluppo. L'analisi statica del codice ci ha permesso di risolvere code smells ed evitare debito tecnico con più facilità. I test eseguiti in automatico hanno evitato notevoli errori di regressione. Infine, tutto il processo di deploy automatico che comprende: release, documentazione e deploy della WebApp del simulatore, ha velocizzato notevolmente la release del prodotto e il suo aggiornamento.
- *Utilizzo di Scrum come framework di riferimento*: come anticipato nella sezione precedente *Scrum* ci ha permesso di monitorare costantemente l'andamento di sviluppo e di sincronizzarci in maniera più agile.
- *Flessibilià del design*: il design prodotto ci ha permesso di effettuare cambiamenti ed adattamenti in itinere agilmente e senza introdurre troppo debito tecnico.

La documentazione riguardante l'andamento dello sviluppo è mantenuta in versione all'interno della repository nella directory `doc/process/`.

### Commenti finali



<div style="page-break-after: always;"></div>
