# Climb Console

Questa è la console web di Climb, basata sul codice di quella di "Percorsi Rovereto". L'unica cosa rimasta intatta dalla base originale è la libreria di caricamento su imgur, mentre tutto il resto è stato modificato.
Di seguito la documentazione sui principali componenti dell'app.

### Occhio ai nomi!

Prima di entrare nel codice dell'app, dovete sapere cosa indicano i nomi dei componenti. Eccoli di seguito:

* ***path*** (o talvolta *itinerary*): indica un percorso legato a un gioco. Naturalmente un gioco può avere più percorsi al suo interno.
* ***leg*** (in rari casi *poi*): indica una sezione di percorso compresa tra la tappa precedente e quella che si sta modificando. La modifica di una *leg*, di fatto, è la modifica della posizione di una determinata tappa.
* ***game***: indica un gioco, sempre legato ad una scuola e che al suo interno può contenere più itinerari/percorsi
* ***school***: indica una scuola, che al suo interno contiene giochi e linee pedibus
* ***line*** (in alcuni casi *route*): indica una linea pedibus, che dev'essere per forza legata ad una scuola.

### Controllers

Ogni file contiene i controller legati a una specifica sezione dell'app. Ad esempio, *paths.js* contiene i controller per la lista dei percorsi, per la pagina del singolo percorso e le tab annesse. Stessa cosa per gli tutti gli altri, che variano semplicemente per la quantità di controller inclusi (dovuta ovviamente al diverso numero di pagine).<br>
Il **main-controller** è il padre di tutti i controller, e contiene alcune variabili di uso globale legate al caricamento dei dati. Se in alcune parti del codice trovate *$scope.parent* o *$parent* nella maggior parte dei casi ci si sta riferendo a tale controller.

### Servizi

I servizi della web-app sono suddivisi in 4 file: *data-service.js* contiene le funzioni per il caricamento e salvataggio dei dati da/su server, *imgur-service.js* contiene quelle per il caricamento delle immagini delle "leg" su imgur, *maps-service.js* ingloba i servizi per la visualizzazione delle mappe di Google per ogni sezione dell'app in cui vengono usate. L'ultimo, *createDialog.js*, è un servizio esterno che offre una gestione semplificata delle finestre modali di Bootstrap; potete consultarne la documentazione [qui](http://fundoo-solutions.github.io/angularjs-modal-service/) e [qui](https://github.com/Fundoo-Solutions/angularjs-modal-service/blob/master/README.md).

### *App.js*

Attenzione! Questo file è molto importante in quanto contiene la configurazione dell'applicazione Angular. Ricordatevi **sempre** di aggiungere qui i nuovi controller e servizi che implementate e di modificare la parte *.config* ogniqualvolta modificate la posizione dei template o ne create di nuovi.

### Templates

I templates HTML sono suddivisi in base alle sezioni dell'app e rappresentano gli "scheletri" di ciascuna delle viste dell'applicazione. Vi sono però delle eccezioni: i template della cartella *modals* contengono il corpo di ognuna delle finestre modali della console (scelta obbligata se si usano i modal di Boostrap), mentre ci sono tre templates che non appartengono a nessuna categoria:

* ***dataset-selection***: contiene la sezione di selezione del set di dati che si trova in tutte le liste principali, in modo da massimizzare il riutilizzo del codice
* ***header***: contiene il layout della barra superiore
* ***upload***: contiene la sezione di upload del percorso da file (potrebbe non essere più necessaria)

### Cosa c'è da ancora da implementare?

* Gestione anagrafica dei volontari pedibus
* Login/logout
* Salvatggio/modifica/cancellazione fermate lato server
* ...

### Versioni componenti esterni

* **AngularJS**: 1.5.11. Se si vuole aggiornare al ramo 1.6, assicurarsi di aver sostituito tutti i callback *.success().error()* (oramai deprecati) con *.then(function(), function())*.
* **jQuery**: 2.2.4. L'aggiornamento al ramo 3.x non dovrebbe essere un problema, personalmente non ho effettuato alcuna prova.
* **jQuery-ui**: 1.12.1. Ultima versione, al momento della scrittura.
* **Angular-ui-router**: 0.4.2. Sconsigliato l'aggiornamento al ramo 1.0, bisognerebbe apportare pesanti modifiche.
* **Angular-ui-sortable**: 0.14.4. Impossibile aggiornare ulteriormente a meno di non portare jQuery almeno alla versione 3.1.
* **Angular-ui-bootstrap**: 0.14.3. L'aggiornamento è fattibile, ma bisognerebbe adattare il modo in cui vengono configurati alcuni elementi (come i date-picker).
* **Bootstrap**: 3.2.0. L'aggiornamento al ramo 3.3 dovrebbe essere fattibile.

*Ultimo aggiornamento: 04/08/2017 - Francesco Saltori*
