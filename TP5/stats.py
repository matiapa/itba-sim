from matplotlib import pyplot as plt
import numpy as np
import pandas
from main import m, g, dt, simulate

def parse_file(filename):
    print('Reading file...')
    df = pandas.read_csv(filename)
    R, V = [], []

    print('Parsing file...')
    for _, t_df in df.groupby('t'):
        R.append( t_df[['x','y']].to_numpy() )
        V.append( t_df[['vx','vy']].to_numpy() )

    return R, V


def kinetic_energy_plot(filename):
    R, V = parse_file(filename)

    uk= []
    for s in range(len(R)):
        _, Vt = R[s], V[s]     
        uk.append( sum(0.5 * m * (Vt[:,0]**2+Vt[:,1]**2)) )
    
    times = np.arange(0, len(R)*dt, dt)
    plt.plot(times, uk)

    plt.xlabel('Time (s)')
    plt.ylabel('Kinetic Energy (J)')
    plt.yscale('Log')
    plt.show()

kinetic_energy_plot('out.csv')