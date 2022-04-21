import matplotlib.pyplot as plt
from scipy.stats import skew
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


#-----------------------------------------------------------------------------
#                                  Punto 3
#-----------------------------------------------------------------------------

# Falta meterle cambio de temperatura
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