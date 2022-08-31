## Implementazione

In questo capitolo verranno descritte le scelte più rilevanti che non sono state descritte nel precedente capitolo di design di dettaglio e che riguardano l'implementazione dei concetti. 

La documentazione del codice si può trovare nella [Scaladoc](https://virusspreadsimulator.github.io/PPS-22-virsim/latest/api/) presente nel sito del simulatore.

### Obiettivi

Come per il design, prima di procedere all'implementazione abbiamo deciso, a livello di team, una serie di obiettivi e di principi da seguire durante l'implementazione. Essi estendono quelli descritti per il design di dettaglio.

Innanzitutto abbiamo considerato, come anticipato, un approccio in cui si favorisce l'immutabilità evitando side-effects e l'uso di eccezioni, incapsulandoli ove necessario. Inoltre, durante l'implementazione si è cercato di rispettare sempre i principi, tra cui: DIP, ISP, LSP, SRP, oltre che a DRY e KISS. Quest'ultimi due anche grazie all'ausilio del tool SonarCloud descritto nel processo di sviluppo.

Di seguito alcuni accorgimenti adottati nell'implementazione.

Nell'utilizzo di **case class** abbiamo sfruttato le **lens** (attraverso la libreria [Monocle](https://www.optics.dev/Monocle/)) per accedere e transformare i dati immutabili in modo più agevole e soprattutto in modo maggiormente dichiarativo.

Abbiamo preferito l'utilizzo di **Factory** attraverso il metodo `apply` nel companion object dei trait invece che esporre direttamente le implementazioni al fine di dare una modellazione più astratta del concetto, che sia indipendente dalla specifica implementazione, consentendo allo stesso tempo di modificare dettagli implementativi in modo più agevole.

Tutto ciò cercando di utilizzare le proprietà, le funzionalità e i costrutti messi a disposizione da **Scala 3**, come ad esempio: uniform access, extension methods, currying, mixins, given instances, type, function type ...

### Boundary

Considerando la necessità di eseguire il rendering della simulazione, sono necessari dei pannelli che contengano le informazioni di simulazione.
Al fine di evitare di modellare gli stessi concetti sia per il boundary JVM che per il boundary JS, si è deciso di creare il concetto di Pannello a livello shared del Boundary.
**BasePanel** modella il concetto di pannello di base che può essere inizializzato e stoppato. Esso viene esteso da **UpdatablePanel** il quale permette di aggiornare il pannello con il nuovo stato dell'*Environment*. Infine, è stato modellata anche la capacità di emettere degli eventi attraverso l'**EventablePanel** che sfrutta il pattern **self-type** in quanto rappresenta un'estensione, una capacità, che può essere aggiunta ad un qualsiasi pannello.

Di seguito verranno descritte le scelte implementative più rilevanti prese nello sviluppo dei tre boundary.

#### JVM

Come anticipato nel design di dettaglio *Java Swing* è fortemente object-oriented e segue un approccio a side-effects. 
Al fine di gestire il disegno dei concetti di Simulazione è stata utilizzata la type-class *Drawable* descritta precedentemente. È stata creata l'instanza per JVM chiamata **DrawableSwing** in cui viene specificato il tipo di grafica utilizzato, *Graphics2D*, ed è stato eseguito un "pimping" specificando l'operazione aggiuntiva che consente di poter disegnare attraverso un'unica chiamata un *set* di *DrawableSwing*. 

I concetti estesi con la capacità di disegno sono: *Environment*, *SimulationEntity* e *SimulationStructure*. Ciò ha permesso di isolare il codice necessario per eseguire il disegno degli elementi e quindi di isolare l'approccio a side-effects tipico dell'API di *Graphics2D*.

Il Boundary JVM è in particolare un **ConfigBoundary** in quanto è il boundary principale dell'applicativo Desktop dedicato alla visualizzazione dell'interfaccia grafica del simulatore. Esso è composto da due frame principali:

- *InitGUI*: è il primo frame che viene visualizzato ed è dedicato al caricamento della configurazione e alla visualizzazione degli eventuali errori associati.
- *SimulationGUI*: è il frame che visualizza la simulazione in corso.

La struttura di entrambi i frame è stata sviluppata attraverso una descrizione monadica lazy sfruttando i **Task** (di cui è stato creato un "alias", `io`, per una migliore leggibilità del codice lato gui) offerti dalla libreria *Monix*. In questo modo essi si integrano molto facilmente all'interno di tutto il sistema monadico predisposto e descritto precedentemente. Inoltre, il frame *SimulationGUI*, data la sua complessità, è stato suddiviso in diversi pannelli che estendono i trait **BasePanel**, **UpdatablePanel** ed **EventablePanel** descritti in precedenza. 

Il Boundary necessita di esporre verso l'esterno gli eventi generati da esso. Questo è stato realizzato andando ad eseguire il *merging* di tutte le sorgenti di eventi presenti: **EventablePanels** ed **EventSource**, attraverso l'API di **Observable** offerta dalla libreria *Monix*.

#### JS

Al fine di gestire il disegno dei concetti di Simulazione è stata utilizzata anche qui la type-class *Drawable* descritta precedentemente. È stata creata l'instanza per JS chiamata **DrawableJS** in cui viene specificato il tipo di grafica utilizzato, *CanvasRenderingContext2D*, e, similmente a prima, è stato eseguito un "pimping" specificando l'operazione aggiuntiva che consente di poter disegnare attraverso un'unica chiamata un *set* di *DrawableJS*. 

I concetti estesi con la capacità di disegno sono: *Environment*, *SimulationEntity* e *SimulationStructure*. Ciò ha permesso di isolare il codice necessario per eseguire il disegno degli elementi e quindi di isolare l'approccio a side-effects tipico dell'API di *CanvasRenderingContext2D*.

Anche il Boundary JS è un **ConfigBoundary** in quanto è il boundary principale della WebApp ed è anch'esso dedicato alla visualizzazione dell'interfaccia grafica del simulatore. Esso è composto da una sola schermata la quale è stata sviluppata attraverso una descrizione monadica lazy sfruttando i **Task** offerti dalla libreria *Monix*. In questo modo essi si integrano molto facilmente all'interno di tutto il sistema monadico predisposto e descritto precedentemente. Similmente a quanto accade per *SimulationGUI* del Boundary JVM, data la sua complessità, la schermata è stato suddivisa in diversi pannelli che estendono i trait **BasePanel**, **UpdatablePanel** ed **EventablePanel** descritti in precedenza. 

Il Boundary necessita di esporre verso l'esterno gli eventi generati da esso. Questo è stato realizzato andando ad eseguire il *merging* di tutte le sorgenti di eventi presenti: **EventablePanels** ed **EventSource**, attraverso l'API di **Observable** offerta dalla libreria *Monix*.

#### Exporter

Per quanto riguarda il metodo per esportare dati ad ogni step della simulazione esso utilizza tecniche di **folding** sugli estrattori di dati presenti.

Inoltre gli estrattori di dati sono stati implementati attraverso *case class* immutabili le quali hanno il solo compito di implementare il metodo generico *extractData()* a seconda del tipo di dato che si intende estrarre.

### Launcher

### Loader

#### Parser

Al Parser vengono aggiunti tramite **extension methods** i metodi *shouldBeWithin* e *andIfNot*.
In questo modo per ciascun parametro è possibile controllare se rientra nel range di valori possibili e in caso negativo generare un errore che verrà comunicato ai Boundary.

Per quanto riguarda invece il *YAMLParser*, esso è arrichito dai metodi *to* e *has* per semplificare le operazioni di look-up e di conversione sulla mappa restituita dal parsing del file YAML.

#### Reader

Il Reader di JavaScript utilizza un *PublishSubject* di *Monix* per leggere il file caricato dall'utente sul broswer. In questo modo è possibile implementare il metodo utilizzando un approccio ad eventi ed associare ad esso un Task di Monix per rimanere coerenti con il resto del progetto.

### Engine

// Spiegazione del dispatch 

// Dire quali logiche implementate con breve descrizione sia di update che event (specificando che nella config è stata utilizzata una mappa come funzione) 

// 

### Environment

#### Common

// far notare che probable events contiene le formule, magari parlando di quella di contagio

#### Entity

#### Structure

// Structures, che essendo case class con i defaults emulano il pattern Builder

#### Virus

### Test

### Suddivisione del lavoro

#### Acampora Andrea

#### Accursi Giacomo

#### Giulianelli Andrea

<div style="page-break-after: always;"></div>
