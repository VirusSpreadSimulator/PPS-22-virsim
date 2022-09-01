## Processo di sviluppo adottato

Lo sviluppo del software è stato gestito attraverso una metodologia di sviluppo agile seguendo il framework **Scrum**.
La scelta di questa metodologia e in particolare di questo framework ha permesso una forte collaborazione all'interno del team, con un aumento dell'efficienza e una maggiore motivazione nell'affrontare il progetto. Grazie a questa metodologia, il cliente è attivamente e continuamente coinvolto per tutto il ciclo di vita del software in modo tale da avere feedback frequenti che portino a una corretta implementazione delle feature customer-centric.
Al fine di simulare un contesto reale, i componenti del team interpretano i vari ruoli del contesto Scrum:

- **Committente**: Andrea Acampora
- **Product Owner**: Andrea Giulianelli
- **Scrum Master**: Giacomo Accursi

Considerando la cardinalità ridotta del team, si è adottata una versione semplificata di Scrum cercando di rimanere coerenti con la filosofia originale. Come anticipato, Andrea Acampora in veste di committente ed esperto del dominio si è occupato di garantire l'usabilità e la qualità del risultato interagendo direttamente con il Product Owner interpretato da Andrea Giulianelli.

### Meeting/interazioni pianificate

I meeting tra i membri del team si sono tenuti online con frequenza regolare sfruttando la piattaforma Microsoft Teams. Il meeting iniziale è stato caratterizzato dall'incontro tra il team, in particolare il Product Owner, e il committente con lo scopo di comprendere il dominio applicativo e gli obiettivi del progetto.
Durante questo incontro è stata eseguita un'intervista che ha portato alla stesura dei requisiti e alla creazione del **Product Backlog**. Quest'ultimo documento è stato fondamentale per mantenere una lista con priorità delle feature customer-centric accompagnando lo sviluppo per tutta la durata del progetto. A livello operativo il documento è stato gestito mediante una tabella in un file in linguaggio Markdown mantenuta in versione all'interno della repository del progetto in modo da permetterne l'evoluzione.
A seguito di questo incontro sono stati eseguiti Sprint con cadenza settimanale, in particolare ciascuno Sprint è stato caratterizzato dalle seguenti attività:

- **Sprint Planning**: cercando di rispettare la natura di Scrum, lo Sprint Planning è stato diviso in due parti. Nella prima parte, dedicata alla preparazione dello sprint, si delineano i principali task ad alta priorità da svolgere all'interno dello sprint stesso. Essi sono estratti dal Product Backlog e approfonditi. Il risultato di questa prima parte è lo **Sprint Goal**, un riassunto con gli obiettivi dello sprint mantenuto in versione in un file in linguaggio Markdown all'interno della repository del progetto. Lo Sprint Goal prodotto lascia flessibilità nella quantità e nella selezione degli elementi in preparazione della seconda parte dello Sprint Planning. Durante la seconda parte il team di sviluppo si occupa di decidere in modo effettivo, considerando i tempi a disposizione, quali item dovranno essere completati nello sprint corrente. Il risultato di questa fase è lo **Sprint Backlog**, un approfondimento della porzione di Product Backlog designata nella fase precedente. Anche questo documento, rappresentato da una tabella in un file in linguaggio Markdown, è mantenuto in versione. 
- **Daily Scrum**: durante lo sprint, con cadenza giornaliera e della durata di 15 minuti, sono stati eseguiti i Daily Scrum al fine di sincronizzare il lavoro e riportare eventuali ostacoli incontrati.
- **Product Backlog Refinement**: alla fine dello sprint viene eseguito il Product Backlog Refinement, un meeting con l'obiettivo di analizzare, ri-stimare e rivedere le priorità del Product Backlog al fine di semplificare i futuri Sprint Planning.
- **Sprint Review**: consiste in un meeting da svolgere alla fine dello sprint ed ha l'obiettivo di analizzarne e ispezionarne il risultato al fine di comprendere, con il Product Owner e il committente, se i task dello sprint sono stati svolti correttamente.
- **Sprint Retrospective**: quest'ultimo meeting ha lo scopo di comprendere gli aspetti positivi e negativi del processo adottato e dell'ambiente lavorativo, dando la possibilità di mettere in discussione dei cambiamenti da provare. Il suo contenuto è mantenuto in versione insieme alla documentazione dello sprint.

### Modalità di divisione in itinere dei task

