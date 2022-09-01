## Implementazione

In questo capitolo verranno descritte le scelte più rilevanti che non sono state descritte nel precedente capitolo di design di dettaglio e che riguardano l'implementazione dei concetti. 

La documentazione del codice si può trovare nella [Scaladoc](https://virusspreadsimulator.github.io/PPS-22-virsim/latest/api/) presente nel sito del simulatore.

### Obiettivi

Come per il design, prima di procedere all'implementazione abbiamo deciso, a livello di team, una serie di obiettivi e di principi da seguire durante l'implementazione. Essi estendono quelli descritti per il design di dettaglio.

Innanzitutto abbiamo considerato, come anticipato, un approccio in cui si favorisce l'immutabilità evitando side-effects e l'uso di eccezioni, incapsulandoli ove necessario. Inoltre, durante l'implementazione si è cercato di rispettare sempre i principi, tra cui: DIP, ISP, LSP, SRP, oltre che a DRY e KISS. Quest'ultimi due anche grazie all'ausilio del tool SonarCloud descritto nel processo di sviluppo.

Di seguito alcuni accorgimenti adottati nell'implementazione.

Nell'utilizzo di **case class** abbiamo sfruttato le **lens** (attraverso la libreria [Monocle](https://www.optics.dev/Monocle/)) per accedere e trasformare i dati immutabili in modo più agevole e soprattutto in modo maggiormente dichiarativo.

Abbiamo preferito l'utilizzo di **Factory** attraverso il metodo `apply` nel companion object dei trait invece che esporre direttamente le implementazioni al fine di dare una modellazione più astratta del concetto, che sia indipendente dalla specifica implementazione, consentendo allo stesso tempo di modificare dettagli implementativi in modo più agevole.

Tutto ciò cercando di utilizzare le proprietà, le funzionalità e i costrutti messi a disposizione da **Scala 3**, come ad esempio: uniform access, extension methods, currying, mixins, given instances, type, function type ...

### Boundary

Considerando la necessità di eseguire il rendering della simulazione, sono necessari dei pannelli che contengano le informazioni di simulazione.
Al fine di evitare di modellare gli stessi concetti sia per il boundary JVM che per il boundary JS, si è deciso di creare il concetto di Pannello a livello shared del Boundary.
**BasePanel** modella il concetto di pannello di base che può essere inizializzato e stoppato. Esso viene esteso da **UpdatablePanel** il quale permette di aggiornare il pannello con il nuovo stato dell'*Environment*. Infine, è stato modellata anche la capacità di emettere degli eventi attraverso l'**EventablePanel** che sfrutta il pattern **self-type** in quanto rappresenta un'estensione, una capacità, che può essere aggiunta ad un qualsiasi pannello.

Di seguito verranno descritte le scelte implementative più rilevanti prese nello sviluppo dei tre boundary.

#### JVM

Come anticipato nel design di dettaglio *Java Swing* è fortemente object-oriented e segue un approccio a side-effects. 
Al fine di gestire il disegno dei concetti di Simulazione è stata utilizzata la type-class *Drawable* descritta precedentemente. È stata creata l'istanza per JVM chiamata **DrawableSwing** in cui viene specificato il tipo di grafica utilizzato, *Graphics2D*, ed è stato eseguito un "pimping" specificando l'operazione aggiuntiva che consente di poter disegnare attraverso un'unica chiamata un *set* di *DrawableSwing*. 

I concetti estesi con la capacità di disegno sono: *Environment*, *SimulationEntity* e *SimulationStructure*. Ciò ha permesso di isolare il codice necessario per eseguire il disegno degli elementi e quindi di isolare l'approccio a side-effects tipico dell'API di *Graphics2D*.

Il Boundary JVM è in particolare un **ConfigBoundary** in quanto è il boundary principale dell'applicativo Desktop dedicato alla visualizzazione dell'interfaccia grafica del simulatore. Esso è composto da due frame principali:

- *InitGUI*: è il primo frame che viene visualizzato ed è dedicato al caricamento della configurazione e alla visualizzazione degli eventuali errori associati.
- *SimulationGUI*: è il frame che visualizza la simulazione in corso.

La struttura di entrambi i frame è stata sviluppata attraverso una descrizione monadica lazy sfruttando i **Task** (di cui è stato creato un "alias", `io`, per una migliore leggibilità del codice lato gui) offerti dalla libreria *Monix*. In questo modo essi si integrano molto facilmente all'interno di tutto il sistema monadico predisposto e descritto precedentemente. Inoltre, il frame *SimulationGUI*, data la sua complessità, è stato suddiviso in diversi pannelli che estendono i trait **BasePanel**, **UpdatablePanel** ed **EventablePanel** descritti in precedenza. 

Il Boundary necessita di esporre verso l'esterno gli eventi generati da esso. Questo è stato realizzato andando ad eseguire il *merging* di tutte le sorgenti di eventi presenti: **EventablePanels** ed **EventSource**, attraverso l'API di **Observable** offerta dalla libreria *Monix*.

#### JS

Al fine di gestire il disegno dei concetti di Simulazione è stata utilizzata anche qui la type-class *Drawable* descritta precedentemente. È stata creata l'istanza per JS chiamata **DrawableJS** in cui viene specificato il tipo di grafica utilizzato, *CanvasRenderingContext2D*, e, similmente a prima, è stato eseguito un "pimping" specificando l'operazione aggiuntiva che consente di poter disegnare attraverso un'unica chiamata un *set* di *DrawableJS*. 

I concetti estesi con la capacità di disegno sono: *Environment*, *SimulationEntity* e *SimulationStructure*. Ciò ha permesso di isolare il codice necessario per eseguire il disegno degli elementi e quindi di isolare l'approccio a side-effects tipico dell'API di *CanvasRenderingContext2D*.

Anche il Boundary JS è un **ConfigBoundary** in quanto è il boundary principale della WebApp ed è anch'esso dedicato alla visualizzazione dell'interfaccia grafica del simulatore. Esso è composto da una sola schermata la quale è stata sviluppata attraverso una descrizione monadica lazy sfruttando i **Task** offerti dalla libreria *Monix*. In questo modo essi si integrano molto facilmente all'interno di tutto il sistema monadico predisposto e descritto precedentemente. Similmente a quanto accade per *SimulationGUI* del Boundary JVM, data la sua complessità, la schermata è stato suddivisa in diversi pannelli che estendono i trait **BasePanel**, **UpdatablePanel** ed **EventablePanel** descritti in precedenza. 

Il Boundary necessita di esporre verso l'esterno gli eventi generati da esso. Questo è stato realizzato andando ad eseguire il *merging* di tutte le sorgenti di eventi presenti: **EventablePanels** ed **EventSource**, attraverso l'API di **Observable** offerta dalla libreria *Monix*.

#### Exporter

Per quanto riguarda il metodo per esportare dati ad ogni step della simulazione esso utilizza tecniche di **folding** sugli estrattori di dati presenti.

Inoltre gli estrattori di dati sono stati implementati attraverso *case class* immutabili le quali hanno il solo compito di implementare il metodo generico *extractData()* a seconda del tipo di dato che si intende estrarre.

Le tipologie di estrattori presenti sono:

- **Time**: si occupa di estrare l'orario corrente della simulazione.

- **Days**: si occupa di estrarre il numero di giorni trascorsi.
- **Hours**: si occupa di estrarre le ore trascorse nel giorno attuale.
- **Minutes**: si occupa di estrarre i minuti trascorsi nell'ora attuale.

- **Alive**: si occupa di estrarre il numero di entità in vita nell'environment.
- **Deaths**: si occupa di estrarre il numero di entità morte nell'environment.
- **Sick**: si occupa di estrarre il numero di entità malate nell'environment.
- **Infected**: si occupa di estrarre il numero di entità infette nell'environment.
- **Healthy**: si occupa di estrarre il numero di entità in salute nell'environment.
- **AtHome**: si occupa di estrarre il numero di entità che si trovano in casa.
- **HospitalsCapacity**: si occupa di estrarre la capacità degli ospedali presenti.
- **HospitalFreeSeats**: si occupa di estrarre il totale dei posti liberi negli ospedali presenti.
- **Hospitalized**: si occupa di estrarre il numero di entità ricoverate negli ospedali.
- **HospitalPressure**: si occupa di estrarre la pressione ospedaliera ossia il rapporto tra gli ospedalizzati ed i posti liberi rimanenti.

### Parser

Al Parser vengono aggiunti tramite **extension methods** i metodi *shouldBeWithin* e *andIfNot*.
In questo modo per ciascun parametro è possibile controllare se rientra nel range di valori possibili e in caso negativo generare un errore che verrà comunicato ai Boundary.

Per quanto riguarda invece il *YAMLParser*, esso è arrichito dai metodi *to* e *has* per semplificare le operazioni di look-up e di conversione sulla mappa restituita dal parsing del file YAML.

### Reader

Il Reader di JavaScript utilizza un *PublishSubject* di *Monix* per leggere il file caricato dall'utente sul broswer. In questo modo è possibile implementare il metodo utilizzando un approccio ad eventi ed associare ad esso un Task di Monix per rimanere coerenti con il resto del progetto.

### Engine

Per implementare il flow dell'engine descritto nel capitolo del design di dettaglio, il quale prevede la necessità di raccogliere gli eventi dai boundary e di eseguire il loop di simulazione aggiornando i boundary dopo aver computato il nuovo Environment, è stato necessario prevedere una fase di setup dell'engine in cui eseguire il *dispatch* del carico di lavoro.

In particolare, il task ritornato dal metodo `startSimulationLoop` è composto dalla descrizione monadica lazy di due task che nel momento dell'esecuzione verranno eseguiti in parallelo attraverso lo scheduler di *Monix* definito:

- Il primo task si occupa di gestire ogni evento proveniente dagli *Observable* restituiti dai Boundary, opportunamente mergiati, inserendoli in una *ConcurrentQueue* di *Monix*.
- Il secondo task esegue il vero loop di simulazione, eseguendo le logiche e spedendo l'Environment aggiornato ai Boundary. Esso considera solo i primi eventi dalla suddetta coda (il cui numero è definito dal valore associato alla configurazione *maxEventPerIteration* iniettata nell'engine).

