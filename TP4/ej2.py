from math import ceil, exp, cos, hypot, sqrt
from matplotlib import pyplot
import numpy as np

# ---------------------------------------------------------------
#                   CONDICIONES DEL PROBLEMA
# ---------------------------------------------------------------

# Parametros de la simulacion
dt = 1e-5
log_step = 5
tf = 5e5*dt

# Parametros del sistema
N = 16**2
D = 1e-8
M = 1e-27
Q = 1e-19
k = 1e10
L = (sqrt(N)-1)*D

# Condiciones iniciales
v0 = np.array([5e3, 0])
r0 = np.array([-D, L/2])

# Expresion de la fuerza
def f(r, v):
    qsign = 1
    force = np.array([0,0])
    for px in np.arange(0, L+D, D):
        for py in np.arange(0, L+D, D):
            if hypot(px-r[0], py-r[1]) < 0.01*D:
                raise Exception('Particle absorbed')
            force[0] += k * Q**2 * qsign / (px-r[0])**2 * np.sign(r[0]-px)
            force[1] += k * Q**2 * qsign / (py-r[1])**2 * np.sign(r[1]-py)
            qsign *= -1
        qsign *= -1
    return force

# Expresion de la energia potencial
def potential_energy(r):
    qsign, u = 1, 0
    for px in np.arange(0, L+D, D):
        for py in np.arange(0, L+D, D):
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
    was_inside_grid = r_t[0]>0 and r_t[0]<L and r_t[1]>0 and r_t[1]<L
    
    r_t = r[step]
    is_inside_grid = r_t[0]>0 and r_t[0]<L and r_t[1]>0 and r_t[1]<L

    return was_inside_grid and not is_inside_grid


def beeman():
    # Inicializamos el problema
    r = [r0]
    v = [v0]
    step = 0
    t = step*dt

    while not should_stop(r, step):
        a_t = f(r[step], v[step])/M
        a_t_less_dt = f(r[step-1], v[step-1])/M if step>0 else f(r[0]-v[0]*dt, v[0])

        # Obtenemos la proxima posicion
        r.append( r[step] + v[step]*dt + 2/3 * a_t * dt**2 - 1/6 * a_t_less_dt * dt**2 )

        # Predecimos la proxima velocidad
        vp = v[step] + 3/2 * a_t * dt - 1/2 * a_t_less_dt * dt

        # Evaluamos la proxima aceleracion
        a_t_plus_dt = f(r[step+1], vp)/M

        # Corregimos la proxima velocidad
        v.append( v[step] + 1/3 * a_t_plus_dt * dt + 5/6 * a_t * dt - 1/6 * a_t_less_dt * dt )

        # Avanzamos el tiempo de la simulacion
        step += 1
        t = step*dt
    
    return r,v

def energy_check():
    rs, vs = beeman()

    ups, uks, u = [], [], None
    for i in range(len(rs)):
        r, v = rs[i], vs[i]
        up = potential_energy(r)
        uk = 0.5 * M * (v[0]**2+v[1]**2)
        ups.append(up)
        uks.append(uk)
        
        if u!=None and up+uk-u>1e-18:
            return False
        u = up+uk
    
    times = np.arange(0, len(rs)*dt, dt)
    pyplot.plot(times, ups, label='Potential')
    pyplot.plot(times, uks, label='Kinetic')
    # pyplot.plot(times, np.array(ups)+np.array(uks), label='Total')

    pyplot.legend()
    pyplot.show()
    
    return True
    
# print(energy_check())