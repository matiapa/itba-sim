from typing import List

import numpy as np

# Modelo de la fuerza sobre una particula
# Rt: [[r0x,r0y], ..., [rnx,rny]]
# Vt: [[v0x,v0y], ..., [vnx,vny]]
# N: Cantidad total de particulas
# i: Particula cuyos vecinos se obtienen
# d: Distancia minima para considerar vecinos
# Devuelve los indices de las particulas vecinas

def get_neighbours(R, D, L, W, Zl, Zw, rc) -> List[List[int]]:
    N = len(R)
    return [[] for i in range(N)]