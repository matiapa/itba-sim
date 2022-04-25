import matplotlib.pyplot as plt
from scipy.stats import skew, linregress
import numpy as np
import pandas as pd
import math


plt.rcParams.update({'font.size': 18})

#-----------------------------------------------------------------------------
#                                  Punto 1
#-----------------------------------------------------------------------------

def analyze_file(file_name):
    collision_file = open(file_name, 'r')
    fist_line = collision_file.readline().rstrip().split(' ')
    collision_count = int(fist_line[0])
    simulation_time = float(fist_line[1])
    avg_collision_time = simulation_time/collision_count

    print('Collision count: {}'.format(collision_count))
    print('Simulation time: {}'.format(simulation_time))
    print('Average collision time: {}'.format(avg_collision_time*1000))

    df_collision = pd.read_csv(collision_file)
    p, x = np.histogram(df_collision['d'], bins=100)
    # x = [i*1000 for i in x]
    p = [float(i)/(collision_count*(x[1]-x[0])) for i in p]

    return x, p, avg_collision_time

def punto_1():
    
    x, p, avg_x = analyze_file('collision_time_110.csv')
    avg_y = np.interp(avg_x, x[:-1], p)
    plt.plot(x[:-1], p, label='N=110')
    plt.plot(avg_x, avg_y, 'bo')


    x, p, avg_x = analyze_file('collision_time_125.csv')
    avg_y = np.interp(avg_x, x[:-1], p)
    plt.plot(x[:-1], p, label='N=125')
    plt.plot(avg_x, avg_y,'yo')


    x, p, avg_x = analyze_file('collision_time_140.csv')
    avg_y = np.interp(avg_x, x[:-1], p)
    plt.plot(x[:-1], p, label='N=140')
    plt.plot(avg_x, avg_y, 'go')

    plt.xlabel('Tiempo de colisión (s)')
    plt.ylabel('PDF')
    plt.ticklabel_format(axis="both", style="sci", scilimits=(0,0))
    plt.legend(loc='best')
    plt.grid()
    plt.tight_layout()

    # plt.yscale('log')
    # plt.xscale('log')
    plt.savefig('collision_time.png')

punto_1()

#-----------------------------------------------------------------------------
#                                  Punto 2
#-----------------------------------------------------------------------------

def punto_2():
    # Stats file
    stats_file = open('stats.txt', 'r')
    df = pd.read_csv(stats_file, skiprows=[1])

    # Punto a: Distribucion de modulos de velocidades
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
    fig1, ax1 = plt.subplots(figsize=(10,10))

    for group, data in df_big_particle.groupby('K'):
        big_x = data.loc[(data['id'] == 0) & (data['t'] < tf), 'x']
        big_y = data.loc[(data['id'] == 0) & (data['t'] < tf), 'y']

        ax1.plot(big_x, big_y, label="Ek = {} J".format(round(group, 1)))
        ax1.plot([3], [3], 'ro') # punto inicial
        ax1.set_xlim(0, 6)
        ax1.set_ylim(0, 6)


    ax1.set_xlabel('Partícula Grande X (m)')
    ax1.set_ylabel('Partícula Grande Y (m)')
    plt.grid()
    plt.legend(loc='best')
    plt.savefig('big_particle.png')
    # plt.show()

# punto_3()
#-----------------------------------------------------------------------------
#                                  Punto 4
#-----------------------------------------------------------------------------
def punto_4():
    df = pd.read_csv('dcm.csv')
    max_t = max(df['t'])
    print(max_t)

    fig, ax = plt.subplots(2)

    ax[0].set_xlabel('t (s)')
    ax[0].errorbar(x = df['t'], y = df['b_avg'], yerr = df['b_dev'], fmt='o ')
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
    plt.show()
    plt.legend()