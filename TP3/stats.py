import matplotlib.pyplot as plt
#from scipy.stats import skew
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

    i = 0

    for index, row in df.iterrows():
        if (row['t'] == 0 and row['id'] != 0):
            speed = math.hypot(row['vx'], row['vy'])
            init_speeds.append(speed)

        if (row['t'] > (simulation_time * 2/3) and row['id'] != 0):
            speed = math.hypot(row['vx'], row['vy'])
            speeds.append(speed)

        # i += 1
        # if i > 1000:
        #     break

    plt.hist(init_speeds, bins = 25, density = True)
    plt.xlabel('|V| [m/s]')
    plt.ylabel('Densidad de probabilidad')
    plt.title('Modulo de velocidad para t = 0')
    plt.show()

    plt.hist(speeds, bins = 25, density = True)
    plt.xlabel('|V| [m/s]')
    plt.ylabel('Densidad de probabilidad')
    plt.title('Modulo de velocidad para t > 2/3T')
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