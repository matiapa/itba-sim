from math import ceil, exp, cos, hypot, sqrt
from matplotlib import animation, pyplot
import numpy as np

# ---------------------------------------------------------------
#                   CONDICIONES DEL PROBLEMA
# ---------------------------------------------------------------

# Parametros de la simulacion
dt = 1e-13
tf = 1e3*dt
log_step = 1

# Parametros del sistema
N = 16**2
D = 1e-8
M = 1e-27
Q = 1e-19
k = 1e10
L = (sqrt(N)-1)*D

# Condiciones iniciales
v0 = np.array([10e3, 0])
r0 = np.array([0, L/2])

# Expresion de la fuerza
def f(ri, v):
    qsign = 1
    force = np.array([0.0,0.0])
    for px in np.linspace(D, L+D, int(sqrt(N)), endpoint=True):
        for py in np.linspace(0, L, int(sqrt(N)), endpoint=True):
            rij = ri - [px, py]
            rij_norm = np.linalg.norm(rij)

            if rij_norm < 0.01*D:
                raise Exception('Particle absorbed')

            force += k * Q**2 * (qsign / rij_norm**2) * rij/rij_norm
            qsign *= -1
        qsign *= -1
    return force

# Expresion de la energia potencial
def potential_energy(r):
    qsign, u = 1, 0
    for px in np.linspace(D, L+D, int(sqrt(N)), endpoint=True):
        for py in np.linspace(0, L, int(sqrt(N)), endpoint=True):
            u += k * Q**2 * qsign / hypot(px-r[0], py-r[1])
            qsign *= -1
        qsign *= -1
    return u


def should_stop(r, step):
    if step == 0:
        return False
    if step > tf/dt:
        return True

    r_t = r[step-1]
    was_inside_grid = r_t[0]>D and r_t[0]<L+D and r_t[1]>0 and r_t[1]<L
    
    r_t = r[step]
    is_inside_grid = r_t[0]>D and r_t[0]<L+D and r_t[1]>0 and r_t[1]<L

    if was_inside_grid and not is_inside_grid:
        print('Cut condition activated')

    return was_inside_grid and not is_inside_grid


def verlett():
    # Inicializamos el problema
    r = [r0]
    v = [v0]
    step = 0

    print(f'{step} - R: {r[step]} - V: {v[step]} - F: {f(r[step], v[step])}')

    # Se evalua la velocidad inicial y la posición inicial
    v.append( v[0] + dt*f(r[0], v[0])/M )
    r.append( r[0] + dt*v[1] + (dt**2)*f(r[0], v[0])/(2*M) )

    step = 1
    t = step*dt

    while not should_stop(r, step):
        if step % log_step == 0:
            print(f'{step} - R: {r[step]} - V: {v[step]} - F: {f(r[step], v[step])}')

        # Obtenemos la próxima posición
        r.append( 2 * r[step] - r[step-1] + dt**2 * f(r[step], v[step])/M )

        # Obtenemos la próxima velocidad
        v.append( (r[step+1]-r[step-1])/(2*dt) )

        # Avanzamos el tiempo de la simulacion
        step += 1
        t = step*dt

    return r,v


def energy_check():
    rs, vs = verlett()

    up, uk, maxdiff = [], [], 0
    for i in range(len(rs)):
        r, v = rs[i], vs[i]
        up.append( potential_energy(r) )
        uk.append( 0.5 * M * (v[0]**2+v[1]**2) )
        maxdiff = max(maxdiff, abs(up[-1]+uk[-1] - (up[0]+uk[0])))
    print(f'Max energy variation: {maxdiff/(up[0]+uk[0])*100}%')
    
    times = np.arange(0, len(rs), 1) * dt
    # pyplot.plot(times, up, label='Potential')
    # pyplot.plot(times, uk, label='Kinetic')
    pyplot.plot(times, np.array(up)+np.array(uk), label='Total')

    pyplot.yscale('log')
    pyplot.legend()
    pyplot.show()


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
        # print(x,y,force[0],force[1])
    
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

def animate():
    rs, vs = verlett()

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
    pyplot.show()

    ani.save('out/animation.gif')


# plot_force()
# energy_check()
animate()