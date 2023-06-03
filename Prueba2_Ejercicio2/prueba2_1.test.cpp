#include <stdio.h>
#include <stdlib.h>
#include "mpi.h"
#include <iostream>
#include <fstream>
#include <sstream>
#include <vector>
#include <algorithm>

using namespace std;

int main(int argc, char** argv) {
    /**
    *size: cantidad de procesos
    *rank: identificador del proceso
    */
    
    MPI_Init(&argc, &argv); //Inicializa entorno MPI con los argumentos

    int rank, size;
    MPI_Comm_rank(MPI_COMM_WORLD, &rank); //Crea rangos (en este caso desde 0 a 3)
    MPI_Comm_size(MPI_COMM_WORLD, &size); //Cantidad de procesos que participan en el comunicador
    
    
    if (argc != 1) {
        if (rank == 0) {
            cout << "Error" << endl;
            cout << "Ejemplo de ingreso de ejecución: mpirun -np 4 ./prueba2" << endl;
        }
        MPI_Finalize();
        return 0;
    }

    if (size != 4) {
        if (rank == 0) {
            cout << "Error" << endl;
            cout << "Ejemplo de ingreso de ejecución: mpirun -np 4 ./prueba2" << endl;
        }
        MPI_Finalize();
        return 0;
    }

    if (rank == 0) {  //Si es el primer proceso
        ifstream file("archivo.txt");   //Lee archivo

        if (!file) {
            cerr << "Error al abrir el archivo." << endl;
            MPI_Abort(MPI_COMM_WORLD, 1);
        }

        string line;
        getline(file, line);
        file.close();

        stringstream ss(line);
        vector<int> numbers;
        string token;

        while (getline(ss, token, ';')) {   //llenamos un vector donde se le van agregando los números
            numbers.push_back(stoi(token));
        }

        if (numbers.size() != 8) {
            cerr << "El archivo debe contener 8 números separados por ';'." << endl;
            MPI_Abort(MPI_COMM_WORLD, 1);
        }
        
        vector<int> pairs[4];
        pairs[0].push_back(numbers[0]);   //A cada i del vector de enteros le agregamos 2 numeros
        pairs[0].push_back(numbers[1]);

        pairs[1].push_back(numbers[2]);
        pairs[1].push_back(numbers[3]);

        pairs[2].push_back(numbers[4]);
        pairs[2].push_back(numbers[5]);

        pairs[3].push_back(numbers[6]);
        pairs[3].push_back(numbers[7]);

        int max_value = max(pairs[0][0], pairs[0][1]);
        int global_max = max_value;

        cout << "Proceso 0: El par de números es (" << pairs[0][0] << ", " << pairs[0][1] << ")" << endl;   //Calculamos el maximo del proceso 0
        cout << "Proceso 0: El mayor valor es " << max_value << endl;


        /**
        * Desde el proceso 0 envía los pares de números al proceso 1, 2 y 3 según les corresponda 
            2 = cantidad de datos
            MPI_INT = tipo de dato
            i = PRoceso destino
            0 = PRoceso origen
            MPI_COMM_WORLD = Comunicador de los procesos
        */
        for (int i = 1; i < size; ++i) {
            MPI_Send(&pairs[i][0], 2, MPI_INT, i, 0, MPI_COMM_WORLD);   
        }



        // Calcula la suma de los máximos de los procesos
        int sum_max = global_max;
        for (int i = 1; i < size; ++i) {
            MPI_Send(&sum_max, 1, MPI_INT, i, 0, MPI_COMM_WORLD);
        }

        /**
        *Recibe un entero desde el proceso i y despues va sumandolos (el entero es un maximo)
        */
        int sum = max_value;
        for (int i = 1; i < size; ++i) {
            int received_sum;
            MPI_Recv(&received_sum, 1, MPI_INT, i, 0, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
            sum += received_sum;
        }

        cout << "Proceso 0: La suma de los máximos de cada proceso es " << sum << endl;

    } else {
        vector<int> local_pair(2);
        MPI_Recv(&local_pair[0], 2, MPI_INT, 0, 0, MPI_COMM_WORLD, MPI_STATUS_IGNORE);

        int max_value = max(local_pair[0], local_pair[1]);

        cout << "Proceso " << rank << ": El par de números es (" << local_pair[0] << ", " << local_pair[1] << ")" << endl;
        cout << "Proceso " << rank << ": El mayor valor es " << max_value << endl;

        // Envía su máximo al proceso 0
        MPI_Send(&max_value, 1, MPI_INT, 0, 0, MPI_COMM_WORLD);

        // Recibe el valor de la suma y envía su propio máximo
        int sum_max;
        MPI_Recv(&sum_max, 1, MPI_INT, 0, 0, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
        MPI_Send(&max_value, 1, MPI_INT, 0, 0, MPI_COMM_WORLD);
    }

    MPI_Finalize();
    return 0;
}