Nella descrizione del design di dettaglio sono presenti due tipologie di logiche: **UpdateLogic** per le logiche di aggiornamento dell'Environment da eseguire ad ogni tick ed **EventLogic** per le logiche associate agli eventi. In particolare le logiche implementate sono le seguenti:

- *UpdateLogic*: esse sono state inserite nel seguente ordine al fine di eseguirle sequenzialmente
  - **UpdateEntityStateLogic**: è la logica che provvede ad aggiornare lo stato di salute di ogni entità.
  - **HospitalRecoveryLogic**: è la logica che si occupa di curare gli individui all'interno degli ospedali.
  - **HospitalizeEntityLogic**: è la logica che si occupa di ricoverare gli individui malati a rischio di vita.
  - **EntityGoalUpdateLogic**: è la logica che si occupa di aggiornare l'obiettivo di ogni entità sulla base del periodo della giornata simulata.
  - **MovementLogic**: è la logica che gestisce il movimento delle entità all'interno dell'Environment.
  - **ExitLogic**: è la logica che si occupa di controllare e gestire l'uscita degli individui dalle strutture.
  - **EntranceLogic**: è la logica che si occupa di controllare e gestire l'entrata degli individui nelle strutture.
  - **ExternalInfectionLogic**: è la logica che si occupa di gestire il contagio nell'Environment, esternamente alle strutture.
  - **InternalInfectionLogic**: è la logica che si occupa di gestire il contagio internamente alle strutture.
  - **LogicTimeUpdate**: è la logica che aggiorna il tempo virtuale della simulazione.
  - **IterationLogic**: è la logica che controlla la terminazione della simulazione.
