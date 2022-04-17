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

tf = 10 # tiempo final de lectura

big_x = df.loc[(df['id'] == 0) & (df['t'] < tf), 'x']
big_y = df.loc[(df['id'] == 0) & (df['t'] < tf), 'y']

fig, ax = plt.subplots(figsize=(10, 10))
ax.plot(big_x, big_y)
ax.plot([3], [3], 'ro') # punto inicial
ax.set_xlim(0, 6)
ax.set_ylim(0, 6)
plt.grid()
plt.tight_layout()
plt.show()