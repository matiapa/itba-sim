import math
from math import hypot, sqrt, ceil
from cim import get_neighbours

import numpy as np
from tqdm import tqdm

# ---------------------------------------------------------

neighbours = []

def neighbours_at(Rt, D, step):
    zy_size, zy_count = 2*max_rad, ceil((L+min_y)/(2*max_rad))
    zx_size, zx_count = 2*max_rad, ceil(W/(2*max_rad))

    # if step == None:
    return get_neighbours(Rt, D, zy_size, zx_size, zy_count, zx_count, 0)

    # if step == len(neighbours):
    #     neighbours.append( get_neighbours(Rt, D, zy_size, zx_size, zy_count, zx_count, 0) )

    # return neighbours[step]


def reinsert_particle(Rt, Vt, D, i):
    cant = 0
    while True:
        x = np.random.uniform(D[i], W-D[i])
        y = np.random.uniform(L/2 + min_y - D[i], L + min_y -D[i])
        p = lambda j : hypot(x-Rt[j][0], y-Rt[j][1]) > D[i]+D[j]
        cant += 1
        if cant % 100 == 0:
            print("Stuck: ", cant)
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
                'collides': (L + min_y - Rt[i][1]) <= D[i],
                'en': np.array([0, 1]),  'zeta_ip': D[i] - (L + min_y - Rt[i][1])
            },
            {   # Pared abajo
                'collides': (Rt[i][1] - min_y) <= D[i] and (Rt[i][0] <= (W-Ap)/2 or Rt[i][0] >= (W+Ap)/2),
                'en': np.array([0, -1]),  'zeta_ip': D[i] - (Rt[i][1] - min_y)
            }
        ]

        for wall in walls:
            if wall['collides']:
                zeta_ip = wall['zeta_ip']
                if (zeta_ip < 0):
                    print("Less than 0")
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

out_file, anim_file, insert_file = "", "", ""


def beeman(R0, V0, D):

    global out_file, anim_file, insert_file

    out_file = open('out_N{}_Ap{}_tf{}.csv'.format(len(R0), Ap, tf), 'w')
    anim_file = open('out_N{}_Ap{}_tf{}.xyz'.format(len(R0), Ap, tf), 'w')
    insert_file = open('insertions_N{}_Ap{}_tf{}.csv'.format(len(R0), Ap, tf), 'w')
    
    global sc_neighbours, sc_part_forces
    # Creamos las matrices de posiciones y velocidades

    steps = int(tf/dt)
    N = len(R0)
    R = np.zeros((3, N, 2))
    V = np.zeros((3, N, 2))
    A = np.zeros((3, N, 2))
    I = []

    # Inicializamos el problema con las condiciones iniciales

    R[1] = R0
    V[1] = V0
    step = 0

    # Iteramos hasta el maximo paso

    undesired_reinsertions = 0

    print("Starting simulation - Steps: ", steps)

    for step in tqdm(range(steps-1)):            
    # for step in range(steps-1):            
        # --- Calculo de la proxima posicion ---

        # Calculamos las aceleraciones en el paso actual
        A[1] = f(R[1], V[1], D, 1) / m

        # Obtenemos las aceleraciones del paso anterior
        if step > 0:
            a_t_less_dt = A[0]
        else:
            v_prev = V[1] - dt * A[1]
            r_prev = R[1] - dt*v_prev - (dt**2) * A[1]/2
            a_t_less_dt = f(r_prev, v_prev, D, None) / m

        # Obtenemos las proximas posiciones
        R[2] = R[1] + V[1]*dt + 2/3 * A[1] * dt**2 - 1/6 * a_t_less_dt * dt**2

        # Reinsertamos las particulas que hayan escapado
        reinserted_particles = []
        for i in range(N):
            if R[2][i][1] <= 0 or (R[2][i][1] > 0 and R[2][i][1] < min_y and (R[2][i][0] > W or R[2][i][0] < 0)):
                reinsert_particle(R[2], V[2], D, i)
                reinserted_particles.append(i)
            elif R[2][i][1] > L + min_y or (R[2][i][0] < 0 and R[2][i][1] > 0) or R[2][i][0] > W:
                reinserted_particles.append(i)
                reinsert_particle(R[2], V[2], D, i)
                print("Undesired Reinsertion")
                undesired_reinsertions += 1

        # --- Calculo de la proxima velocidad ---

        # Predecimos las proximas velocidades
        vp = V[1] + 3/2 * A[1] * dt - 1/2 * a_t_less_dt * dt

        # Predecimos las proximas aceleraciones
        a_t_plus_dt = f(R[2], vp, D, 2) / m

        # Recalculamos las proximas velocidades
        V[2] = V[1] + 1/3 * a_t_plus_dt * dt + 5/6 * A[1] * dt - 1/6 * a_t_less_dt * dt

        for i in reinserted_particles:
            A[1][i] = np.array([0, -g])
            V[2][i] = np.array([0, 0])

        I.append(len(reinserted_particles))
        # print("Inserted: ",len(reinserted_particles))

        # if step % 1000 == 0:
        #     print("Step: ", int((step/steps)*100), "%")

        # Avanzamos el tiempo de la simulacion
        if step % anim_step == 0:
            write_ovito_line(R, V, D)
        write_csv_line(R, V, D, step)

        R[0] = R[1]
        R[1] = R[2]
        V[0] = V[1]
        V[1] = V[2]
        A[0] = A[1]

        step += 1

    print(f'Undesired reinsertions: {undesired_reinsertions}')
    out_file.close()
    anim_file.close()

    return R,V,I

