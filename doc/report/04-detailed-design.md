## Design di dettaglio

Dopo aver descritto l'architettura del sistema, si procede con il design di dettaglio, in cui si evidenziano le scelte di progettazione dei componenti principali. In questo capitolo verranno esposti, oltre alle scelte di design, i design pattern utilizzati ed una breve descrizione di come il codice è stato organizzato.

### Obiettivi

Il design del sistema segue un approccio che cerca di combinare i vantaggi del mondo funzionale e del mondo ad oggetti. In generale, la linea che abbiamo seguito è quella di preferire l'approccio funzionale favorendo la dichiaratività, l'immutabilità e la descrizione lazy della computazione ed evitando o eventualmente incapsulando i side-effects e le eccezioni.
In particolare, si è scelto di perseguire l'immutabilità evitando o incapsulando side-effects ed eccezioni in quanto nella maggior parte dei casi ciò permette di semplificare la logica del programma e soprattutto l'analisi e la comprensione del codice. Infatti, quando un elemento è mutabile, occorre ricostruire il suo stato rispetto a tutto il flow in cui esso viene utilizzato. Quindi, in questo design si è scelto di stressare l'immutabilità, anche per cercare di ottenere, ove possibile, il concetto di funzione pura, la quale a parità di input restituisce sempre lo stesso output. 

Inoltre, si è deciso di adottare, ovunque possibile, un approccio monadico. Infatti questo approccio permette di rappresentare sequenze, anche complicate, di funzioni attraverso pipeline succinte che astraggono dal control flow e soprattutto dai side-effects. Infatti, l'approccio monadico consente  di rappresentare i side-effects come effects sul quale si ha maggiore controllo ed inoltre, utilizzando la libreria Monix, è stato possibile ottenere facilmente anche il vantaggio di poter descrivere la computazione in modo lazy, limitando i side-effects nell'*end-of-the-world*.
La scelta della libreria Monix per il nostro approccio monadico è derivata dal fatto che Monix possiede la monade *Task*, la quale consente di rappresentare la specifica di una computazione lazy o asincrona che, una volta eseguita, produrrà un risultato, assieme a tutti i possibile side-effect. *Task*s infatti non è *eager*, ed è *referential transparent* nel suo utilizzo safe. Questo permette di dividere l'esecuzione dalla descrizione della computazione, favorendo la dichiaratività.

### Design a componente dell'architettura

Il pattern architetturale *ECB* descritto nel capitolo precedente si presta facilmente alla *component-programming* in cui sostanzialmente ogni elemento appartenente all'entity, al boundary o al control lo si vede come un componente che ha dipendenze da altri (come ad esempio l'engine che necessita dei boundary per poter comunicare loro il nuovo stato). 
Per questo motivo, si è scelto di progettare l'architettura a livello di design di dettaglio scegliendo il **Cake Pattern** grazie al quale è stato possibile rappresentare ogni elemento dell'architettura come un componente con una ben definita interfaccia offerta agli altri componenti, e delle ben definite dipendenze dagli altri componenti. L'applicazione risultate quindi viene costruita instanziando ogni componente e collegandoli in modo da soddisfare le varie dipendenze.

Grazie a questo pattern è stato possibile rappresentare esplicitamente le dipendenze tra i vari elementi architetturali permettendo di fare dependency injection agilmente. 

La combinazione ECB + Cake Pattern ha semplificato notevolmente il raggiungemento del requisito 2.6 il quale comporta la realizzazione di un applicazione cross-platform (JVM e JS). Infatti, ha permesso di limitare al minimo la ripetizione di codice, condividendo tra le diverse piattaforme tutto il design dei componenti di core, limitando le modifiche principali ai boundaries e agli elementi nativi di ogni piattaforma.

Il design di ogni componente è il medesimo:

![component_diagram](imgs/detailed_design_component_diagram.svg)

Come si vede dal diagramma, gli elementi presenti in ciascun componente sono:

- un trait che definisce l'interfaccia, cioè il contratto, del componente; qui chiamato per comodità **ComponentInterface**
- un trait che si occupa di fornire l'instanza del componente; qui chiamato per comodità **Provider**
- un trait, qui chiamato per comodità **Component**, che utilizza le dipendenze richieste dal componente (**Requirements**) e contiene l'implementazione del componente stesso, qui chiamata per comodità **ComponentImpl**.
- un trait che espone tutti i concetti del componente necessari per poter essere utilizzato assieme agli altri, qui chiamato per comodità **Interface**.

