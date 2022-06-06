from matplotlib import pyplot as plt
import numpy as np
import pandas
from main import m, g, dt, simulate, run
import math

def parse_file(filename):
    print('Reading file...')
    df = pandas.read_csv(filename)
    R, V = [], []

    print('Parsing file...')
    for _, t_df in df.groupby('t'):
        R.append( t_df[['x','y']].to_numpy() )
        V.append( t_df[['vx','vy']].to_numpy() )

    return R, V

def beverloo(N, c, i, x):
    np = N/0.3
    return np * math.sqrt(9.8) * (x[i] - (c * 0.0125))**1.5

def ej1():

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
    
    dt = 2

    avg = list()
    std = list()
    
    for j in range(len(datas)):
        i = 0
        while dt_x[j][i] < dt:
            i += 1
        avg.append(np.mean(dt_q[j][i:]))
        std.append(np.std(dt_q[j][i:]))


    plt.errorbar(data_labels, avg, yerr=std, marker='o')
    plt.ylabel("Promedio del caudal (1/s)")
    plt.xlabel("Apertura (m)")
    plt.show()

    #---------------- FIN CALCULO DE PROMEDIO -----------------

    #---------------- CALCULO DE BEVERLOO -----------------

    ecm = []
    c = []
    min_sum = None
    min_c = 0
    step = 1/100000
    i = 0
    max_i = 5

    while i < max_i:
        c.append(i)
        sum = 0
        for j in range(len(data_labels)):
            sum += (avg[j] - beverloo(300, i, j, data_labels))**2
        sum /= len(data_labels)
        ecm.append(sum)
        if min_sum == None or sum < min_sum:
            min_sum = sum
            min_c = i
        i+=step

    plt.plot(c, ecm)
    plt.scatter([min_c], [min_sum])
    plt.xlabel("Valor constante C")
    plt.ylabel("ECM")
    plt.show()


    plt.errorbar(data_labels, avg, label="Datos Obtenidos", yerr=std, marker='o')

    beverloo_values = [beverloo(300, min_c, i, data_labels) for i in range(len(data_labels))]

    plt.plot(data_labels, beverloo_values, label="Beverloo")
    plt.ylabel("Caudal (1/s)")
    plt.xlabel("Apertura (m)")
    plt.legend()
    plt.show()

    #---------------- FIN CALCULO DE BEVERLOO -----------------

def ej3():
    df1 = pandas.read_csv('ej3_0.15.csv')
    df2 = pandas.read_csv('ej3_0.18.csv')
    df3 = pandas.read_csv('ej3_0.21.csv')
    df4 = pandas.read_csv('ej3_0.24.csv')

    datas = [df1, df2, df3, df4]
    data_labels = [0.15, 0.18, 0.21, 0.24]

    #---------------- CALCULO DE Ek -----------------

    for i in range(len(datas)):
        plt.plot(df1['Dt'], datas[i]['KE'], label = "Ap = "+str(data_labels[i])+" m")
    plt.xlabel("t (s)")
    plt.ylabel("Ek (J)")
    plt.semilogy()
    plt.legend()
    plt.show()

def ej4():
    df1 = pandas.read_csv('ej4_1.csv')
    df2 = pandas.read_csv('ej4_2.csv')
    df3 = pandas.read_csv('ej4_3.csv')

    datas = [df1, df2, df3]

    #---------------- CALCULO DE Ek en base a Kn -----------------

    for i in range(len(datas)):
        plt.plot(datas[i]['Dt'], datas[i]['KE'], label = "Kt = "+str(i+1)+"Kn N/m")

    plt.xlabel("t (s)")
    plt.ylabel("Ek (J)")
    plt.semilogy()
    plt.legend()
    plt.show()

    #---------------- FIN CALCULO DE Ek en base a Kn -----------------

    #---------------- CALCULO DE Ek en base a Kt -----------------

    t_avg = [2.2, 7, 3.3]
    avg = []
    std = []
    labels = ["Kn", "2Kn", "3Kn"]

    # get average of each Kt where 'Dt' is greater than t_avg
    for i in range(len(datas)):

        values = []
        t = 0
        while t < len(datas[i]['Dt']):
            if datas[i]['Dt'][t] > t_avg[i]:
                values = datas[i]['KE'][t:]
                break
            t+=1

        print(t)

        avg.append(np.mean(values))
        std.append(np.std(values))

        print(avg)

    # show errorbar without line
    plt.errorbar(labels, avg, yerr=std, marker='o', ls='none')


    # plt.errorbar(labels, avg, yerr=std)
    plt.xlabel("Kt")
    plt.ylabel("Ek (J)")
    plt.show()





# kinetic_energy_plot('out.csv')
# ej1()
# ej3()
ej4()
# ej1_file_generation()