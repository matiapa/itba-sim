import matplotlib.pyplot as plt
from scipy.stats import skew, linregress
import numpy as np
import pandas as pd
import math

# Stats file
stats_file = open('stats.txt', 'r')

fist_line = stats_file.readline().rstrip().split(' ')
collision_count = int(fist_line[0])
simulation_time = float(fist_line[1])

# Se lee el archivo a partir de la segunda fila como un archivo csv
df = pd.read_csv(stats_file, skiprows=[1])

#-----------------------------------------------------------------------------
#                                  Punto 1
#-----------------------------------------------------------------------------

def punto_1():
    # Punto a: Frecuencia de colisiones

    print('Collision count: {}'.format(collision_count))
    print('Simulation time: {}'.format(simulation_time))
    print('Average collision time: {}'.format(simulation_time/collision_count))

    # Punto b: Distribucion de tiempos de colisiones
    times = list()
    for group, data in df.groupby('t'):
        if (group == 0):
            continue
        # get first row from data
        row = data.iloc[0]
        times.append(row['d'])

    plt.hist(times, bins = 100)
    plt.show()


#-----------------------------------------------------------------------------
#                                  Punto 2
#-----------------------------------------------------------------------------

def punto_2():
    init_speeds = list()
    speeds = list()

    for index, row in df.iterrows():
        if (row['t'] == 0 and row['id'] != 0):
            speed = math.hypot(row['vx'], row['vy'])
            init_speeds.append(speed)

        if (row['t'] > (simulation_time * 2/3) and row['id'] != 0):
            speed = math.hypot(row['vx'], row['vy'])
            speeds.append(speed)

    fig, axes = plt.subplots(2)

    # Punto a: Distribucion de modulos de velocidades
    axes[0].hist(init_speeds, bins = 25, density = True)
    axes[0].set_xlabel('|V| [m/s]')
    axes[0].set_ylabel('Densidad de probabilidad')
    axes[0].set_title('|V| para t=0 con N=140')

    # Punto b: Distribucion de modulos de velocidades
    axes[1].hist(speeds, bins = 25, density = True)
    axes[1].set_xlabel('|V| [m/s]')
    axes[1].set_ylabel('Densidad de probabilidad')
    axes[1].set_title('|V| para t>2/3*T con N=140, T=10s')

    fig.tight_layout()
    plt.show()


#-----------------------------------------------------------------------------
#                                  Punto 3
#-----------------------------------------------------------------------------

# Falta meterle cambio de temperatura
def punto_3():
    big_particle_file = open('big_particle.csv', 'r')
    df_big_particle = pd.read_csv(big_particle_file)
    tf = 70 # tiempo final de lectura

    amount = len(df_big_particle.groupby('K'))
    print(amount)

    for group, data in df_big_particle.groupby('K'):
        fig, ax = plt.subplots(figsize=(10,10))
        big_x = data.loc[(data['id'] == 0) & (data['t'] < tf), 'x']
        big_y = data.loc[(data['id'] == 0) & (data['t'] < tf), 'y']

        ax.plot(big_x, big_y)
        ax.plot([3], [3], 'ro') # punto inicial
        ax.set_xlim(0, 6)
        ax.set_ylim(0, 6)

        ax.set_xlabel('x [m]')
        ax.set_ylabel('y [m]')
        ax.set_title('Trayectoria de la particula grande para Ek = {} J'.format(group))

        plt.grid()
        plt.tight_layout()
        plt.savefig('big_particle_K={}.png'.format(group))
        # plt.show()


#-----------------------------------------------------------------------------
#                                  Punto 4
#-----------------------------------------------------------------------------
def punto_4():
    df = pd.read_csv('dcm.csv')
    max_t = max(df['t'])
    print(max_t)

    fig, ax = plt.subplots(2)

    ax[0].errorbar(x = df['t'], y = df['b_avg'], yerr = df['b_dev'], fmt='o ')
    ax[0].set_xlabel('t (s)')
    ax[0].set_ylabel('DCM '+r'($\mathregular{m^2}$)')
    ax[0].set_title('Particula grande (N=140, S=100)')
    
    r = linregress(df['t'], df['b_avg'])
    ax[0].plot([0, max_t], [r.intercept, r.intercept + r.slope * max_t], marker='o', label=f'D={round(r.slope/2, 2)}' + r'$\mathregular{m^2/s}$')
    ax[0].legend()

    ax[1].errorbar(x = df['t'], y = df['s_avg'], yerr = df['s_dev'], fmt='o ')
    ax[1].set_xlabel('t (s)')
    ax[1].set_ylabel('DCM '+r'($\mathregular{m^2}$)')
    ax[1].set_title('Particulas chicas (N=140, S=100)')

    r = linregress(df['t'], df['s_avg'])
    ax[1].plot([0, max_t], [r.intercept, r.intercept + r.slope * max_t], marker='o', label=f'D={round(r.slope/2, 2)}' + r'$\mathregular{m^2/s}$')
    ax[1].legend()

    fig.tight_layout()
    plt.show()

    plt.errorbar(x = [100, 110, 120, 130], y = [0.05, 0.02, 0.05, 0.05], yerr = [0.01, 0.004, 0.012, 0.017], fmt='o ', label='Particula grande')
    plt.errorbar(x = [100, 110, 120, 130], y = [0.16, 0.12, 0.11, 0.09], yerr = [0.021, 0.014, 0.009, 0.02], fmt='o ', label='Particulas chicas')
    plt.xlabel('Numero de particulas')
    plt.ylabel('Coeficiente de difusion '+r'($\mathregular{m^2/s}$)')
    plt.legend()
    plt.show()

punto_4()