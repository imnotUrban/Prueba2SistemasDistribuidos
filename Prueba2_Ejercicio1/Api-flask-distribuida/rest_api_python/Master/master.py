from flask import Flask, jsonify
from flask import Flask, request, jsonify,json
import requests
from dotenv import load_dotenv
import os

app = Flask(__name__)


## Productos
load_dotenv() #Cargamos las variables almacenadas en .env
hostServer = os.getenv("HOST_SV")
portServer = os.getenv("PORT_SV")

slaveQuantity = int(os.getenv("SLAVE_QUANTITY"))
#Creamos una lista que contenga las direcciones de todos los escalvos
slaveList = []
for i in range(slaveQuantity):
    slavei = os.getenv(f"SLAVE_{i}")
    slaveList.append(slavei)



#Ruta por defecto y manejo de errores
@app.route("/")
def home():
    return f"Acá no hay nada que ver, prueba con: http://127.0.0.1:5001/query?categorias=Hogar+Deporte+Salud"

# Definimos la ruta que manejará todas las solicitudes HTTP que no coincidan con ninguna otra ruta definida
@app.errorhandler(404)
def page_not_found(e):
    return {"message": "Ruta no encontrada, prueba con: http://127.0.0.1:5001/query?categorias=Hogar+Deporte+Salud"}
# Muestra la lista resultante



@app.route('/query', methods=['GET'])
def products_query():
    if(request.args.get('productos') is not None):
        prod = request.args.get('productos') #recibe los argumentos en un string -> "prod1 prod2"
        prod_list = prod.split(' ') #lo separa en una lista [prod1, prod2]
        list_prod = []
        print("Maestro dispersa la carga en los esclavos")
        for i in range(len(prod_list)):
            response = requests.get(f"{slaveList[i%slaveQuantity]}/node", params={"productos": f"{prod_list[i]}"}).json()
            if(len(response)>0):
                for i in response:
                    list_prod.append(i)
        return jsonify({"products": list_prod})
    
    ###### Por categorias
    elif(request.args.get('categorias') is not None):
        cat = request.args.get('categorias') #recibe los argumentos en un string -> "cat1 cat2"
        cat = cat.strip('\n')  #Quitamos el salto de linea ya que nos daba un pequeño error al ejecutar (Al final de cat siempre había un /n)
        cat_list = cat.split(' ') #lo separa en una list_prod [cat1, cat2] categorias
        print(cat_list)
        list_prod = []
        for i in range(len(cat_list)):
            response = requests.get(f"{slaveList[i%slaveQuantity]}/catNode", params={"categorias": f"{cat_list[i]}"}).json()  #Puerto 4000 corresponde a  la categoría Deporte
            for i in response:
                list_prod.append(i)
        return jsonify(list_prod)

    
    ##Query vacia o con formato invalido
    else:
        return jsonify({"message": "Query not found"})



#FIN ENDPOINTS

if __name__ == '__main__':
    app.run(host = hostServer, port = portServer,debug=True, threaded=True)
    #print(f"El servidor 'Master' ha iniciado correctamente en el puerto {portServer}")
##Se ejecuta::
# python master.py
#