- *EventLogic*: nella configurazione (*SimulationConfig*) ogni evento appartenente ad **Event** è associato alla corrispettiva logica. All'interno della configurazione la funzione `Event => EventLogic` è implementata attraverso l'utilizzo di una **Map**.
  - **PauseLogic**, **ResumeLogic**, **StopLogic**: rispettivamente associati agli eventi *Pause*, *Resume* e *Stop*, si occupano della gestione della volontà dell'utente di mettere in pausa, riprendere e stoppare preventivamente la simulazione.
  - **SimulationSpeedLogic**: associato all'evento *ChangeSpeed(speed)*, si occupa di gestire la volontà dell'utente di cambiare la velocità di simulazione.
  - **SwitchMaskObligationLogic**: associato all'evento *SwitchMaskObligation*, si occupa di gestire la volontà dell'utente di inserire/togliere l'obbligo di mascherina per tutte le entità dell'Environment.
  - **VaccineRound**: associato all'evento *VaccineRound(percentage)*, si occupa di gestire la volontà dell'utente di vaccinare una percentuale delle entità presenti nell'Environment.
  - **SwitchStructureLogic**: associato all'evento *SwitchStructure(group)*, si occupa di gestire la volontà dell'utente di aprire/chiudere uno specifico gruppo di strutture.

