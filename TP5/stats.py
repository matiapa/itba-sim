from audioop import avg
from matplotlib import pyplot as plt
import numpy as np
import pandas
from main import m, g, dt, simulate, run
import math

def parse_file(filename):
    print('Reading file...')
    df = pandas.read_csv(filename, sep=' ')
    R, V = [], []

    print('Parsing file...')
    for _, t_df in df.groupby('t'):
        R.append( t_df[['x','y']].to_numpy() )
        V.append( t_df[['vx','vy']].to_numpy() )

    return df['t'].unique(), R, V

def beverloo(N, c, i, x):
    np = N/0.3
    return np * math.sqrt(9.8) * (x[i] - (c * 0.0125))**1.5

def kinetic_energy_plot_file(filename, label):
    T, R, V = parse_file(filename)

    df1 = pandas.read_csv('ej1_0_15.csv')
    df2 = pandas.read_csv('ej1_0_18.csv')
    df3 = pandas.read_csv('ej1_0_21.csv')
    df4 = pandas.read_csv('ej1_0_24.csv')

    datas = [df1, df2, df3, df4]
    data_labels = [0.15, 0.18, 0.21, 0.24]

    #---------------- CALCULO DE CAUDAL -----------------

    dt_q = []
    dt_x = []

    for j in range(len(datas)):
        steps_count = datas[j]['Dt'].count()
        reinsertion_count = datas[j]['N'].sum()
        group_count = int(reinsertion_count*0.05)

        q = list()
        x = list()
        sum = 0
        last_t = 0
        i = 0

        for t, t_df in datas[j].groupby('Dt'):
            sum += t_df['N'].sum()
            if sum >= group_count:
                q.append(group_count/(t-last_t))
                x.append(last_t)
                sum -= group_count
                last_t = t
                i += 1

        dt_q.append(q)
        dt_x.append(x)
        plt.plot(x, q, label="Ap = "+str(data_labels[j])+" m", marker='o')

    plt.xlabel('Dt (s)')
    plt.ylabel('Caudal (1/s)')
    plt.legend()
    plt.show()

    #---------------- FIN CALCULO DE CAUDAL -----------------

    #---------------- CALCULO DE PROMEDIO -----------------
    
    plt.plot(T, uk, label=label)


def kinetic_energy_plot_kt():
    kinetic_energy_plot_file('out/kt1.csv', label='Kt=1*Kn')
    kinetic_energy_plot_file('out/kt2.csv', label='Kt=2*Kn')
    kinetic_energy_plot_file('out/kt3.csv', label='Kt=3*Kn')

    plt.xlabel('Time (s)')
    plt.ylabel('Kinetic Energy (J)')
    plt.yscale('Log')
    plt.legend()
    plt.show()


def kinetic_energy_plot_ap():
    kinetic_energy_plot_file('out/ap015.csv', label='Ap=0.15m')

    plt.xlabel('Time (s)')
    plt.ylabel('Kinetic Energy (J)')
    plt.yscale('Log')
    plt.legend()
    plt.show()


def avg_energy(filename, start, stop):
    R, V = parse_file(filename)

    uk= []
    for s in range(int(start/dt), int(stop/dt)):
        _, Vt = R[s], V[s]     
        uk.append( sum(0.5 * m * (Vt[:,0]**2+Vt[:,1]**2)) )
    
    return np.average(uk), np.std(uk)


def avg_energy_plot():
    avg_energy('out_N200_Ap0.0_tf0.5_kt1.csv', )


kinetic_energy_plot_ap()
