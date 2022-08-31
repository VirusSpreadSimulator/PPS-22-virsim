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

// Descrizione panels

#### JVM

//

Considerando che *Java Swing* è fortemente object-oriented e con un approccio a side-effects, al fine di gestire il disegno dei concetti di Simulazione è stata utilizzata la type-class *Drawable* descritta precedentemente. È stata creata l'instanza per JVM chiamata **DrawableSwing** in cui viene specificato il tipo di grafica utilizzata, **Graphics2D**, ed è stato eseguito un "pimping" specifcando l'operazione aggiuntiva che consente di poter disegnare con solo una chiamata un *set* di *DrawableSwing*. 

I concetti estesi con queste capacità sono: *Environment*, *SimulationEntity*, *SimulationStructure*, permettendo quindi di disegnarli isolando tutto ciò che lavora tramite side-effects.

// fatto che il codice non funzionale è limitato qui in drawable e isolato 

// ma comunque è protetto dall'utilizzo di un approccio monadico rispetto a dove viene chiamato

// Come si è suddivisa la GUI in frame e pannelli + merge dei vari eventi

​	// publish subject pannello di init (config boundary) 

// MonadComponents -> conversione + Observer creati ad hoc (magari con un esempio) + merge degli eventi



#### JS

// Come JVM

#### Exporter

### Launcher

### Loader

#### Parser

#### Reader

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