Al termine di ogni Sprint Planning, partendo dallo Sprint Backlog, i task vengono rappresentati all'interno di una **Sprint Task Board**, con l'ausilio del software **Trello**. La divisione dei task è stata gestita mediante la Task Board, assegnando ciascun task a uno o più componenti del team.
All'interno della Task Board sono presenti molteplici liste, ciascuna delle quali rappresenta un determinato stato nell'evoluzione del task. Le tipologie di liste presenti sono: *TO-DO, Doing, Testing, Done, Waiting, Blocked*. In questo modo ogni componente del team può avere una panoramica completa sul lavoro svolto e in esecuzione.
Nella repository del progetto, nella sezione relativa alla documentazione di ciascuno sprint, è possibile osservare alcuni screenshot relativi all'evoluzione della Board di Trello.
A seconda della tipologia i task sono stati assegnati a un singolo membro del team, oppure sfruttando il *pair-programming*, mentre i task più importanti hanno coinvolto l'intero team.
Per ogni task assegnato è stata considerata la seguente *definition of done*: un task, o in particolare una funzionalità è da considerarsi terminata nel momento in cui è stata adeguatamente testata e documentata, ha passato una code review (automatica o manuale a seconda dell'importanza) e soddisfa le aspettative del committente.

### Modalità di revisione in itinere dei task

Durante lo sprint non è possibile effettuare cambiamenti agli obiettivi di esso. Ciascuna modifica, la quale viene discussa in uno dei meeting finali quali Product Backlog Refinement, Sprint Review e Sprint Retrospective, è da includere necessariamente all'interno dello sprint successivo.
L'unica revisione consentita è relativa alla modifica della stima del carico di lavoro residuo di ogni task all'interno dello Sprint Backlog. Invece, nel caso di cambi drammatici e/o critici è possibile interrompere lo Sprint in corso.

### Scelta degli strumenti di test 

L'intero processo di sviluppo è stato effettuato seguendo i principi del **Test-Driven Development** (TDD), un modello di sviluppo di software nel quale si porta avanti il codice di test insieme a quello di produzione, facendo sì che i test diventino una specifica di comportamento desiderato, utile per comprendere gli obiettivi e per documentare il codice.
A supporto di ciò sono stati utilizzati due testing framework: 

+ **ScalaTest**: sfruttando **AnyFunSuite** e i **Matchers** per una maggiore dichiaratività. Ulteriori dettagli inerenti ai test sono presenti nell'apposita sezione all'interno del capitolo Implementazione.
+ **WeaverTest**: un test framework interoperabile con monix sviluppato da **[Disney](https://github.com/disneystreaming/weaver-test)**.

### Scelta degli strumenti di build e Continuous Integration

Al fine di rendere il lavoro più efficiente, si è cercato di automatizzare il più possibile alcuni degli elementi più ripetitivi del processo di sviluppo attraverso *Build Automation* e *Continuous Integration*.
L'obiettivo è quello di sfruttare le principali tecniche DevOps al fine di ottenerne i vantaggi tra cui l'aumento di collaborazione, riproducibilità, incrementalità e robustezza del sistema.
Questi obiettivi sono stati perseguiti attraverso l'utilizzo di diverse pratiche qui di seguito descritte.

#### Workflow Organization

Al fine di gestire un corretto versioning del software è stato utilizzato **Git** come software per il controllo di versione distribuito.  Per permettere una collaborazione agile tra i vari componenti del team la repository è gestita attraverso il servizio di hosting **GitHub**.
Per una gestione efficace dei branch di Git è stato utilizzato il workflow **Git-Flow** utilizzandolo esclusivamente come branching model.
I branch previsti dal workflow sono i seguenti: 

- *main*: branch principale, contiene il codice associato alle varie release.
- *develop*: branch di sviluppo, contiene il codice di pre-produzione integrando le feature sviluppate.
- *feature*: branch di supporto, utilizzato per lo sviluppo di una specifica feature, la quale una volta ultimata verrà integrata nel branch develop.
- *release*: branch di supporto, dedicato alla preparazione di una nuova release.
- *hotfix*: branch di supporto, utilizzato per la correzione di errori nel codice di produzione rilasciato.

Inoltre, al fine di esplicitare maggiormente il significato dei commit si è scelto di utilizzare la specifica **[Conventional Commits](https://www.conventionalcommits.org/en/v1.0.0/)**, che ha semplificato l'utilizzo di tool automatici per il versionamento dell'applicazione. In particolare è stato adottata la specifica del **[Semantic Versioning](https://semver.org/)**.
Al fine di controllare il rispetto della specifica *Conventional Commits* è stato utilizzato un *hook* per git con lo scopo di analizzare il messaggio di commit ed eventualmente farlo fallire in caso di errore. Inoltre, è stato utilizzato un ulteriore hook pre-commit per eseguire automaticamente il pull dalla repository prima di poter effettuare qualsiasi commit in modo tale da limitare il più possibile la divergenza tra le linee di sviluppo. Per poter condividere e configurare automaticamente gli hook è stato utilizzato il plugin sbt **sbt-git-hooks** il quale al caricamento del progetto inserisce quest'ultimi all'interno della cartella *.git*.

#### Build Automation

Come strumento per la build automation è stato utilizzato **Scala-Build Tool** (Sbt), un build tool automator che ha permesso una gestione efficiente del progetto attraverso la gestione delle dipendenze e di vari plugins utili per l'analisi statica del codice e per la configurazione cross-platform del progetto. Inoltre, per una gestione più agile del progetto cross-platform (JVM e JS) sono stati utilizzati i plugin di [sbt-crossproject](https://github.com/portable-scala/sbt-crossproject).

#### Code Quality Control

Un elemento centrale dell'intero processo è stata la continua ricerca di qualità nel codice sviluppato. A supporto di ciò sono stati utilizzati diversi tool automatici per l'analisi statica del codice sia a livello di plugin per l'IDE che a livello di Continuous Integration:

- **Scalafmt**: tool per una corretta formattazione del codice.
- **Wartremover**: utilizzato in locale per l'analisi del codice Scala al fine di trovare potenziali problemi in esso.
- **Jacoco**: utilizzato per calcolare e controllare la coverage del codice rispetto ai test.
- **SonarCloud**: tool per la ricerca di code smells, bugs e vulnerabilità. Esso è utilizzato per automatizzare la code review e per visualizzare i report sulla coverage generati da Jacoco.

In generale, la code review è stata in parte automatizzata mediante il tool SonarCloud in modo da avere un controllo completo su tutto il codice e in parte eseguita attraverso meeting ad hoc per le features più importanti e per gli elementi core del sistema.

#### Continuous Integration

Per quanto riguarda lo strumento per la *Continuous Integration* è stata utilizzata la piattaforma **GitHub Actions**, la quale consente di automatizzare i flussi di lavoro dello sviluppo di software all'interno di GitHub. È possibile distribuire i flussi di lavoro nella stessa posizione in cui si archivia il codice per compilare, testare, assemblare, analizzare, rilasciare e distribuire software oltre ad automatizzare la collaborarazione tramite *issue* e *pull requests*.
L'obiettivo è quello di verificare continuamente l'integrità del codice eseguendo nuovamente tutti i test presenti per ciascuna modifica effettuata evitando così situazioni di regressione. Un altro utilizzo del workflow di Continuous Integration è quello di mandare in esecuzione i tool per l'analisi statica del codice.
Al fine di garantire un corretto utilizzo del software su piattaforme differenti i test verranno eseguiti su Windows, Linux e MacOs.
È stato sviluppato un workflow dedicato esclusivamente al processo di build che comprende: 

+ Check di scalafmt su tutto il codice per assicurare una corretta formattazione. 
+ Esecuzione dei test per evitare situazioni di regressione. 
+ Analisi statica del codice mediante Sonarcloud, a supporto del processo di code review. 

Inoltre, per la gestione automatica degli aggiornamenti delle dipendenze è stato utilizzato il tool **Renovate**, il quale si occupa di controllare la presenza di aggiornamenti nelle dipendenze, e prova ad eseguirli in un branch ad hoc che verrà poi integrato automaticamente nel branch di sviluppo qualora i test vengano eseguiti con successo.  

#### Continuous Deployment

In aggiunta alla Continuous Integration è stato utilizzato anche un meccanismo di Continuous Deployment, il quale permette di rilasciare le *major versions* del software in maniera automatica.
Il delivery target scelto è **Github Release**. 
Considerando, come anticipato, l'utilizzo di Git-Flow come branching model, ad ogni commit sul branch Main viene attivata la pipeline di deploy. Essa ha un duplice obiettivo: 

+ Release: al fine di ottenere una gestione automatica della versione la quale sfrutti i Conventional Commit e della pubblicazione degli artefatti è stato utilizzato il tool [Semantic Release](https://github.com/semantic-release/semantic-release). La release include:

  + Il file jar dell'applicativo JVM: generato attraverso il plugin **sbt-assembly**. 
  + Il file pdf del report, ottenuto dal merging automatico dei file in formato Markdown presenti in versione. Per la conversione dei file è stata sviluppata una [action](https://github.com/andrea-acampora/action-md2pdf) ad hoc che sfrutta il converter [mdpdf](https://github.com/BlueHatbRit/mdpdf).
  + I sample di configurazione della simulazione in formato scala e yaml. 

+ Deploy: è stato utilizzato il servizio di hosting Github Pages al fine di creare un sito che permetta di accedere a: 

  + Scaladoc: generata automaticamente mediante il plugin **sbt-unidoc** ed **sbt-site** e pubblicata al seguente [link](https://virusspreadsimulator.github.io/PPS-22-virsim/latest/api/).
  + Web App: il codice del simulatore viene transcompilato attraverso il plugin sbt-crossproject descritto precedentemente e ne viene eseguito il deploy all'interno del sito del simulatore al seguente [link](https://virusspreadsimulator.github.io/PPS-22-virsim/simulator/).

  Il deploy del sito avviene automaticamente al seguente [link](https://virusspreadsimulator.github.io/PPS-22-virsim/) grazie all'azione **[action-gh-pages](https://github.com/peaceiris/actions-gh-pages)** in modo tale da aggiornare la scaladoc e la webapp ad ogni release senza interventi umani. 

<div style="page-break-after: always;"></div>
