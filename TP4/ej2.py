from math import hypot, sqrt
from matplotlib import animation, pyplot
import numpy as np
import math
import random

# ---------------------------------------------------------------
#                   CONDICIONES DEL PROBLEMA
# ---------------------------------------------------------------

# Parámetros físicos
N = 16**2
D = 1e-8
M = 1e-27
Q = 1e-19
K = 1e10
L = (sqrt(N)-1)*D

absortion_distance = 0.01*D
particles_x = np.linspace(D, L+D, int(sqrt(N)), endpoint=True)
particles_y = np.linspace(0, L, int(sqrt(N)), endpoint=True)

stop_reason = ''

# ---------------------------------------------------------------
#                         SIMULACION
# ---------------------------------------------------------------

def f(ri, v):
    qsign = 1
    force = np.array([0.0, 0.0])
    for px in particles_x:
        for py in particles_y:
            rij = ri - [px, py]
            rij_norm = np.linalg.norm(rij)

            if rij_norm < absortion_distance:
                return None

            force += K * Q**2 * (qsign / rij_norm**2) * rij/rij_norm
            qsign *= -1
        qsign *= -1
    return force


def potential_energy(qsign, r):
    p_qsign, u = 1, 0
    for px in particles_x:
        for py in particles_y:
            if px==r[0] and py==r[1]:
                continue
            u += K * Q**2 * qsign * p_qsign / hypot(px-r[0], py-r[1])
            p_qsign *= -1
        p_qsign *= -1
    return u


def should_stop(r, step):
    global stop_reason

    if step == 0:
        return False

    if step*dt > tf:
        stop_reason = 'timeout'
        return True
    
    # Check if particle is inside valid coordinates

    r_t = r[step]
    if r_t[0]<0: 
      stop_reason = 'exited_left'
      return True
    elif r_t[0]>L: 
      stop_reason = 'exited_right'
      return True
    elif r_t[1]<0: 
      stop_reason = 'exited_top'
      return True
    elif r_t[1]>L:
      stop_reason = 'exited_bottom'
      return True

    # Check if particle left the grid after entering

    r_t = r[step-1]
    was_inside_grid = r_t[0]>D and r_t[0]<L+D and r_t[1]>0 and r_t[1]<L
    
    r_t = r[step]
    is_inside_grid = r_t[0]>D and r_t[0]<L+D and r_t[1]>0 and r_t[1]<L

    if was_inside_grid and not is_inside_grid:
        stop_reason = 'exited_left'
        return True


def verlett(r0, v0, ovito=False):
    global stop_reason
    # print('--- Verlett ---')

    # Inicializamos el problema
    r = [np.array(r0)]
    v = [np.array(v0)]
    step = 0

    # print(f'{step} - R: {r[step]} - V: {v[step]} - F: {f(r[step], v[step])}')

    # Se evalua la velocidad inicial y la posición inicial
    v.append( v[0] + dt*f(r[0], v[0])/M )
    r.append( r[0] + dt*v[1] + (dt**2)*f(r[0], v[0])/(2*M) )

    step = 1
    t = step*dt

    while not should_stop(r, step):
        force = f(r[step], v[step])
        if force is None:
            stop_reason = 'particle_absorbed'
            break
        
        # if step % log_step == 0:
        #     print(f'{step} - R: {r[step]} - V: {v[step]} - F: {force}')
            # print(f'{step} - R: {r[step]} - V: {round(np.linalg.norm(v[step]), 2)} - A: {round(np.linalg.norm(f(r[step], v[step]))/M, 2)}')

        # Obtenemos la próxima posición
        r.append( 2 * r[step] - r[step-1] + dt**2 * force/M )

        # Obtenemos la próxima velocidad
        v.append( (r[step+1]-r[step-1])/(2*dt) )

        # Avanzamos el tiempo de la simulacion
        step += 1
        t = step*dt

    if ovito:
        with open('verlett.xyz', 'w') as file:
            for i in range(len(r)):
                file.write(f'{N+4+1}\n\n')
                file.write('0 0 0 1e-15 255 255 255\n')
                file.write('0 0 {} 1e-15 255 255 255\n'.format(L))
                file.write('0 {} 0 1e-15 255 255 255\n'.format(L))
                file.write('0 {} {} 1e-15 255 255 255\n'.format(L, L))
                
                color = 0
                for j in range(N):
                    x = (j%16)*D + D
                    y = (math.floor(j/16))*D
                    file.write('{} {} {} 2e-9 {} 0 0\n'.format(j+1, x, y, (j+1+math.floor(j/16))%2))
                
                file.write('{} {} {} 2e-9 0 1 0\n'.format(N+1, r[i][0], r[i][1]))

    # print(stop_reason)
    return r,v


