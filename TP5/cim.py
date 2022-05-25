from typing import List

# Modelo de la fuerza sobre una particula
# Rt: [[r0x,r0y], ..., [rnx,rny]]
# Vt: [[v0x,v0y], ..., [vnx,vny]]
# N: Cantidad total de particulas
# i: Particula cuyos vecinos se obtienen
# d: Distancia minima para considerar vecinos
# Devuelve los indnices de las particulas vecinas

def get_neighbours(R, V, N, i, d) -> List[int]:
    if R.shape != (N,2) or V.shape != (N, 2):
        raise Exception("Dimension of given position/velocity matrix is invalid")
    raise NotImplemented()