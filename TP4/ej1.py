from cProfile import label
from math import exp, cos, sqrt
from matplotlib import pyplot
import numpy as np

# Caracteristicas
tf = 5
m = 70
k = pow(10,4)
gamma = 100
A = 1

# Resoluciones temporales
dt = 1e-5
log_step = 5

# Condiciones iniciales
r0 = 1
v0 = -A*gamma/(2*m)

# Expresion de la fuerza
f = lambda r,v : - k * r - gamma * v

def verlet():
    # Inicializacion de condiciones
    r = np.arange(0, tf, dt).tolist()
    v = np.arange(0, tf, dt).tolist()

    r[0] = r0
    v[0] = v0
    step = 0

    # Ejecutamos el primer paso
    r_prev = r[0] - dt * v[0]
    force = f(r[0], v[0])

    r[1] = 2*r[0] - r_prev + (step * dt)**2/m * force

    step += 1
    t = step * dt

    # Ejecutamos los demas pasos
    while t < tf-dt:
        # Calculamos la proxima posicion
        r[step+1] = 2*r[step] - r[step-1] + t**2/m * f(r[step], v[step-1])

        # Calculamos la velocidad actual
        v[step] = (r[step+1] - r[step-1])/(2*dt)

        print(f't: {t}')
        print(f'r(t):')

        # Avanzamos el tiempo
        step += 1
        t = step * dt

    return r,v


def compare():
    times = np.arange(0, tf, dt).tolist()
    r_sim, _ = verlet()
    r_real = [A * exp(-t*gamma/(2*m)) * cos(sqrt(k/m - gamma**2/(4*m**2)) * t) for t in times]

    # pyplot.plot(times, r_sim, label='Simulated')
    # pyplot.plot(times, r_real, label='Analytic')
    pyplot.plot(times, [x-y for x,y in zip(r_sim,r_real)], label='Error')
    
    pyplot.legend()
    pyplot.show()

compare()