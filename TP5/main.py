from math import sqrt
import numpy as np
from numpy.linalg import norm
from cim import get_neighbours

# ---------------------------------------------------------

# Parametros del sistema

L = 1       # L ε [1.00, 1.50] (m)
W = 0.3     # W ε [0.30, 0.40] (m)
D = 0.15    # D ε [0.15, 0.25] (m)
d = 0.02    # d ε [0.02, 0.03] (m)
min_y = -L/10

m = 0.01    # (kg)
kn = 1e5    # (N/m)
kt = 2*kn   # (N/m)
g = 9.81    # m/s^2
N = 1000

# Parametros de la simulacion

dt = 0.1 * sqrt(m/kn)
max_step = 1e5

# 0      W
# |      |  L
# |      |
# |__  __|  0
#    D

# ---------------------------------------------------------

# Modelo de la fuerza sobre una particula
# Rt: [[r0x,r0y], ..., [rnx,rny]]
# Vt: [[v0x,v0y], ..., [vnx,vny]]
# i: Particula sobre la que se calcula la fuerza

def f(Rt, Vt, i):
    if Rt.shape != (N,2) or Vt.shape != (N,2):
        raise Exception("Dimension of given position/velocity matrix is invalid")

    force = np.array([0, -m*g])

    # Calculamos la fuerza debido al contacto con otras particulas

    neighbours = get_neighbours(Rt, Vt, N, i, 2*d)
    for j in neighbours:
        zeta_ij = 2*d - norm(Rt[j] - Rt[i])
        v_rel = Vt[i] - Vt[j]

        en = (Rt[j] - Rt[i]) / norm(Rt[j] - Rt[i])
        et = np.array([-en[1], en[0]])

        fn = -kn * zeta_ij
        ft = -kt * zeta_ij * np.inner(v_rel, et)

        fx = fn * en[0] - ft * en[1]
        fy = fn * en[1] + ft * en[0]

        force += np.array([fx, fy])

    # Calculamos la fuerza debido al contacto con las paredes

    walls = [
        {   # Pared izquierda
            'collides': (Rt[i][0] - 0) <= d and Rt[i][1] >=0,
            'en': np.array([-1, 0]),  'zeta_ip': d - (Rt[i][0] - 0)
        },
        {   # Pared derecha
            'collides': (W - Rt[i][0]) <= d and Rt[i][1] >=0, 
            'en': np.array([1, 0]),   'zeta_ip': d - (W - Rt[i][0])
        },
        {   # Pared abajo
            'collides': (Rt[i][1] - 0) <= d and (Rt[i][0] <= (W-D)/2 or Rt[i][0] >= (W+D)/2),
            'en': np.array([0, -1]),  'zeta_ip': d - (Rt[i][1] - 0)
        }
    ]

    for wall in walls:
        if wall['collides']:
            zeta_ip = wall['zeta_ip']
            v_rel = Vt[i]

            en = wall['en']
            et = np.array([-en[1], en[0]])

            fn = -kn * zeta_ip
            ft = -kt * zeta_ip * np.inner(v_rel, et)

            fx = fn * en[0] - ft * en[1]
            fy = fn * en[1] + ft * en[0]

            force += np.array([fx, fy])

    return force

# Algoritmo de integracion
# R0: [[r0x,r0y], ..., [rnx,rny]]
# V0: [[v0x,v0y], ..., [vnx,vny]]

def beeman(R0, V0):
    # Creamos las matrices de posiciones y velocidades

    R = np.zeros(max_step, N, 2)
    V = np.zeros(max_step, N, 2)

    # Inicializamos el problema con las condiciones iniciales

    if R0.shape != (N,2) or V0.shape != (N,2):
        raise Exception("Dimension of given position/velocity matrix is invalid")

    R[0] = R0
    V[0] = V0
    step = 0

    # Iteramos hasta el maximo paso

    while step < max_step:

        # Integramos la trayectoria de cada particula
        
        for i in range(N):
            # Chequeamos si la particula llego al limite inferior
            if R[step][i][1] <= min_y:
                R[step+1][i] = np.array([R[step][i][0], L])
                V[step+1][i] = np.array([0, 0])
                step += 1
                continue

            # Calculamos la aceleracion en el paso actual
            a_t = f(R[step][i], V[step][i]) / m

            # Calculamos la aceleracion en el paso anterior
            if step > 0:
                a_t_less_dt = f(R[step-1][i], V[step-1][i]) / m
            else:
                v_prev = V[0][i] - dt * f(R[0][i], V[0][i]) / m
                r_prev = R[0][i] - dt*v_prev - (dt**2) * f(R[0][i], V[0][i]) / (2*m)
                a_t_less_dt = f(r_prev, v_prev) / m

            # Obtenemos la proxima posicion
            R[step+1][i] = R[step][i] + V[step][i]*dt + 2/3 * a_t * dt**2 - 1/6 * a_t_less_dt * dt**2

            # Predecimos la proxima velocidad
            vp = V[step][i] + 3/2 * a_t * dt - 1/2 * a_t_less_dt * dt

            # Evaluamos la proxima aceleracion
            a_t_plus_dt = f(R[step+1][i], vp) / m

            # Corregimos la proxima velocidad
            V[step+1][i] = V[step][i] + 1/3 * a_t_plus_dt * dt + 5/6 * a_t * dt - 1/6 * a_t_less_dt * dt

            # Avanzamos el tiempo de la simulacion
            step += 1
    
    return R,V