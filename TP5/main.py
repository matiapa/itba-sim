import math
from math import hypot, sqrt, ceil
from cim import get_neighbours

import numpy as np
from tqdm import tqdm

# ---------------------------------------------------------

neighbours = []

def neighbours_at(Rt, D, step):
    zy_size, zy_count = 2*max_rad, ceil((L-min_y)/(2*max_rad))
    zx_size, zx_count = 2*max_rad, ceil(W/(2*max_rad))

    if step == None:
        return get_neighbours(Rt, D, zy_size, zx_size, zy_count, zx_count, 0)

    if step == len(neighbours):
        neighbours.append( get_neighbours(Rt, D, zy_size, zx_size, zy_count, zx_count, 0) )

    return neighbours[step]


def reinsert_particle(Rt, Vt, D, i):
    while True:
        x = np.random.uniform(D[i], W-D[i])
        y = np.random.uniform(L-6*D[i], L-D[i])
        p = lambda j : hypot(x-Rt[j][0], y-Rt[j][1]) > D[i]+D[j]
        if all([p(j) for j in range(len(Rt))]):
            break
    Rt[i] = np.array([x, y])
    Vt[i] = np.array([0, 0])

# ---------------------------------------------------------

# Modelo de la fuerza sobre una particula
# Rt: [[r0x,r0y], ..., [rnx,rny]]
# Vt: [[v0x,v0y], ..., [vnx,vny]]
# Returns [[f0x,f0y], ..., [fnx,fny]]

