import matplotlib.pyplot as plt
import pandas
import os
from os.path import exists

def get_stats_file(minN, stepN, maxN):
    filename = f"stats_{minN}_{stepN}_{maxN}.csv"
    if not exists(filename):
        os.chdir("../src/main/java")
        os.system("javac Statistic.java")
        os.system(f"java Statistic {minN} {stepN} {maxN} ../../../stats/{filename}")
        os.system("del *.class")
    return filename

def plot_N_M_Times():
    df = pandas.read_csv(get_stats_file(0, 1, 5), sep=' ')

    plt.title("Average Time vs N,M")
    ax = plt.axes(projection='3d')
    ax.set_xlabel("N")
    ax.set_ylabel("M")
    ax.set_zlabel("Time (ms)")

    ax.scatter3D(df['N'].tolist(), df['M'].tolist(), df['cimTime'].tolist(), label='CIM')
    ax.scatter3D(df['N'].tolist(), df['M'].tolist(), df['bfTime'].tolist(), label='BF')

def plot_N_M_Comp():
    df = pandas.read_csv(get_stats_file(1, 20, 1), sep=' ')

    colors = []
    for n in range(0, len(df['cimTime'].tolist())):
        colors.append('g' if df['cimTime'].tolist()[n] < df['bfTime'].tolist()[n] else 'r')

    plt.scatter(df['N'].tolist(), df['M'].tolist(), c=colors, label='CIM')

    plt.xlabel('N')
    plt.ylabel('M')

def plot_M_Time(n):
    df = pandas.read_csv(get_stats_file(10, 10, 90), sep=' ')

    df = df[df['N'] == n]
    plt.scatter(df['M'].tolist(), df['cimTime'].tolist(), label='CIM')
    plt.scatter(df['M'].tolist(), df['bfTime'].tolist(), label='BF')

    plt.xlabel('M')
    plt.ylabel('Time (ms)')
    plt.legend(loc='upper left')

plot_N_M_Times()
plt.show()