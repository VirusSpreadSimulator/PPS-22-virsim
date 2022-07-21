## Design Architetturale

### Architettura complessiva

Per garantire una corretta separazione delle responsabilità e per fornire flessibilità ed estendibilità alle componenti del software, il progetto adotta il pattern architetturale **ECB** (**Entity-Control-Boundary**).
Di seguito è fornito il diagramma che riprende gli elementi core dell'architettura proposta.

![ecb_architecture_diagram](/home/andrea/Desktop/PPS-22-virsim/doc/report/imgs/architecture.svg)

### Descrizione del pattern architetturale utilizzato

L'architettura è stata progettata partendo direttamente dal diagramma dei casi d’uso fornito nel capitolo dei requisiti, come tipico delle architetture basate su ECB, dove sono presenti gli attori del sistema e i rispettivi casi d'uso. 
La distinzione di tre diverse tipologie di oggetti all’interno del sistema, *entity*, *control* e *boundaries*, porta a dei modelli che sono più resilienti a cambi futuri:

- *boundary*: ciascun *boundary* incapsula l'interazione con gli attori del sistema, come ad esempio utenti o servizi esterni. Tipicamente essi vengono cambiati con una frequenza maggiore rispetto ai *control*.
- *control*: ciascun *control* implementa la business-logic richiesta per gestire l'esecuzione di un caso d'uso coordinando le *entità* coinvolte ed eventualmente interagendo con uno o più *boundary*. Tipicamente essi vengono cambiati con una frequenza maggiore rispetto alle *entity*.
- *entity*: rappresentano il modello del dominio del sistema.

Perciò ciascun *Boundary* rappresenta una diversa interazione tra un attore ed il sistema. Il *Control* rappresenta quindi la realizzazione di un determinato caso d’uso da parte di un attore tramite uno specifico *Boundary* mentre le *Entities* rappresentano i dati persistenti del dominio.

*// il perchè della scelta (anche rispetto ai requisiti) riprendendo anche tutti i concetti che abbiamo messo su notion in Architettura e Effective Progetto.*

*// rimando alla clean architecture facendo un mapping degli elementi, specificando la possibilità di un'interazione bi-direzionale nel momento in cui si rispetta il principio DIP* .

### Descrizione dei componenti dell'architettura

*// descrizione architettura (le nostre scelte) e magari un possibile flow descritto da diagramma di sequenza*