def f(Rt, Vt, D, step):
    N = len(Rt)
    forces = np.zeros((N,2))
    force_pairs = {}

    # Obtenemos todos los vecinos

    neighbours = neighbours_at(Rt, D, step)
    
    for i in range(N):
        # Sumamos la fuerza de la gravedad

        forces[i][1] += -m*g

        # Calculamos las fuerzas debido al contacto con otras particulas

        for j in neighbours[i]:
            if (j,i) in force_pairs:
                forces[i] += -force_pairs[(j,i)]
            else:
                zeta_ij = D[i] + D[j] - math.dist(Rt[j], Rt[i])
                v_rel = Vt[i] - Vt[j]

                en = (Rt[j] - Rt[i]) / math.dist(Rt[j], Rt[i])
                et = np.array([-en[1], en[0]])

                fn = -kn * zeta_ij
                ft = -kt * zeta_ij * np.inner(v_rel, et)

                fx = fn * en[0] - ft * en[1]
                fy = fn * en[1] + ft * en[0]

                # force_pairs[(i,j)] = np.array([fx, fy])
                forces[i] += np.array([fx, fy])

        # Calculamos las fuerzas debido al contacto con las paredes

        walls = [
            {   # Pared izquierda
                'collides': (Rt[i][0] - 0) <= D[i] and Rt[i][1] >=0,
                'en': np.array([-1, 0]),  'zeta_ip': D[i] - (Rt[i][0] - 0)
            },
            {   # Pared derecha
                'collides': (W - Rt[i][0]) <= D[i] and Rt[i][1] >=0, 
                'en': np.array([1, 0]),   'zeta_ip': D[i] - (W - Rt[i][0])
            },
            {   # Pared arriba
                'collides': (L - Rt[i][1]) <= D[i],
                'en': np.array([0, 1]),  'zeta_ip': D[i] - (L - Rt[i][1])
            },
            {   # Pared abajo
                'collides': (Rt[i][1] - 0) <= D[i] and (Rt[i][0] <= (W-Ap)/2 or Rt[i][0] >= (W+Ap)/2),
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

def beeman(R0, V0, D):
    global sc_neighbours, sc_part_forces
    # Creamos las matrices de posiciones y velocidades

    steps = int(tf/dt)
    N = len(R0)
    R = np.zeros((steps, N, 2))
    V = np.zeros((steps, N, 2))
    A = np.zeros((steps, N, 2))

    # Inicializamos el problema con las condiciones iniciales

    R[0] = R0
    V[0] = V0
    step = 0

    # Iteramos hasta el maximo paso

    undesired_reinsertions = 0

    for step in tqdm(range(steps-1)):            
        # --- Calculo de la proxima posicion ---

        # Calculamos las aceleraciones en el paso actual
        A[step] = f(R[step], V[step], D, step) / m

        # Obtenemos las aceleraciones del paso anterior
        if step > 0:
            a_t_less_dt = A[step-1]
        else:
            v_prev = V[0] - dt * A[step]
            r_prev = R[0] - dt*v_prev - (dt**2) * A[step]/2
            a_t_less_dt = f(r_prev, v_prev, D, None) / m

        # Obtenemos las proximas posiciones
        R[step+1] = R[step] + V[step]*dt + 2/3 * A[step] * dt**2 - 1/6 * a_t_less_dt * dt**2

        # Reinsertamos las particulas que hayan escapado
        
        for i in range(N):
            if R[step+1][i][1] <= min_y:
                reinsert_particle(R[step+1], V[step+1], D, i)
            if R[step+1][i][1] > L or R[step+1][i][0] < 0 or R[step+1][i][0] > W:
                reinsert_particle(R[step+1], V[step+1], D, i)
                undesired_reinsertions += 1

        # --- Calculo de la proxima velocidad ---

        # Predecimos las proximas velocidades
        vp = V[step] + 3/2 * A[step] * dt - 1/2 * a_t_less_dt * dt

        # Predecimos las proximas aceleraciones
        a_t_plus_dt = f(R[step+1], vp, D, step+1) / m

        # Recalculamos las proximas velocidades
        V[step+1] = V[step] + 1/3 * a_t_plus_dt * dt + 5/6 * A[step] * dt - 1/6 * a_t_less_dt * dt

        # Avanzamos el tiempo de la simulacion
        step += 1

    print(f'Undesired reinsertions: {undesired_reinsertions}')

    return R,V

# ---------------------------------------------------------

def random_init(N):
    R0, D = [], []

    # Randomly create N particles
    
    while len(R0) < N:
        for _ in range(N):
            d = np.random.uniform(min_rad, max_rad)
            D.append(d)
            x, y = np.random.uniform(d, W-d), np.random.uniform(d, L-d)
            R0.append(np.array([x,y]))

        # Remove particles that are overlapping

        R0_n, D_n, avoid = [], [], set()

        zy_size, zy_count = 0.06, ceil((L-min_y)/0.06)
        zx_size, zx_count = 0.06, ceil(W/0.06)
        neighbours = get_neighbours(R0, D, zy_size, zx_size, zy_count, zx_count, 0)

        for i in range(len(neighbours)):
            if i not in avoid:
                R0_n.append(R0[i])
                D_n.append(D[i])
            for j in neighbours[i]:
                avoid.add(j)

        R0, D = R0_n, D_n
    
    if len(R0) > N:
        R0 = R0[:N]
        D = D[:N]

    print(f'Created {len(R0)} particles')

    return R0, D

# ---------------------------------------------------------

# Parametros fijos

L = 1       # L ε [1.00, 1.50] (m)
W = 0.3     # W ε [0.30, 0.40] (m)
min_y = -L/10
m = 0.01    # (kg)
kn = 10**5  # (N/m)
g = 9.81    # m/s^2

min_rad = 0.005
max_rad = 0.015

# Parametros variables

Ap = 0.0
kt = 2*kn
tf = 1
dt = 0.1 * sqrt(m/kn)
N = 100

fps = 48*4
anim_step = int((1/fps) / dt)

# ---------------------------------------------------------

def simulate():
    # Corremos la simulacion

    R0, D = random_init(N)
    V0 = np.zeros_like(R0)
    R,V = beeman(R0, V0, D)

    # Guardamos los archivos de output

    print('Saving files...')
    
    out_file = open('out.csv', 'w')
    anim_file = open('out.xyz', 'w')

    out_file.write('t,id,x,y,vx,vy\n')    
    for s in range(len(R)):
        for i in range(N):
            out_file.write(f'{s},{i},{R[s][i][0]},{R[s][i][1]},{V[s][i][0]},{V[s][i][1]}\n')

        if s % anim_step == 0:
            anim_file.write(f'{N+6}\n\n')
            anim_file.write(f'{N+1} 0 {min_y} 0 0 1e-15 255 255 255\n')
            anim_file.write(f'{N+1} 0 {L} 0 0 1e-15 255 255 255\n')
            anim_file.write(f'{N+1} {W} {min_y} 0 0 1e-15 255 255 255\n')
            anim_file.write(f'{N+1} {W} {L} 0 0 1e-15 255 255 255\n')
            anim_file.write(f'{N+1} {(W-Ap)/2} 0 0 0 0.01 255 0 0\n')
            anim_file.write(f'{N+1} {(W+Ap)/2} 0 0 0 0.01 255 0 0\n')
            
            for i in range(N):
                anim_file.write(f'{i} {R[s][i][0]} {R[s][i][1]} {V[s][i][0]} {V[s][i][1]} {D[i]} 0 0 0\n')

if __name__ == '__main__':
    simulate()