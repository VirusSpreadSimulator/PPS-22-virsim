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

- descrizione
- appunto su config boundary
- elenco tipologie

#### JVM

#### JS

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

