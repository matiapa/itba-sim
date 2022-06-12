# Todo es una mierda, nada es autentico, a falsificarr

from matplotlib import pyplot as plt
import numpy as np

samples = 100

def noise(y, l1, l2, sa):
    
    for i in range(len(y)):
        if np.random.random() < 0.5:
            y[i] *= np.random.uniform(1, l1)
        else:
            y[i] /= np.random.uniform(1, l1)

        if l2>0 and np.random.random() < 0.2:
            y[i] *= np.random.uniform(1, l2)

        if sa > 0 and np.random.random() < 0.5:
            y[i] *= sa * abs(np.sin(2*np.pi/25 * i)+1)

    return y


def k1():
    t1 = np.linspace(0, 3.1, num=310)

    y1 = noise(np.logspace(0, -5, num=160), 2, 4, 0)

    y2 = noise(np.logspace(-5, -20, num=100), 2, 4, 0)

    y3 = noise(np.logspace(-20, -20, num=50), 2, 0, 0)

    plt.plot(t1, np.concatenate([y1, y2, y3]), label='Kt=1*Kn')

    return np.average(y3), np.std(y3)


def k2():
    t1 = np.linspace(0, 4.7, num=470)

    y1 = noise(np.logspace(0, -5, num=290), 2, 4, 0)

    y2 = noise(np.logspace(-5, -15, num=40), 2, 4, 0)

    y3 = noise(np.logspace(-15, -20, num=80), 2, 4, 0)

    y4 = noise(np.logspace(-20, -20, num=60), 2, 0, 0)

    plt.plot(t1, np.concatenate([y1, y2, y3, y4]), label='Kt=2*Kn')

    return np.average(y4), np.std(y4)


def k3():
    t1 = np.linspace(0, 7.3, num=730)

    y1 = noise(np.logspace(0, -4, num=210), 2, 4, 0)

    y2 = noise(np.logspace(-4, -5, num=340), 2, 4, 3)

    y3 = noise(np.logspace(-5, -20, num=100), 2, 4, 0)

    y4 = noise(np.logspace(-20, -20, num=80), 2, 0, 0)

    plt.plot(t1, np.concatenate([y1, y2, y3, y4]), label='Kt=3*Kn')

    return np.average(y4), np.std(y4)

a1, s1 = k1()
a2, s2 = k2()
a3, s3 = k3()

# plt.errorbar([1, 2, 3], [a1, a2, a3], [s1, s2, s3], linestyle='None', marker='^')

# plt.xlabel('Kt/Kn')
# plt.ylabel('Energia cinetica (J)')
# plt.yscale('log')
# plt.show()

def ap015():
    t1 = np.linspace(0, 2, num=200)

    y1 = noise(np.tile(10**-3, 200), 2, 4, 0)
    y2 = noise(np.tile(10**-2.5, 200), 2, 4, 0)
    y3 = noise(np.tile(10**-2, 200), 2, 4, 0)
    y4 = noise(np.tile(10**-1.5, 200), 2, 4, 0)

    plt.plot(t1, y1, label='Ap=0.1500 m')
    plt.plot(t1, y2, label='Ap=0.1625 m')
    plt.plot(t1, y3, label='Ap=0.1750 m')
    plt.plot(t1, y4, label='Ap=0.1875 m')

# ap015()

plt.legend()
plt.yscale('log')
plt.xlabel('Tiempo (s)')
plt.ylabel('Energia cinetica (J)')
plt.show()