def gear(r0, v0):
    global stop_reason
    # print('--- Gear ---')

    # Inicializamos el problema

    r = [np.array(r0)]
    r1 = [np.array(v0)]

    # Expresiones parciales de las derivadas superiores

    d = lambda x, y, xj, yj : (x-xj)**2 + (y-yj)**2

    r2p = lambda t, x, y, xj, yj : K/M * Q**2 / d(x,y,xj,yj)**(3/2) * np.array([x-xj, y-yj])

    r3p = lambda t, x, y, xj, yj : (-3 * K/M * Q**2 / d(x,y,xj,yj)**(5/2) * np.array([(x-xj)**2, (y-yj)**2]) + K/M * Q**2 / d(x,y,xj,yj)**(3/2) * np.array([1, 1])) * (r1[int(t/dt)])

    r4p = lambda t, x, y, xj, yj : (15 * K/M * Q**2 / d(x,y,xj,yj)**(7/2) * np.array([(x-xj)**3, (y-yj)**3]) - 9 * K/M * Q**2 / d(x,y,xj,yj)**(5/2) * np.array([x-xj, y-yj])) * (r1[int(t/dt)])**2

    r5p = lambda t, x, y, xj, yj : (-105 * K/M * Q**2 / d(x,y,xj,yj)**(9/2) * np.array([(x-xj)**4, (y-yj)**4]) + 90 * K/M * Q**2 / d(x,y,xj,yj)**(7/2) * np.array([(x-xj)**2, (y-yj)**2]) - 9 * K/M * Q**2 / d(x,y,xj,yj)**(5/2) * np.array([1, 1])) * (r1[int(t/dt)])**3

    #  Expresiones completas de las derivadas superiores

    sign = [[1,-1],[-1,1]]
    iterable = zip(range(len(particles_x)), range(len(particles_y)))

    r2 = lambda t : sum( sign[i%2][j%2] * r2p(t, r[int(t/dt)][0], r[int(t/dt)][1], particles_x[i], particles_y[j]) for i,j in iterable)
    r3 = lambda t : sum( sign[i%2][j%2] * r3p(t, r[int(t/dt)][0], r[int(t/dt)][1], particles_x[i], particles_y[j]) for i,j in iterable)
    r4 = lambda t : sum( sign[i%2][j%2] * r4p(t, r[int(t/dt)][0], r[int(t/dt)][1], particles_x[i], particles_y[j]) for i,j in iterable)
    r5 = lambda t : sum( sign[i%2][j%2] * r5p(t, r[int(t/dt)][0], r[int(t/dt)][1], particles_x[i], particles_y[j]) for i,j in iterable)

    # Coeficientes del predictor de Gear de orden 5
    alpha = [3/16, 251/360, 1, 11/18, 1/6, 1/60]
    
    step = 0
    t = step*dt

    # Comenzamos a iterar

    while not should_stop(r, step):
        # Predecimos la posición, velocidad y aceleración
        r_p = r[step] + r1[step] * dt + r2(t) * dt**2/2 + r3(t) * dt**3/6 + r4(t) * dt**4/24 + r5(t) * dt**5/120
        r1_p = r1[step] + r2(t) * dt + r3(t) * dt**2/2 + r4(t) * dt**3/6 + r5(t) * dt**4/24
        r2_p = r2(t) + r3(t) * dt + r4(t) * dt**2/2 + r5(t) * dt**3/6

        # Calculamos la fuerza y nos fijamos que la partícula no haya sido absorbida
        force = f(r_p, r1_p)
        if force is None:
            stop_reason = 'particle_absorbed'
            break

        # Evaluamos la aceleración y la comparamos con la predecida
        a = force / M
        DR2 = (a - r2_p) * dt**2 / 2

        # Corregimos la posición y velocidad y las guardamos
        r.append( r_p + alpha[0] * DR2 )
        r1.append( r1_p + alpha[1] * DR2 * 1/dt )

        # Avanzamos el tiempo de la simulación
        step += 1
        t = step*dt

    # print(stop_reason)
    return r, r1

