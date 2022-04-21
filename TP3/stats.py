import matplotlib.pyplot as plt
#from scipy.stats import skew
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

# print('Collision count: {}'.format(collision_count))
# print('Simulation time: {}'.format(simulation_time))
# print('Average collision time: {}'.format(simulation_time/collision_count))

# # Punto b: Distribucion de tiempos de colisiones
# times = list()
# for group, data in df.groupby('t'):
#     if (group == 0):
#         continue
#     # get first row from data
#     row = data.iloc[0]
#     times.append(row['d'])

# plt.hist(times, bins = 100)
# plt.show()


#-----------------------------------------------------------------------------
#                                  Punto 2
#-----------------------------------------------------------------------------

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

# tf = 10 # tiempo final de lectura

# big_x = df.loc[(df['id'] == 0) & (df['t'] < tf), 'x']
# big_y = df.loc[(df['id'] == 0) & (df['t'] < tf), 'y']

# fig, ax = plt.subplots(figsize=(10, 10))
# ax.plot(big_x, big_y)
# ax.plot([3], [3], 'ro') # punto inicial
# ax.set_xlim(0, 6)
# ax.set_ylim(0, 6)
# plt.grid()
# plt.tight_layout()
# plt.show()