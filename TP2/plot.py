import pandas as pd
import matplotlib.pyplot as plt
import math

rules_2D = ['Rule1112', 'Rule3323', 'Rule3623']
rules_3D = ['Rule2645', 'Rule5556', 'Rule6657']

def graph_rules(rules):
    df = pd.read_csv('stats_by_t_3D.csv')
    df.set_index(['rule'])
    df.set_index(['p'])

    fig, axes = plt.subplots(1, 3)
    fig.subplots_adjust(hspace=0.3, wspace=0.3)

    for i in range(len(rules)):
        ax = axes[i]
        ax.set_xlabel("Iteration")
        ax.set_ylabel("Alive cells")
        ax.set_yscale("log")
        ax.title.set_text(rules[i])

    for r in range(len(rules)):
        selected_data = df.loc[df['rule'] == rules[r]]
        for p, grp in selected_data.groupby(['p']):
            grp.plot(ax=axes[r], kind='line', x='t', y='avgAliveCells', label=p)

    plt.show()

def graph_observable():
    df = pd.read_csv('output.csv')

    fig, ax = plt.subplots(1)
    ax.set_xlabel("Iteration")
    ax.set_ylabel("Pattern radius")

    ts = []
    rs = []
    for t, t_rows in df.groupby(['t']):
        dist = t_rows.apply(lambda row: math.sqrt(row['x']**2+row['y']**2), axis=1)
        ts.append(t)
        rs.append(dist.max())

    ax.plot(ts, rs)
    plt.show()

graph_observable()

# yerr='stdMaxRadius', ecolor='red' to add error bars