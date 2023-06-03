# Modo de ejecución
## Configuraciones: 
En cada carpeta, tanto del Master como de los esclavos hay un archivo .env que permite configurar los puertos y el path de los logs que se crean       

## Información: 
		
Son 4 categorías las implementadas: Deporte, Hogar, Salud y Juguetes.
Aún así, se pueden agregar más al archivo json y la aplicación los tratará de manera automática.

**Explicación:**

*Productos: Son N nodos esclavos, por cada producto pedido en la **query** un nodo lo buscará. Primero llega la petición **GET** al nodo maestro este distribuye la carga 1 a 1 a cada nodo.*

*Categorías: Son N nodos, donde cada nodo está asociado a la búsqueda de productos de un tipo de categoría: <br>
**Si hay más categorías que nodos, el sistema automáticamente acopla una categoría a un nodo que ya tiene una categoría asignada**

*Cuando se hace la búsqueda, cada nodo entrega todos los productos de su categoría asignada.*

### Pasos para ejecución: 
1. Instalar framework (flask)

		pip install flask 
2.	Instalar libreria necesaria para enviar peticiones desde el master a los nodos esclavos
				
		pip install requests  


3. Para iniciar servidor Master:

	    cd rest_api_python
	    cd Master
        python master.py


4. cada nodo esclavo (usaremos 3 para las pruebas)
5. Iniciar nodos esclavo:

	    cd rest_api_python
        cd slaveN, N entre 1 y la cantidad de esclavos deseada
	    python slave.py 


6. Ejecutar peticiones:

		http://127.0.0.1:5001/query?categorias=Hogar+Deporte+Salud
		http://127.0.0.1:5001/query?productos=Mesa+Silla+Lampara


**Observación:** 
    Tanto los productos como categorías se pueden pedir como: lampara o Lampara, ya que se uso la función lower para evitar problemas.

**Datos:**
name | category | 
|--|--|
Sofa|Hogar
Bicicleta|Deporte
Vitaminas|Salud
Lampara|Hogar
Raqueta|Deporte
Medicina|Salud
Silla|Hogar
Pelota|Deporte
Suplemento|Salud
Mesa|Hogar
Pesas|Deporte
Aspirina|Salud
Almohada|Hogar
Gorra|Deporte
Jabon|Salud
Manta|Hogar
Toalla|Deporte
Cuerda|Deporte
Jarabe|Salud
Mueble|Hogar