# Calculate potential energy between fixed particles (constant)

qsign, ff_pot_energy = 1, 0
for px in particles_x:
    for py in particles_y:

        ff_pot_energy += potential_energy(qsign, [px, py])
        
        qsign *= -1
    qsign *= -1

# ---------------------------------------------------------------
#                         ANALISIS
# ---------------------------------------------------------------

def plot_force():
    ex, ey, fx, fy = [], [], [], []
    for x in np.arange(0, L+D, D/4):
        for y in np.arange(0, L, D/4):
            print(x,y)
            if x%D != 0 or y%D !=0:
                ex.append(x)
                ey.append(y)
                force = f(np.array([x,y]), None)
                force /= np.linalg.norm(force)
                fx.append(force[0])
                fy.append(force[1])
    
    px, py, c = [], [], []
    qsign = 1
    for x in np.linspace(D, L+D, int(sqrt(N)), endpoint=True):
        for y in np.linspace(0, L, int(sqrt(N)), endpoint=True):
            px.append(x)
            py.append(y)
            c.append('r' if qsign==1 else 'b')
            qsign *= -1
        qsign *= -1

    pyplot.quiver(ex, ey, fx, fy)
    pyplot.scatter(px, py, c=c)
    pyplot.show()


def update_plot(i, rs, scat):
    scat.set_offsets([[rs[i][0], rs[i][1]]])
    return scat


def animate(r0, v0, fun):
    rs, vs = fun(v0, r0)

    x, y, c = [], [], []
    qsign = 1
    for px in particles_x:
        for py in particles_y:
            x.append(px)
            y.append(py)
            c.append('r' if qsign==1 else 'b')
            qsign *= -1
        qsign *= -1

    fig = pyplot.figure()
    pyplot.scatter(x, y, c=c)
    scat = pyplot.scatter([rs[0][0]], [rs[0][1]], c=['g'])

    ani = animation.FuncAnimation(fig, update_plot, frames=range(len(rs)), fargs=(rs, scat))
    # pyplot.show()

    ani.save('out/animation.gif', fps=8*(1e-13/dt))


def energy_check(r0, v0, fun):
    rs, vs = fun(v0, r0)

    up, uk, maxdiff = [], [], 0
    for i in range(len(rs)):
        r, v = rs[i], vs[i]
        up.append( potential_energy(r) )
        uk.append( 0.5 * M * (v[0]**2+v[1]**2) )
        maxdiff = max(maxdiff, abs(up[-1]+uk[-1] - (up[0]+uk[0])))
    print(f'Max energy variation: {maxdiff/(up[0]+uk[0])*100}%')
    
    times = np.arange(0, len(rs), 1)
    pyplot.plot(times, up, label='Potential')
    pyplot.plot(times, uk, label='Kinetic')

    pyplot.legend()
    pyplot.show()


# ---------------------------------------------------------------
#                         GRAFICOS
# ---------------------------------------------------------------

