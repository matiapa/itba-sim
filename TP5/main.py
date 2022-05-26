from math import sqrt
from random import random
import numpy as np
from numpy.linalg import norm
from cim import get_neighbours

# ---------------------------------------------------------

# Parametros del sistema

L = 1       # L ε [1.00, 1.50] (m)
W = 0.3     # W ε [0.30, 0.40] (m)
A = 0.15    # A ε [0.15, 0.25] (m)
min_y = -L/10

m = 0.01    # (kg)
kn = 1e5    # (N/m)
kt = 2*kn   # (N/m)
g = 9.81    # m/s^2
N = 5

# Parametros de la simulacion

tf = 1
dt = 0.1 * sqrt(m/kn)
Zl, Zw = L/0.06, W/0.06

# Parametros de la animacion

fps = 48
anim_step = int((1/fps) / dt)

# 0      W
# |      |  L
# |      |
# |__  __|  0
#    A

# ---------------------------------------------------------

# Modelo de la fuerza sobre una particula
# Rt: [[r0x,r0y], ..., [rnx,rny]]
# Vt: [[v0x,v0y], ..., [vnx,vny]]
# Returns [[f0x,f0y], ..., [fnx,fny]]

def f(Rt, Vt, D):
    forces = np.tile(np.array([0, -m*g]), (N,1))

    # Calculamos las fuerzas debido al contacto con otras particulas

    neighbours = get_neighbours(Rt, D, L, W, Zl, Zw, 0)
    
    for i in range(N):
        for j in neighbours[i]:
            zeta_ij = 2*D[i] - norm(Rt[j] - Rt[i])
            v_rel = Vt[i] - Vt[j]

            en = (Rt[j] - Rt[i]) / norm(Rt[j] - Rt[i])
            et = np.array([-en[1], en[0]])

            fn = -kn * zeta_ij
            ft = -kt * zeta_ij * np.inner(v_rel, et)

            fx = fn * en[0] - ft * en[1]
            fy = fn * en[1] + ft * en[0]

            forces[i] += np.array([fx, fy])

    # Calculamos las fuerzas debido al contacto con las paredes

    for i in range(N):
        walls = [
            {   # Pared izquierda
                'collides': (Rt[i][0] - 0) <= D[i] and Rt[i][1] >=0,
                'en': np.array([-1, 0]),  'zeta_ip': D[i] - (Rt[i][0] - 0)
            },
            {   # Pared derecha
                'collides': (W - Rt[i][0]) <= D[i] and Rt[i][1] >=0, 
                'en': np.array([1, 0]),   'zeta_ip': D[i] - (W - Rt[i][0])
            },
            {   # Pared abajo
                'collides': (Rt[i][1] - 0) <= D[i] and (Rt[i][0] <= (W-A)/2 or Rt[i][0] >= (W+A)/2),
                'en': np.array([0, -1]),  'zeta_ip': D[i] - (Rt[i][1] - 0)
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

                forces[i] += np.array([fx, fy])

    return forces

# Algoritmo de integracion
# R0: [[r0x,r0y], ..., [rnx,rny]]
# V0: [[v0x,v0y], ..., [vnx,vny]]
# Returns [R0, ..., Rt], [V0, ..., Vt]

def beeman(R0, V0, D, ovito):
    # Creamos las matrices de posiciones y velocidades

    steps = int(tf/dt)
    R = np.zeros((steps, N, 2))
    V = np.zeros((steps, N, 2))

    # Inicializamos el problema con las condiciones iniciales

    R[0] = R0
    V[0] = V0
    step = 0

    # Iteramos hasta el maximo paso

    while step < steps - 1:
        
        # Reinsertamos las particulas que hayan escapado
        for i in range(N):
            if R[step][i][1] <= min_y:
                R[step][i] = np.array([R[step][i][0], L])
                V[step][i] = np.array([0, 0])

        # Calculamos las aceleraciones en el paso actual
        a_t = f(R[step], V[step], D) / m

        # Calculamos las aceleraciones en el paso anterior
        if step > 0:
            a_t_less_dt = f(R[step-1], V[step-1], D) / m
        else:
            v_prev = V[0] - dt * a_t
            r_prev = R[0] - dt*v_prev - (dt**2) * a_t/2
            a_t_less_dt = f(r_prev, v_prev, D) / m

        # Obtenemos las proximas posiciones
        R[step+1] = R[step] + V[step]*dt + 2/3 * a_t * dt**2 - 1/6 * a_t_less_dt * dt**2

        # Predecimos las proximas velocidades
        vp = V[step] + 3/2 * a_t * dt - 1/2 * a_t_less_dt * dt

        # Evaluamos las proximas aceleraciones
        a_t_plus_dt = f(R[step+1], vp, D) / m

        # Corregimos las proximas velocidades
        V[step+1] = V[step] + 1/3 * a_t_plus_dt * dt + 5/6 * a_t * dt - 1/6 * a_t_less_dt * dt

        # Avanzamos el tiempo de la simulacion
        step += 1

    # Guardamos el archivo de animacion
    
    if ovito:
        lb = min_y
        with open('out.xyz', 'w') as file:
            
            for i in range(0, steps, anim_step):
                file.write(f'{N+4}\n\n')
                file.write(f'0 0 {lb} 1e-15 255 255 255\n')
                file.write(f'0 0 {L} 1e-15 255 255 255\n')
                file.write(f'0 {W} {lb} 1e-15 255 255 255\n')
                file.write(f'0 {W} {L} 1e-15 255 255 255\n')
                
                for j in range(N):
                    file.write('{} {} {} {} 0 0 0\n'.format(j+1, R[i][j][0], R[i][j][1], D[j]))

    return R,V

# ---------------------------------------------------------

def random_init(N):
    R0 = []
    D = []

    # Randomly create N particles
    
    for _ in range(N):
        d = random()*0.01 + 0.02
        D.append(d)
        x, y = np.random.uniform(d, W-d), np.random.uniform(d, L-d)
        R0.append([x,y])

    # Remove particles that are overlapping

    neighbours = get_neighbours(R0, D, L, W, Zl, Zw, 0)

    for i in range(len(neighbours)):
        for j in neighbours[i]:
            del R0[j]
            del D[j]
            del neighbours[j][i]

    print(f'Created {N} particles')

    return R0, D, N


R0, D, N = random_init(N)
V0 = np.zeros((N,2))

R, V = beeman(R0, V0, D, True)
