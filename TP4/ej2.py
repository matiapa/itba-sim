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
k = 1e10
L = (sqrt(N)-1)*D
absortion_distance = 0.01*D

stop_reason = ''

# ---------------------------------------------------------------
#                         SIMULACION
# ---------------------------------------------------------------

def f(ri, v):
    qsign = 1
    force = np.array([0.0, 0.0])
    for px in np.linspace(D, L+D, int(sqrt(N)), endpoint=True):
        for py in np.linspace(0, L, int(sqrt(N)), endpoint=True):
            rij = ri - [px, py]
            rij_norm = np.linalg.norm(rij)

            if rij_norm < absortion_distance:
                return None

            force += k * Q**2 * (qsign / rij_norm**2) * rij/rij_norm
            qsign *= -1
        qsign *= -1
    return force

def potential_energy(r):
    qsign, u = 1, 0
    for px in np.linspace(D, L+D, int(sqrt(N)), endpoint=True):
        for py in np.linspace(0, L, int(sqrt(N)), endpoint=True):
            u += k * Q**2 * qsign / hypot(px-r[0], py-r[1])
            qsign *= -1
        qsign *= -1
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
        
        if step % log_step == 0:
            print(f'{step} - R: {r[step]} - V: {v[step]} - F: {force}')
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


def animate(r0, v0):
    rs, vs = verlett(v0, r0)

    x, y, c = [], [], []
    qsign = 1
    for px in np.linspace(D, L+D, int(sqrt(N)), endpoint=True):
        for py in np.linspace(0, L, int(sqrt(N)), endpoint=True):
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


def energy_check(r0, v0):
    rs, vs = verlett(v0, r0)

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

def energy_plot(v0):
    global  dt

    for _dt in [0.5e-12, 1e-13, 1e-14, 1e-15]:
        u_dt = []
        for y in [L/2]:
        # for y in np.linspace(L/2-D, L/2+D, 5, True):
            dt = _dt

            print(f'Running dt={dt}, y={y}')
            rs, vs = verlett(r0=[0, y], v0=v0)

            u0 = potential_energy(rs[0]) + 0.5 * M * (vs[0][0]**2+vs[0][1]**2)

            for i in range(len(rs)):
                r, v = rs[i], vs[i]
                if len(u_dt)<=i:
                    u_dt.append(0)
                u_dt[i] += abs(potential_energy(r) + 0.5 * M * (v[0]**2+v[1]**2) - u0) * 100 / (u0 * len(rs))
        
        times = np.arange(0, len(rs), 1)*dt
        pyplot.plot(times[1:], u_dt[1:], label=f'dt={dt}')
    
    pyplot.xlabel('Tiempo (s)')
    pyplot.ylabel('Variacion de Et (%)')
    pyplot.yscale('log')
    pyplot.legend()
    pyplot.show()


def trajectory_vs_dt_plot():
    y_values = [random.uniform(0, L) for _ in range(25)]
    # v_values = [5e3+11250*i for i in range(5)]
    v_values = [5e3, 10e3, 15e3, 20e3, 25e3, 30e3, 35e3, 40e3, 45e3, 50e3]
    avg_long = list()
    std = list()

    for v_value in v_values:
        trajectories = list()
        for y_value in y_values:
            print(f'V0: {v_value} - Y0: {y_value}')
            r, v = verlett(r0=[0, y_value], v0=[v_value, 0])

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


def absortion_escape_plot():
    global stop_reason

    V0 = [40e3, 45e3, 50e3]
    # V0 = [5e3, 10e3, 15e3, 20e3, 25e3, 30e3, 35e3, 40e3, 45e3, 50e3]
    Y0 = np.linspace(L/2-D, L/2+D, 25, endpoint=True)

    samples = len(Y0)
    p_abs, p_exited, p_left, p_right, p_top, p_bottom = [], [], [], [], [], []

    for v0 in V0:
        absorbed, exited, left, right, top, bottom = 0, 0, 0, 0, 0, 0

        for y0 in Y0:
            print(f'V0: {v0} - Y0: {y0}')

            r,v = verlett(r0=[0, y0], v0=[v0, 0])

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


def trajectory_pdf_plot():
    global stop_reason

    V0 = [40e3, 45e3, 50e3]
    Y0 = np.linspace(L/2-D, L/2+D, 100, endpoint=True)

    # trajectory
    for v0 in V0:
        trajectory_lengths = []

        for y0 in Y0:
            print(f'V0: {v0} - Y0: {y0}')
            
            r,v = verlett(r0=[0, y0], v0=[v0, 0])

            if stop_reason == 'particle_absorbed':
                length = 0
                for i in range(1, len(r)):
                    length += math.dist([r[i-1][0], r[i-1][1]], [r[i][0], r[i][1]])
                trajectory_lengths.append(length)
        
        print(trajectory_lengths)
        pyplot.hist(trajectory_lengths, density = True, histtype="step", label=f'V0={"{:.1e}".format(v0)}')
    
    pyplot.legend()
    pyplot.ticklabel_format(style='sci', axis='x', scilimits=(0,0))
    
    pyplot.xlabel('Longitud de la trayectoria (m)')
    pyplot.ylabel('Densidad de probabilidad')

    # pyplot.xscale('log')
    pyplot.yscale('log')

    pyplot.show()


# ---------------------------------------------------------------
#                         EJECUCIÓN
# ---------------------------------------------------------------

# Parámetros de simulación
dt = 1e-13
log_step = 10
# tf = np.Infinity
tf = 2e-12

if __name__ == '__main__':
    energy_plot(v0=[5e4, 0])

    # animate(r0=[0, L/3], v0=[5e3, 0])

    # trajectory_pdf_plot()