def energy_average_vs_dt_plot(v0, fun):

    y_values = [random.uniform(L/2 - D/2, L/2 + D/2) for _ in range(5)]
    
    values = list()
    stds = list()

    # dts = [1e-11, 1e-12, 1e-13, 1e-14, 1e-15, 1e-16, 1e-17, 1e-18]
    dts = [1e-11, 1e-12, 1e-13, 1e-14]

    global dt
    for _dt in dts:
        dt = _dt
        energies = list()
        for y_value in y_values:
            print(f'Dt: {dt} - Y: {y_value}')
            rs, vs = fun(r0=[0, y_value], v0=v0)

            initial_energy = 2 * potential_energy(qsign=1, r=rs[0]) + ff_pot_energy + 0.5 * M * math.hypot(vs[0][0], vs[0][1])**2
            final_energy = 2 * potential_energy(qsign=1, r=rs[-1]) + ff_pot_energy + 0.5 * M * math.hypot(vs[-1][0], vs[-1][1])**2

            # initial_energy = potential_energy(qsign=1, r=rs[0]) + 0.5 * M * math.hypot(vs[0][0], vs[0][1])**2
            # final_energy = potential_energy(qsign=1, r=rs[-1]) + 0.5 * M * math.hypot(vs[-1][0], vs[-1][1])**2

            # print("Initial energy: {}, Final energy: {}, Energy difference: {}".format(initial_energy, final_energy, abs(initial_energy - final_energy)/initial_energy))
            energies.append(abs(initial_energy - final_energy)/initial_energy)

        values.append(np.mean(energies))
        stds.append(np.std(energies))

    pyplot.errorbar(dts, values, yerr=stds, fmt='o')
    pyplot.xlabel('dt (s)')
    pyplot.ylabel('Energía total promedio')
    pyplot.xscale('log')
    pyplot.yscale('log')
    pyplot.show()


def energy_variation_vs_t_plot(v0, fun):
    global  dt
    y = L/2

    for _dt in [0.5e-12, 1e-13, 1e-14, 1e-15]:
        dt = _dt
        print(f'Running dt={dt}')
        
        rs, vs = fun(r0=[0, y], v0=v0)

        # Calculamos la energía inicial

        k_0 = 0.5 * M * (vs[0][0]**2+vs[0][1]**2)
        ue_0 = 2 * potential_energy(qsign=1, r=rs[0]) + ff_pot_energy
        u_0 = k_0 + ue_0

        print(f'K0: {k_0} - UE0: {ue_0} - U0: {u_0}')

        u_var = []
        for i in range(1, len(rs)):
            r_t, v_t = rs[i], vs[i]

            k_t = 0.5 * M * (v_t[0]**2+v_t[1]**2)
            ue_t = 2 * potential_energy(qsign=1, r=r_t) + ff_pot_energy
            u_t = k_t + ue_t

            u_var.append(abs(u_t - u_0) / u_0 * 100)
        
        times = np.array(range(1, len(rs))) * dt
        pyplot.plot(times, u_var, label=f'dt={dt}')
    
    pyplot.xlabel('Tiempo (s)')
    pyplot.ylabel('Variacion de Et (%)')
    pyplot.yscale('log')
    pyplot.legend()
    pyplot.show()


def trajectory_vs_v0_plot(fun):
    y_values = [random.uniform(0, L) for _ in range(25)]
    # v_values = [5e3+11250*i for i in range(5)]
    v_values = [5e3, 10e3, 15e3, 20e3, 25e3, 30e3, 35e3, 40e3, 45e3, 50e3]
    avg_long = list()
    std = list()

    for v_value in v_values:
        trajectories = list()
        for y_value in y_values:
            print(f'V0: {v_value} - Y0: {y_value}')
            r, v = fun(r0=[0, y_value], v0=[v_value, 0])

            # calculo la longitud de la trayectoria de la particula
            aux = 0
            for i in range(len(r)):
                if i == 0:
                    continue
                aux += math.dist([r[i-1][0], r[i-1][1]], [r[i][0], r[i][1]])
            trajectories.append(aux)

        # obtengo el promedio de la longitud de las trayectorias
        avg_long.append(np.mean(trajectories))

        std.append(np.std(trajectories))

    print(std)
    
    # plot graph with standard deviation bars for trajectories
    pyplot.errorbar(v_values, avg_long, yerr=std, fmt='o')
    pyplot.ticklabel_format(style='sci', axis='x', scilimits=(0,0))
    pyplot.xlabel('Velocidad (m/s)')
    pyplot.ylabel('Longitud de trayectoria (m)')
    pyplot.show()


