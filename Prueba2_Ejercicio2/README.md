Este codigo funciona con 4 procesos, así mismo funciona con 8 números en el archivo.txt.
Se decidió hacer de esta manera ya que así fue indicado a la hora de presentar el trabajo.



compilar:
mpic++  prueba2.cpp -o prueba2

correr:
mpirun -np 4 ./prueba2