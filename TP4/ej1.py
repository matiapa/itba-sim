from cProfile import label
from math import exp, cos, sqrt
from matplotlib import pyplot
import numpy as np

# ---------------------------------------------------------------
#                   CONDICIONES DEL PROBLEMA
# ---------------------------------------------------------------

# Resoluciones temporales
dt = 1e-5
log_step = 5

# Caracteristicas
tf = 5e5*dt
m = 70
k = pow(10,4)
gamma = 100
A = 1

# Condiciones iniciales
r0 = 1
v0 = -A*gamma/(2*m)

# Expresion de la fuerza
f = lambda r,v : -k * r - gamma * v

# ---------------------------------------------------------------
#                   ALGORITMO DE VERLET ORIGINAL
# ---------------------------------------------------------------

def verlet():
    # Inicializacion de condiciones
    r = np.arange(0, tf, dt).tolist()
    v = np.arange(0, tf, dt).tolist()

    r[0] = r0
    v[0] = v0
    step = 0

    # Ejecutamos el primer paso
    r_prev = r[0] - dt * v[0]
    r[1] = 2*r[0] - r_prev + (step * dt)**2/m * f(r[0], v[0])

    step += 1
    t = step * dt

    # Ejecutamos los demas pasos
    while t < tf-dt:
        # Calculamos la proxima posicion
        r[step+1] = 2*r[step] - r[step-1] + t**2/m * f(r[step], v[step-1])

        # Calculamos la velocidad actual
        v[step] = (r[step+1] - r[step-1])/(2*dt)

        print(f't: {t}')
        print(f'r(t): {r[step]}')
        print(f'v(t-1): {v[step-1]}')
        print(f'f(t): {f(r[step], v[step-1])}')
        print(f'r(t+1): {r[step+1]}')
        print('--------------------------')

        # Avanzamos el tiempo
        step += 1
        t = step * dt

    return r,v

# ---------------------------------------------------------------
#                   ALGORITMO DE GEAR ORDEN 5
# ---------------------------------------------------------------

def gear():
    # Vectores de posicion y velocidad
    r = np.arange(0, tf, dt).tolist()
    r1 = np.arange(0, tf, dt).tolist()

    # Expresiones de las derivadas superiores de la posicion
    r2 = lambda t : -k/m * r[int(t/dt)] - gamma/m * r1[int(t/dt)]
    r3 = lambda t : -k/m * r1[int(t/dt)] - gamma/m * r2(t)
    r4 = lambda t : -k/m * r2(t) - gamma/m * r3(t)
    r5 = lambda t : -k/m * r3(t) - gamma/m * r4(t)

    # Coeficientes del predictor de Gear de orden 5
    alpha = [3/16, 251/360, 1, 11/18, 1/6, 1/60]
    
    # Inicializamos el problema
    r[0] = r0
    r1[0] = v0
    step = 0
    t = step*dt

    while t < tf-dt:
        # Predecimos la posicion, velocidad y aceleracion
        r_p = r[step] + r1[step] * dt + r2(t) * dt**2/2 + r3(t) * dt**3/6 + r4(t) * dt**4/24 + r5(t) * dt**5/120
        r1_p = r1[step] + r2(t) * dt + r3(t) * dt**2/2 + r4(t) * dt**3/6 + r5(t) * dt**4/24
        r2_p = r2(t) + r3(t) * dt + r4(t) * dt**2/2 + r5(t) * dt**3/6

        # Evaluamos la aceleracion y la comparamos con la predecida
        a = f(r_p, r1_p) / m
        DR2 = (a - r2_p) * dt**2 / 2

        # Corregimos la posicion y velocidad y las guardamos
        r[step+1] = r_p + alpha[0] * DR2
        r1[step+1] = r1_p + alpha[1] * DR2 * 1/dt

        # Avanzamos el tiempo de la simulacion
        step += 1
        t = step*dt
    
    return r, r1

def compare():
    times = np.arange(0, tf, dt).tolist()
    # r_verlett, _ = verlet()
    r_gear, _ = gear()
    r_real = [A * exp(-t*gamma/(2*m)) * cos(sqrt(k/m - gamma**2/(4*m**2)) * t) for t in times]

    # pyplot.plot(times, r_verlett, label='Verlett')
    pyplot.plot(times, r_gear, label='Gear')
    # pyplot.plot(times, r_real, label='Analytic')
    # pyplot.plot(times, [x-y for x,y in zip(r_sim,r_real)], label='Error')
    
    pyplot.legend()
    pyplot.show()

compare()