# ---------------------------------------------------------

def random_init(N):
    R0, D = [], []

    # Randomly create N particles
    
    while len(R0) < N:
        for _ in range(N):
            d = np.random.uniform(min_rad, max_rad)
            D.append(d)
            x, y = np.random.uniform(d, W-d), np.random.uniform(min_y + d, L + min_y - d)
            R0.append(np.array([x,y]))

        # Remove particles that are overlapping

        R0_n, D_n, avoid = [], [], set()

        zy_size, zy_count = 0.06, ceil((L + min_y)/0.06)
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
min_y = L/10
W = 0.4     # W ε [0.30, 0.40] (m)
m = 0.01    # (kg)
kn = 10**5  # (N/m)
g = 9.81    # m/s^2

min_rad = 0.01
max_rad = 0.015

# Parametros variables

Ap = 0.15  # Ap ε [0.15, 0.25] (m)
kt = 2*kn
tf = 2
dt = 0.1 * sqrt(m/kn)
N = 150

fps = 48*4
anim_step = int((1/fps) / dt)

# ---------------------------------------------------------

def write_ovito_line(R, V, D):
    anim_file.write(f'{N+6}\n\n')
    anim_file.write(f'{N+1} 0 0 0 0 1e-15 255 255 255\n')
    anim_file.write(f'{N+1} 0 {L+min_y} 0 0 1e-15 255 255 255\n')
    anim_file.write(f'{N+1} {W} 0 0 0 1e-15 255 255 255\n')
    anim_file.write(f'{N+1} {W} {L+min_y} 0 0 1e-15 255 255 255\n')
    anim_file.write(f'{N+1} {(W-Ap)/2} {min_y} 0 0 0.01 255 0 0\n')
    anim_file.write(f'{N+1} {(W+Ap)/2} {min_y} 0 0 0.01 255 0 0\n')
    for i in range(N):
        anim_file.write(f'{i} {R[1][i][0]} {R[1][i][1]} {V[1][i][0]} {V[1][i][1]} {D[i]} 0 0 0\n')

def write_csv_line(R, V, D, s):
    for i in range(N):
        out_file.write(f'{s},{i},{R[1][i][0]},{R[1][i][1]},{V[1][i][0]},{V[1][i][1]}\n')

def write_insertions_file(I):
    insert_file.write('t,cant\n')
    for i in range(len(I)):
        if I[i] != 0:
            insert_file.write(f'{i*dt},{I[i]}\n')


def file_generation(R, V, D, I=None, animate=True):

    out_file.write('t,id,x,y,vx,vy\n')    
    for s in range(len(R)):

        for i in range(N):
            out_file.write(f'{s},{i},{R[s][i][0]},{R[s][i][1]},{V[s][i][0]},{V[s][i][1]}\n')

        if s % anim_step == 0 and animate:
            anim_file.write(f'{N+6}\n\n')
            anim_file.write(f'{N+1} 0 0 0 0 1e-15 255 255 255\n')
            anim_file.write(f'{N+1} 0 {L+min_y} 0 0 1e-15 255 255 255\n')
            anim_file.write(f'{N+1} {W} 0 0 0 1e-15 255 255 255\n')
            anim_file.write(f'{N+1} {W} {L+min_y} 0 0 1e-15 255 255 255\n')
            anim_file.write(f'{N+1} {(W-Ap)/2} {min_y} 0 0 0.01 255 0 0\n')
            anim_file.write(f'{N+1} {(W+Ap)/2} {min_y} 0 0 0.01 255 0 0\n')
            
            for i in range(N):
                anim_file.write(f'{i} {R[s][i][0]} {R[s][i][1]} {V[s][i][0]} {V[s][i][1]} {D[i]} 0 0 0\n')
    
    if not I is None:
        insert_file.write('t,cant\n')
        for i in range(len(I)):
            if I[i] != 0:
                insert_file.write(f'{i*dt},{I[i]}\n')

def simulate(animate=True):
    # Corremos la simulacion

    R0, D = random_init(N)
    V0 = np.zeros_like(R0)
    R,V,I = beeman(R0, V0, D)

    # Guardamos los archivos de output

    print('Saving files...')
    file_generation(R, V, D)
    

def run(w, l, ap, n):
    global W, L, Ap, N
    W = w
    L = l
    Ap = ap
    N = n
    R0, D = random_init(n)
    V0 = np.zeros_like(R0)
    R,V,I = beeman(R0, V0, D)

    write_insertions_file(I)

    # print('Saving files...')
    # file_generation(R, V, D, I)

    return R,V,D

if __name__ == '__main__':
    simulate()

