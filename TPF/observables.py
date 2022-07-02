import pandas as pd
import numpy as np
import matplotlib.pyplot as plt

def obs1_pc(show=True):
    df = pd.read_csv('new_infected_pc.csv')
    
    y = []
    x = []
    err = []

    for group, data in df.groupby('pc'):

        p = []
        for group_s, data_s in data.groupby('s'):
            # get amount of iterrations
            t = data_s['t'].iloc[-1]
            max_t = t

            # get last new_infected from data
            last_r0 = (data_s['newInfected'].iloc[max_t]/data_s['infected'].iloc[max_t-1])
            first_r0 = (data_s['newInfected'].iloc[1]/data_s['infected'].iloc[0])   
            p.append((last_r0 - first_r0)/max_t)

        y.append(np.mean(p))
        err.append(np.std(p))
        x.append(group)

    plt.errorbar(x, y, yerr=err, fmt='o')
    plt.xlabel("Probabilidad de contagio (pc)")
    plt.ylabel("Pendiente de R0")

    # set y axis in scientific notation
    plt.ticklabel_format(axis='y', style='sci', scilimits=(0,0))

    if show:
        plt.show()
    else:
        plt.savefig('graphs/obs1_pc.png')

def obs1_barbijo(show=True):
    df = pd.read_csv('new_infected_barbijo.csv')
    
    y = []
    x = []
    err = []

    for group, data in df.groupby('pc'):

        p = []
        for group_s, data_s in data.groupby('s'):
            # get amount of iterrations
            t = data_s['t'].iloc[-1]
            max_t = t

            # get last new_infected from data
            last_r0 = (data_s['newInfected'].iloc[max_t]/data_s['infected'].iloc[max_t-1])
            first_r0 = (data_s['newInfected'].iloc[1]/data_s['infected'].iloc[0])   
            p.append((last_r0 - first_r0)/max_t)

        y.append(np.mean(p))
        err.append(np.std(p))
        x.append(group)

    plt.errorbar(x, y, yerr=err, fmt='o')
    plt.xlabel("Efectividad del barbijo")
    plt.ylabel("Pendiente de R0")

    # set y axis in scientific notation
    plt.ticklabel_format(axis='y', style='sci', scilimits=(0,0))

    if show:
        plt.show()
    else:
        plt.savefig('graphs/obs1_barbijo.png')

def r0_vs_t():
    df = pd.read_csv('r0.csv')
    t = []
    infected_t = []
    
    for index, row in df.iterrows():
        if index == 0:
            continue
        if df['infected'].iloc[index-1] == 0:
            infected_t.append(0)
        else:
            infected_t.append(row['newInfected']/(df['infected'].iloc[index-1]))
        t.append(row['t'])

    plt.plot(t, infected_t)
    plt.xlabel('Iteración')
    plt.ylabel('R0')
    plt.show()

def obs2_k(show = True):
    df = pd.read_csv('obs2_k.csv')

    y = []
    x = []
    err = []
    for group, data in df.groupby('k'):
        y.append((data['i']/data['s']).mean())
        err.append((data['i']/data['s']).std())
        x.append(group)

    plt.errorbar(x, y, yerr=err, fmt='o')
    plt.xlabel("Proporción de 'cautelosos'")
    plt.ylabel("SAR")
    if show:
        plt.show()
    else:
        plt.savefig('graphs/obs2_k.png')

def obs2_pq(show=True):
    df = pd.read_csv('obs2_pq.csv')

    y = []
    x = []
    err = []
    for group, data in df.groupby('pq'):
        y.append((data['i']/data['s']).mean())
        err.append((data['i']/data['s']).std())
        x.append(group)

    plt.errorbar(x, y, yerr=err, fmt='o')
    plt.xlabel("Probabilidad de transición al estado cuarentena (pq)")
    plt.ylabel("SAR")
    if show:
        plt.show()
    else:
        plt.savefig('graphs/obs2_pq.png')

def obs2_pc(show=True):
    df = pd.read_csv('obs2_pc.csv')

    y = []
    x = []
    err = []
    for group, data in df.groupby('pc'):
        y.append((data['i']/data['s']).mean())
        err.append((data['i']/data['s']).std())
        x.append(group)

    plt.errorbar(x, y, yerr=err, fmt='o')
    plt.xlabel("Probabilidad de contagio (pc)")
    plt.ylabel("SAR")
    if show:
        plt.show()
    else:
        plt.savefig('graphs/obs2_pc.png')

def obs3(show=True):
    df = pd.read_csv('obs3.csv')

    y = []
    x = []
    err = []
    for group, data in df.groupby('p_infected'):
        y.append((data['max_infected']/data['t_max']).mean())
        err.append((data['max_infected']/data['t_max']).std())
        x.append(group)

    plt.errorbar(x, y, yerr=err, fmt='o')
    plt.xlabel("Proporción de infectados iniciales")
    plt.ylabel("Velocidad de contagio")
    if show:
        plt.show()
    else:
        plt.savefig('graphs/obs3.png')

def obs4(show=True):
    df = pd.read_csv('t_vs_k.csv')

    y = []
    x = []
    err = []

    for group, data in df.groupby('k'):
        x.append(group)
        y.append(data['t'].mean())
        err.append(data['t'].std())

        print(group, data['d'].mean(), data['r'].mean())

    plt.errorbar(x, y, yerr=err, fmt='o')
    plt.xlabel("Proporción de 'cautelosos'")
    plt.ylabel("Duración del sistema")
    if show:
        plt.show()
    else:
        plt.savefig('graphs/obs4.png')

def obs5(show=True):
    df = pd.read_csv('t_vs_k.csv')

    y_d = []
    y_r = []
    x = []
    err_d = []
    err_r = []


    for group, data in df.groupby('k'):
        x.append(group)
        y_d.append(data['d'].mean())
        y_r.append(data['r'].mean())
        err_d.append(data['d'].std())
        err_r.append(data['r'].std())

        # print(group, data['d'].mean(), data['r'].mean())

    plt.errorbar(x, y_d, yerr=err_d, fmt='o', label='Muertos')
    plt.errorbar(x, y_r, yerr=err_r, fmt='o', label='Recuperados')
    plt.legend()
    plt.xlabel("Proporción de 'cautelosos'")
    plt.ylabel("Cantidad de estados")
    if show:
        plt.show()
    else:
        plt.savefig('graphs/obs5.png')

# obs2_k(show=False)
# plt.clf()
# obs2_pq(show=False)
# plt.clf()
# obs2_pc(show=False)
# plt.clf()
# obs3(show=False)
obs4(show=False)
# obs5(show=False)

# obs1_pc(show=False)
# obs1_barbijo(show=True)
# obs1()

# r0_vs_t()

