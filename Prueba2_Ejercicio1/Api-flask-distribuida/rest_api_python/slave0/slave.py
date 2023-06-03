from dotenv import load_dotenv
from flask import Flask, request, jsonify,json
import logging
from logging import handlers
import time
import os

app = Flask(__name__)
load_dotenv() #Cargamos las variables almacenadas en .env

hostServer = os.getenv("HOST_SV")
portServer = os.getenv("PORT_SV")

logPath = os.getenv("PATH_SV")
print(logPath)
logPath = "archivo.log"
# ##LOGGER####################################3

# # Crear un objeto de registro
logger = logging.getLogger("my_logger")
logger.setLevel(logging.DEBUG)

# # Crear un manejador para escribir en el archivo de registro
log_file = logPath
file_handler = handlers.RotatingFileHandler(log_file, maxBytes=1024, backupCount=3)
file_handler.setLevel(logging.DEBUG)

# # Formateador para el registro
formatter = logging.Formatter('%(message)s')
file_handler.setFormatter(formatter)

# # Agregar el manejador al objeto de registro
logger.addHandler(file_handler)

#################################################

# Lee el archivo JSON

with open("../data/products.json", "r") as file:
    json_data = file.read()

# # Transforma el JSON en una lista de diccionarios
products = json.loads(json_data)


@app.route('/node')  # Funcion para los productos donde cada uno de los esclavos busca un elemento 
def node_x_node():
    logger.debug(f"{time.time()};buscar_por_producto;ini")
    prod = request.args.get('productos') # se ve asi : "producto"
    productFound = [product for product in products if product['name'].lower()==prod.lower()] 
    if(len(productFound)>0):
        logger.debug(f"{time.time()};buscar_por_producto;fin")
        return productFound
    else:
        logger.debug(f"{time.time()};buscar_por_producto;fin") 
        return jsonify({})



@app.route('/catNode')
def cat_node():
    logger.debug(f"{time.time()};buscar_por_categoria;ini")
    # 3 categorias: Deporte, Salud, Hogar
    cat = request.args.get('categorias') # se ve asi : "categoria"
    print(cat)
    found_cat_list = []
    catFound = [category for category in products if category['category'].lower()==cat.lower()]    #Buscamos en la lista, 1 por 1 los categorias
    if(len(catFound)>0):
        logger.debug(f"{time.time()};buscar_por_categoria;fin")
        return catFound
    else:
        logger.debug(f"{time.time()};buscar_por_categoria;fin") 
        return jsonify({})


if __name__ == '__main__':
    app.run(host = hostServer, port = portServer,debug=True, threaded=True)
    #print(f"El servidor 'Master' ha iniciado correctamente en el puerto {portServer}")
##Se ejecuta::
# python slave.py
#