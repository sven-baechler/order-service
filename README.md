# order-service
Projekt im Modul Software Architecture and Techniques (SWAT).

## Beschreibung Setup
TODO

## Analyse Code - Erkentniss Testbarkeit
In den Receivern wird unter anderem der MongoClient direkt in den zu testenden Methoden instanziert.
Durch diese Architektur lässt sich der Client nicht mocken und es muss ein Refactoring vorgenommen werden.

## TODO
* exchange "swda" automatisch erstellen bei `docker-compose -f stack.local.yml up`
* UML-Klassendiagramm