Combinando tutto ciò con gli obiettivi del design descritti nella sezione precedente *si può riassumere il design in questo modo*:

- Ogni elemento architetturale è rappresentato come un componente.
- Le entities sono progettate favorendo un approccio funzionale.
- Le computazioni sono rappresentate con un approccio monadico attraverso l'utilizzo della libreria Monix.
- Gli eventi di ogni componente boundary sono rappresentati come stream di eventi attraverso l'uso di *Observable* di Monix.
- I side effects sono limitati all'*end-of-the-world*.

### Boundary

Come anticipato, ciascun *boundary* incapsula l'interazione con gli attori del sistema.
Il pattern ECB pone le sue fondamenta sul fatto che tutti i Boundary siano uguali e passavi, ricevendo le stesse informazioni dai control ed incapsulando le interazioni. Le interazioni degli attori del sistema con i componenti boundary vengono rappresentati nel nostro design come stream di eventi, sfruttando **Observable** di Monix.

Tra i boundary che possono essere iniettati all'interno del simulatore deve essere sempre essere prensente un **ConfigBoundary** dedicato al caricamento della configurazione e alla visualizzazione degli errori in essa. La necessità di un tipo speciale di Boundary è nata dal fatto che nel nostro caso abbiamo due tipologie di eventi:

- *eventi asincroni*: sono quelli che vengono emessi dall'interazione dell'attore con il sistema
- *eventi sincroni*: rappresentano quegli eventi necessari per la configurazione della simulazione, e che quindi devono essere ricevuti in un certo ordine.

Al fine di rispettare la *dependency rule* descritta dalla Clean Architecture e da ECB, si è deciso di modallare un ulteriore trait **ConfigBoundary** che estende il trait **Boundary** con due metodi necessari per ottenere la configurazione e segnalare errori al boundary. In questo modo, il componente Boundary rimane passivo, infatti non eseguirà mai chiamate dirette agli elementi del control rispettando la *dependency rule*.

![config_boundary](imgs/detailed_design_config_boundary.svg)

Perciò, tra i boundary specificati per l'applicazione ve ne sarà solamente uno di tipo ConfigBoundary, il quale gestirà, tra le altre cose, anche la parte di inizializzazione della simulazione con il compito di fornire la configurazione e gestire gli eventuali errori verso l'attore del sistema.

I boundary sviluppati sono i seguenti:

- **GUI-JVM**: si occupa della creazione di un'interfaccia grafica dell'applicazione Desktop jvm-based.
- **Esportatore**: si occupa dell'esportazione in un foglio di calcolo dei dati aggregati e delle statistiche riguardanti la simulazione
- **GUI-JS**: si occupa della creazione dell'interfaccia grafica della WebApp js-based. Come detto precedentemente infatti, l'applicazione sviluppata dovrà essere cross-platform e la specifica di un apposito boundary rientra tra le parti platform-specific.

Considerando la necessità di eseguire il rendering della simulazione, nonostante solitamente i framework per gestire le GUI siano fortemente object-oriented e sfruttino principalmente side-effects si è deciso comunque di descrivere la struttura delle view utilizzando un approccio monadico, isolando tutto ciò che non è funzionale nell'*end-of-the-world*. 
A tal proposito, al fine di isolare l'approccio a side-effects tipico del disegno degli elementi su *"canvas"*, è stata creata la **type-class** **Drawable** la quale rappresenta l'estensione di un tipo generico con le capacità di disegno. Questo è un concetto comune e non platform-specific.

![drawable_concept](imgs/detailed_design_drawable_general.svg)

La type class è stata progettata per lavorare con gli extension methods di Scala (non facilmente rappresentabili in UML). 
Grazie a questa type-class la capacità di essere disegnati può essere inserita a piacere su ogni tipo definito anche dopo la sua definizione. Tutto ciò grazie al pattern **type class** che ci permette di definire metodi dotati di **polimorfismo ad-hoc**. 
Essendo un concetto comune a tutti i boundary, Drawable astrae dal tipo di grafica utilizzata e definisce al suo posto un **abstract type** (**Graphic**).
In questo modo i boundary platform-specific potranno specificare il proprio tipo ed eseguire il "pimping" di operazioni basate su di essa.