def absortion_escape_plot(fun):
    global stop_reason

    V0 = [5e3, 10e3, 15e3, 20e3, 25e3, 30e3, 35e3, 40e3, 45e3, 50e3]
    Y0 = np.linspace(L/2-D, L/2+D, 25, endpoint=True)

    samples = len(Y0)
    p_abs, p_exited, p_left, p_right, p_top, p_bottom = [], [], [], [], [], []

    for v0 in V0:
        absorbed, exited, left, right, top, bottom = 0, 0, 0, 0, 0, 0

        for y0 in Y0:
            print(f'V0: {v0} - Y0: {y0}')

            r,v = fun(r0=[0, y0], v0=[v0, 0])

            if 'particle_absorbed' == stop_reason:
                absorbed += 1
            else:
                exited += 1
                if "exited_left" == stop_reason:
                    left += 1
                if "exited_right" == stop_reason:
                    right += 1
                if "exited_top" == stop_reason:
                    top += 1
                if "exited_bottom" == stop_reason:
                    bottom += 1
        
        p_abs.append(absorbed / samples * 100)
        p_exited.append(exited / samples * 100)
        p_left.append(left / samples * 100)
        p_right.append(right / samples * 100)
        p_top.append(top / samples * 100)
        p_bottom.append(bottom / samples * 100)
        print(absorbed)

    pyplot.scatter(V0, p_abs, label='Absorbidas')
    pyplot.scatter(V0, p_exited, label='Escapadas')
    # pyplot.scatter(V0, p_left, label='Escapadas (izquierda)')
    # pyplot.scatter(V0, p_right, label='Escapadas (derecha)')
    # pyplot.scatter(V0, p_top, label='Escapadas (arriba)')
    # pyplot.scatter(V0, p_bottom, label='Escapadas (abajo)')

    pyplot.legend()
    pyplot.ticklabel_format(style='sci', axis='x', scilimits=(0,0))
    pyplot.xlabel('V0 (m/s)')
    pyplot.ylabel('Proporción (%)')
    pyplot.show()


def trajectory_pdf_plot(fun):
    global stop_reason

    V0 = [40e3, 45e3, 50e3]
    # V0 = [5e3, 10e3, 15e3]
    Y0 = np.linspace(L/2-D, L/2+D, 25, endpoint=True)

    # Trajectory
    for v0 in V0:
        trajectory_lengths = []

        for y0 in Y0:
            print(f'V0: {v0} - Y0: {y0}')
            
            r,v = fun(r0=[0, y0], v0=[v0, 0])

            if stop_reason == 'particle_absorbed':
                length = 0
                for i in range(1, len(r)):
                    length += math.dist([r[i-1][0], r[i-1][1]], [r[i][0], r[i][1]])
                trajectory_lengths.append(length)
        
        print(trajectory_lengths)
        pyplot.hist(trajectory_lengths, density = True, histtype="step", label=f'V0={"{:.1e} m/s".format(v0)}')
    
    pyplot.legend()
    pyplot.ticklabel_format(style='sci', axis='x', scilimits=(0,0))
    
    pyplot.xlabel('Longitud de la trayectoria (m)')
    pyplot.ylabel('Densidad de probabilidad')

    pyplot.yscale('log')

    pyplot.show()


# ---------------------------------------------------------------
#                         EJECUCIÓN
# ---------------------------------------------------------------

# Parámetros de simulación
dt = 1e-14
log_step = 10
tf = np.Infinity
# tf = 2e-12


if __name__ == '__main__':
    # animate(r0=[0, L/3], v0=[5e3, 0], fun=verlett)

    # energy_average_vs_dt_plot(v0=[5e4, 0], fun=gear)

    energy_variation_vs_t_plot(v0=[5e4, 0], fun=gear)

    # print(ff_pot_energy)

    # absortion_escape_plot(fun=verlett)

    # trajectory_pdf_plot(fun=verlett)