### Environment

#### Common

Come anticipato nel design di dettaglio, la **type class** *Probable* permette di aumentare un tipo con la capacità di rappresentare un evento dotato di una determinata probabilità di accadimento, la quale viene definita da una formula. Infatti è possibile notare che all'interno dell'*object ProbableGivenInstances* sono presenti tutti i *given instances* necessari. In particolare si possono osservare quelli associati agli eventi di contagio. Essi sono modellati andando ad aumentare i tipi *ExternalProbableInfection* e *InternalProbableInfection* che rappresentano un contagio probabile rispettivamente esterno ed interno alle strutture.

Le formule implementate, le quali sono semplici e non hanno alcuna volontà di essere significative in un contesto reale, sono le seguenti:

- *Contagio esterno* (individuo *j*):
  
  ![external_probable_infection_formula](imgs/external_infection.svg)
  
- *Contagio interno* (individuo *j*): 

  ![internal_probable_infection_formula](imgs/internal_infection.svg)
  

Considerando:

- I: infetti in prossimità dell'entità
- P0: spreadRate del virus
- PS: infectionProbability della struttura
- M: riduzione data dalla presenza della mascherina
- Mj: riduzione data dalla presenza o meno della mascherina per l'entità j
- Dji: distanza tra le entità j ed i
- Dmax: distanza massima di contagio del virus
- Ij: immunità dell'individuo j
- Imax: immunità massima
- NS: numero entità all'interno della struttura
- NSinf: numero entità infette nella struttura
- NSinfm: numero entità infette nella struttura con la mascherina

Grazie alla type class è stato possibile isolare la rappresentazione delle formule in un concetto che rispecchia il significato di evento probabile.

#### Entity

Al fine di poter confrontare due Entity anche in tick della simulazione differenti è stato ridefinito il metodo `equals`, definendo che due Entity sono uguali se hanno lo stesso id. 
Le Entity vengono create tramite una **Factory**, implementata utilizzando il metodo `apply` nel companion object, evitando quindi di esporne l'implementazione. 
Ad ogni Entity al momento della creazione viene attribuita un valore MaxHealth il quale rappresenta il massimo della vita che essa potrà avere per tutta la durata della simulazione. Questo valore dipende dall'età e siccome presumubilmente molte Entity avranno la stessa età, è stata utilizzata la libreria **Scalaz**, la quale fornisce una immutableHashMapMemo, per salvare le nuove maxHealth calcolate ed evitare di ricalcolarle successivamente. Il campo health assume maxhealth al momento della creazione. 

#### Structure

Le Strutture sono state effettivamente create partendo dal trait *SimulationStructure*, il quale definisce tutti gli **abstract types** dichiarati nel trait *Structure*, e componendolo con i trait rappresentanti le caratteristiche desiderate. Questo approccio rende flessibile e agile l'estensione e l'aggiunta di nuove strutture e l'aggiunta di nuove tipologie.
Seguendo i requisiti sono state implementate le seguenti strutture:

- *House*: rappresenta la casa per le entità
- *GenericBuilding*: rappresenta un edificio generico
- *Hospital*: rappresenta l'ospedale

### Test

Al fine di garantire la correttezza e la qualità del codice il team ha sfruttato il modello di sviluppo **TDD** (*Test Driven Development*) durante lo sviluppo di tutte le parti del sistema ad eccezione delle parti di interfaccia utente. Inoltre, allo scopo di prevenire situazioni di regressione, come anticipato nella descrizione del processo di sviluppo, i test vengono eseguiti come parte della pipeline di Build del workflow di Continuous Integration.

Abbiamo ottenuto una coverage di linea di circa 50% ed una coverage totale di circa 30%.

### Suddivisione del lavoro

// Dire che analisi, requisiti, ci e design arch fatto insieme

Durante gli Sprint Planning e in particolare durante la compilazione della Sprint task board i vari compiti da portare a termine sono stati suddivisi tra i componenti del team. Di seguito, per ciascun componente, verranno elencati i concetti progettati e sviluppati.

#### Acampora Andrea

#### Accursi Giacomo

#### Giulianelli Andrea

Nel progetto mi sono occupato del design e dello sviluppo di:

- 

<div style="page-break-after: always;"></div>