Inoltre, al fine di rappresentare il concetto di sorgente di eventi a livello di boundary è stato modellato il trait **EventSource**.

![event_source](imgs/detailed_design_event_source.svg)

Il seguente concetto modella tutto ciò che è in grado di emettere eventi dovuti all'interazione dell'attore: pulsanti, text fields, ecc... In questo modo i suddetti componenti possono essere integrati con maggiore facilità ed elasticità all'interno di un contesto monadico.
**Event** rappresenta gli eventi emessi dai boundary ed è modellato attraverso un *Product Type*. Ogni evento specifica il suo interesse rispetto ad un particolare stato dell'engine, esprimendo il fatto che esso, in un particolare stato dell'applicazione, potrebbe perdere di significatività.

#### JVM

Il boundary che gestisce la gui jvm-based si occupa di visualizzare l'interfaccia grafica del simulatore dell'applicazione Desktop.
L'applicazione è composta da due schermate principali che soddisfano i mockup sviluppati ed approvati dal committente e mostrati nel capitolo dei requisiti. 

Il design di questo boundary è avvenuto considerando l'utilizzo della libreria **Java Swing**. Al fine di poter integrare agilmente il design monadico di tutto il sistema con la gui, si è deciso di adottare un approccio in cui le varie view consistono in descrizioni monadiche lazy della costruzione e del comportamento dei componenti, in modo tale da aderire al paradigma funzionale incapsulando la natura object-oriented e side-effect oriented di **Java Swing**.
Inoltre, considerando che i boundary comunicano con i control emettendo eventi, tutti i componenti di Java Swing necessari all'interazione degli attori sono stati ridefiniti attraverso dei wrapper ad-hoc che consentono di integrarli agilmente in un contesto monadico (**MonadComponents**). In particolare, ogni componente in questione (wrapper di: *JButton*, *JComboBox*, *JTextField*, ...) estende il trait **EventSource** descritto in precedenza.

In questo modo ogni componente, il quale esprime, in Java Swing, ogni comportamento attraverso side-effect, diventa un componente facilmente integrabile in un contesto monadico nel quale il flow è gestito attraverso stream di eventi.

Considerando che *Java Swing* è fortemente object-oriented e con un approccio basato sui side-effects, al fine di gestire il disegno dei concetti di Simulazione è stata utilizzata la type-class *Drawable* descritta precedentemente. Maggiori dettagli verrano forniti nel capitolo *Implementazione*.

#### JS

Il boundary che gestisce la gui js-based si occupa di visualizzare l'interfaccia grafica del simulatore della WebApp.
In questo caso è stata sviluppata un'unica schermata in quanto non erano previste indicazioni dal committente.

Il design di questo boundary è avvenuto considerando l'utilizzo della libreria **Scalajs**. Scalajs, per quanto riguarda l'API fornita, emula fortemente JavaScript rendendo poco agevole il suo utilizzo diretto in un approccio monadico. Perciò, al fine di poter integrare agilmente il design monadico di tutto il sistema con la gui, si è deciso di adottare un approccio in cui le varie view consistono in descrizioni monadiche lazy della costruzione e del comportamento dei componenti similmente a quanto descritto per il *boundary jvm-based*.

Similmente a quanto descritto precedentemente, considerando che i boundary comunicano con i control emettendo eventi, tutti i componenti HTML-based di Scalajs necessari all'interazione degli attori sono stati ridefiniti attraverso dei wrapper ad-hoc che consentono di integrarli agilmente in un contesto monadico (**MonadComponents**). In particolare, ogni componente in questione (wrapper di: *Button*, *Select*, *Input*, ...) estende il trait **EventSource** descritto in precedenza.

Al fine di gestire il disegno dei concetti di Simulazione è stata utilizzata la type-class *Drawable* descritta precedentemente. Maggiori dettagli verrano forniti nel capitolo *Implementazione*.

#### Exporter

### Launcher

### Loader

#### Parser

#### Reader

### Engine

### Environment

- Descrizione del componente ECB
- env iniziale poi evoluto dall'engine.
-  Dipende solo dal loader

#### Common

#### Entity

#### Structure

#### Virus

### Pattern di progettazione 

### Organizzazione del codice

<div style="page-break-after: always;"></div>

