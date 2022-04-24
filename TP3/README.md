# Movimiento Browniano
El siguiente programa simula un sistema de movimiento browniano el cual 
consiste en un un dominio cuadrado de lado L = 6 m. En su interior colocar 
100 < N < 150 partículas pequeñas de radio R1 = 0.2 m y masa m1 = 0.9 kg y 
una partícula grande de radio R2= 0.7 y masa m2 = 2 kg.
Las posiciones de todas las partículas son al azar con distribución
uniforme dentro del dominio. Las partículas pequeñas tienen velocidades 
con una distribución uniforme en el rango: |v| < 2 m/s. La partícula grande 
tiene velocidad inicial v2 = 0 y su posición inicial en x=L/2, y=L/2.

## Requisitos para la ejecución
- Ovito
- Python
- Java

## Ejecutar Simulación
```bash
    java -jar target/run.jar
```

## Crear archivo de animación
```bash
    python animation.py
```
Se generará un arhivo `out.xyz` el cual se debe abrir con Ovito
