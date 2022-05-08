from math import exp, cos, sqrt
from matplotlib import pyplot
from scipy.stats import skew, linregress
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
k = 10000
gamma = 100
A = 1

# Condiciones iniciales
r0 = 1
v0 = -A*gamma/(2*m)

# Expresion de la fuerza
f = lambda r,v : -k * r - gamma * v

# ---------------------------------------------------------------
#                   ALGORITMO DE VERLETT
# ---------------------------------------------------------------


def verlett():
    # Vectores de posicion y velocidad
    r = np.arange(0, tf, dt).tolist()
    v = np.arange(0, tf, dt).tolist()
    
    # Inicializamos el problema
    r[0] = r0
    v[0] = v0
    step = 0

    # Se evalua la velocidad inicial y la posición inicial
    v[1] = v[0] + dt*f(r[0], v[0])/m
    r[1] = r[0] + dt*v[1] + (dt**2)*f(r[0], v[0])/(2*m)

    step += 1
    t = step*dt

    while t < tf-dt:
        # Obtenemos la próxima posición
        r[step+1] = 2 * r[step] - r[step-1] + dt**2 * f(r[step], v[step])/m

        # Obtenemos la próxima velocidad
        v[step+1] = (r[step+1]-r[step-1])/(2*dt)

        # Avanzamos el tiempo de la simulacion
        step += 1
        t = step*dt

    return r,v

# ---------------------------------------------------------------
#                   ALGORITMO DE BEEMAN
# ---------------------------------------------------------------

def beeman():
    # Vectores de posicion y velocidad
    r = np.arange(0, tf, dt).tolist()
    v = np.arange(0, tf, dt).tolist()
    
    # Inicializamos el problema
    r[0] = r0
    v[0] = v0
    step = 0
    t = step*dt

    while t < tf-dt:
        a_t = f(r[step], v[step])/m
        a_t_less_dt = f(r[step-1], v[step-1])/m if step>0 else f(r[0]-v[0]*dt, v[0])

        # Obtenemos la proxima posicion
        r[step+1] = r[step] + v[step]*dt + 2/3 * a_t * dt**2 - 1/6 * a_t_less_dt * dt**2

        # Predecimos la proxima velocidad
        vp = v[step] + 3/2 * a_t * dt - 1/2 * a_t_less_dt * dt

        # Evaluamos la proxima aceleracion
        a_t_plus_dt = f(r[step+1], vp)/m

        # Corregimos la proxima velocidad
        v[step+1] = v[step] + 1/3 * a_t_plus_dt * dt + 5/6 * a_t * dt - 1/6 * a_t_less_dt * dt

        # Avanzamos el tiempo de la simulacion
        step += 1
        t = step*dt
    
    return r,v


# ---------------------------------------------------------------
#                   ALGORITMO DE GEAR
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

def single_dt_analysis():
    # Ejecucion de los algoritmos

    times = np.arange(0, tf, dt).tolist()
    r_verlett, _ = verlett()
    r_gear, _ = gear()
    r_beeman, _ = beeman()
    r_real = [A * exp(-t*gamma/(2*m)) * cos(sqrt(k/m - gamma**2/(4*m**2)) * t) for t in times]

    # Errores cuadraticos medios

    print('ECM Verlett', sum([(x-y)**2 for x,y in zip(r_verlett, r_real)]) / (tf/dt))
    print('ECM Beeman', sum([(x-y)**2 for x,y in zip(r_beeman, r_real)]) / (tf/dt))
    print('ECM Gear', sum([(x-y)**2 for x,y in zip(r_gear, r_real)]) / (tf/dt))

    # Graficos de las posiciones

    pyplot.plot(times, r_verlett, label='Verlett')
    pyplot.plot(times, r_beeman, label='Beeman')
    pyplot.plot(times, r_gear, label='Gear')
    pyplot.plot(times, r_real, label='Analitico')
    pyplot.xlabel('t (s)')
    pyplot.ylabel('r (m)')
    pyplot.legend(loc='best')
    pyplot.show()

    # Grafico de los errores de las posiciones

    pyplot.plot(times, [x-y for x,y in zip(r_verlett, r_real)], label='Verlett')
    pyplot.plot(times, [x-y for x,y in zip(r_beeman, r_real)], label='Beeman')
    pyplot.plot(times, [x-y for x,y in zip(r_gear, r_real)], label='Gear')
    pyplot.legend()
    pyplot.xlabel('t (s)')
    pyplot.ylabel('Error de r (m)')
    pyplot.show()


def multi_dt_analysis():
    global dt

    # Grafico de los ECM vs dt
    dts = [1e-2, 5e-2, 1e-3, 5e-3, 1e-4, 5e-4, 1e-5]
    verlett_ecms = []
    beeman_ecms = []
    gear_ecms = []

    for _dt in dts:
        dt = _dt

        # Ejecucion de los algoritmos

        times = np.arange(0, tf, dt).tolist()
        r_verlett, _ = verlett()
        r_beeman, _ = beeman()
        r_gear, _ = gear()
        r_real = [A * exp(-t*gamma/(2*m)) * cos(sqrt(k/m - gamma**2/(4*m**2)) * t) for t in times]

        # Errores cuadraticos medios

        verlett_ecms.append(sum([(x-y)**2 for x,y in zip(r_verlett, r_real)]) / (tf/dt))
        beeman_ecms.append(sum([(x-y)**2 for x,y in zip(r_beeman, r_real)]) / (tf/dt))
        gear_ecms.append(sum([(x-y)**2 for x,y in zip(r_gear, r_real)]) / (tf/dt))

        print('DT', dt)

    pyplot.scatter(dts, verlett_ecms, label='Verlett', marker='o')
    pyplot.scatter(dts, beeman_ecms, label='Beeman', marker='o')
    pyplot.scatter(dts, gear_ecms, label='Gear', marker='o')

    pyplot.xlabel('dt (s)')
    pyplot.ylabel('ECM ' + r'($\mathregular{m^2}$)')
    pyplot.xscale('log')
    pyplot.yscale('log')
    pyplot.legend()

    pyplot.show()

single_dt_analysis()
multi_dt_analysis()
